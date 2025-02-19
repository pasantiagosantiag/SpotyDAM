package ies.sequeros.servicios.usuarios

import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.dto.ListaUsuarioDTO
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.modelo.repositorios.AListasRepositorio
import ies.sequeros.modelo.repositorios.AUsuarioRepositorio
import ies.sequeros.modelo.repositorios.mongo.MongoListaRepositorio
import ies.sequeros.modelo.repositorios.mongo.MongoUsuarioRepositorio
import ies.sequeros.servicios.eventsbus.DomainEvent
import ies.sequeros.servicios.eventsbus.EventBus
import org.bson.BsonArray
import org.bson.BsonBinary
import org.bson.BsonDocument
import org.bson.BsonNumber
import org.bson.BsonObjectId
import org.bson.BsonString
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.litote.kmongo.MongoOperator

class UsuariosService(val usuarioRepositorio: AUsuarioRepositorio,
                      val listasRepositorio: AListasRepositorio,
    val eventBus: EventBus
) {
    private val conAgregados=true
    private fun UsuarioToUsuarioDTO(usuario: Usuario,lista:List<Lista>): UsuarioDTO {
        var usuarioDTO = UsuarioDTO()
        usuarioDTO._id=usuario._id;
        usuarioDTO.nombre=usuario.nombre;
        usuarioDTO.password=usuario.password;
        usuarioDTO.avatar=usuario.avatar;
        usuarioDTO.fechaalta=usuario.fechaalta;
        usuarioDTO.ultimaconexion=usuario.ultimaconexion
        lista.forEach {
            var listaUsuarioDTO = ListaUsuarioDTO()
            listaUsuarioDTO._id=it._id
            listaUsuarioDTO.nombre=it.nombre
            listaUsuarioDTO.portada=it.portada
        }
        return usuarioDTO
    }
    private fun UsuarioDTOToUsuario(usuarioDTO: Usuario): Usuario {
        var usuario = Usuario()
        usuario._id=usuarioDTO._id;
        usuario.nombre=usuarioDTO.nombre;
        usuario.password=usuarioDTO.password;
        usuario.avatar=usuarioDTO.avatar;
        usuario.fechaalta=usuarioDTO.fechaalta;
        usuario.ultimaconexion=usuarioDTO.ultimaconexion
        usuarioDTO.listas.forEach {
            usuario.listas.add(it)
        }
        return usuario
    }
    suspend fun register(usuario: Usuario) {
        usuarioRepositorio.save(usuario)
    }

    suspend fun getAll(): List<UsuarioDTO> {
        var usuariosDTO = mutableListOf<UsuarioDTO>()
        if(!conAgregados) {
            var usuarios = usuarioRepositorio.getAll()

            usuarios.forEach { usuario ->
                run {
                    var listas = listasRepositorio.getByUsuario(usuario)
                    usuariosDTO.add(this.UsuarioToUsuarioDTO(usuario, listas))
                }
            }
        }
        else {
            //se obtiene directamente de la base de datos con agregados
            usuariosDTO= usuarioRepositorio.getAllUsuarioMongo().toMutableList()
        }
        return usuariosDTO

    }
    suspend fun getById(id: ObjectId): UsuarioDTO? {
        var usuario = usuarioRepositorio.getById(id)
        if(usuario!=null){
            var listas=listasRepositorio.getByUsuario(usuario)
            return UsuarioToUsuarioDTO(usuario,listas)
        }
        return null

    }
    suspend fun login(username: String, password: String): Usuario {
        return  Usuario();
    }
    suspend fun remove(usuario: Usuario) {
        usuarioRepositorio.remove(usuario)
        eventBus.sendEvent(DomainEvent.UserDeleted(usuario))
       
    }
    suspend fun removeById(id: ObjectId) {
        usuarioRepositorio.removeById(id)
        listasRepositorio.removeByUsuarioId(id)
        eventBus.sendEvent(DomainEvent.UsuarioDeletedById(id))

    }
    suspend fun save(usuario: Usuario):UsuarioDTO {
        usuarioRepositorio.save(usuario)
        return UsuarioToUsuarioDTO(usuario, emptyList())
    }
    suspend fun addLista(lista: Lista, usuarioid: ObjectId) {
        var usuario=usuarioRepositorio.getById(usuarioid)
        if (usuario != null) {
            //solo se inserta sino esta en la lista
            if(usuario.listas.firstOrNull { it==lista._id }==null){
            usuario.listas.add(lista._id!!)
            usuarioRepositorio.save(usuario)
            }
        }

    }
    suspend fun removeLista(lista: Lista, usuarioid: ObjectId) {
        var usuario=usuarioRepositorio.getById(usuarioid)
        if (usuario != null) {
            usuario.listas.remove((lista._id))
            usuarioRepositorio.save(usuario)
        }

    }
    suspend fun removeLista(listaid: ObjectId, usuarioid: ObjectId) {
        var usuario=usuarioRepositorio.getById(usuarioid)
        if (usuario != null) {
            usuario.listas.remove((listaid))
            usuarioRepositorio.save(usuario)
        }

    }
}