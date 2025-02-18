package ies.sequeros.servicios.listas

import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.dto.UsuarioListaDTO
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.modelo.repositorios.ACancionRepositorio
import ies.sequeros.modelo.repositorios.AListasRepositorio
import ies.sequeros.modelo.repositorios.AUsuarioRepositorio
import ies.sequeros.modelo.repositorios.mongo.MongoCancionRepositorio
import org.bson.types.ObjectId

class ListaService(
    private val usuarioRepositorio: AUsuarioRepositorio,
    private val listaRepositorio: AListasRepositorio,
    private val cancionRepositorio: ACancionRepositorio
) {

    private fun itemToDTO(item: Lista, usuario: Usuario,canciones:List<Cancion>): ListaDTO {
        var DTO = ListaDTO()
        var usuarioDTO = UsuarioListaDTO()
        DTO.portada = item.portada
        DTO._id = item._id
        DTO.nombre = item.nombre
        DTO.comentario = item.comentario
        DTO.fechacreacion = item.fechacreacion

        usuarioDTO._id = usuario._id;
        usuarioDTO.nombre = usuario.nombre
        DTO.usuario = usuarioDTO
        item.canciones.forEach { c ->
            run {
                var cancion = canciones.firstOrNull{ it._id == c }
                if(cancion!=null){
                    DTO.canciones.add(cancion)
                }
            }
        }
        return DTO
    }

    private fun DTOToItem(DTO: ListaDTO): Lista {
        var item = Lista()
        item._id = DTO._id;
        item.usuario = DTO.usuario._id;
        item.comentario = DTO.comentario;
        item.portada = DTO.portada

        item.fechacreacion = DTO.fechacreacion
        item.nombre = DTO.nombre
        DTO.canciones.forEach{
            item.canciones.add(it._id)
        }
        return item
    }

    suspend fun add(lista: ListaDTO) {
        var item = DTOToItem(lista)
        listaRepositorio.save(item)
        lista._id = item._id

    }

    suspend fun remove(lista: ListaDTO) {
        var item = DTOToItem(lista)
        //se elimina del usuario
        var usuario = usuarioRepositorio.getById(lista.usuario._id)
        usuario?.let {
            it.listas.remove(lista._id)
            usuarioRepositorio.update(it)

        }
        //se elimina de la lista
        listaRepositorio.remove(item)

    }

    suspend fun addCancion(lista: Lista, cancion: Cancion) {
        lista.canciones.remove(cancion._id)
        listaRepositorio.save(lista)
    }

    suspend fun removeById(id: ObjectId) {

        listaRepositorio.removeById(id)

    }

    suspend fun save(item: ListaDTO) {
        var item2 = DTOToItem(item)
        item2.usuario = item.usuario._id
        listaRepositorio.save(item2)
        item._id = item2._id
        //si la lista es nueva no tiene usuario asociado
        var usuario = usuarioRepositorio.getById(item.usuario._id)
        usuario?.let {
            //es nuevo
            if (usuario.listas.firstOrNull { it == item._id } == null) {
                it.listas.add(item._id)
                usuarioRepositorio.update(it)
            }

        }

    }

    suspend fun save(item: ListaDTO, usuario: UsuarioDTO) {
        var item2 = DTOToItem(item)
        item2.usuario = usuario._id
        listaRepositorio.save(item2)
        item._id = item2._id
        //si la lista es nueva no tiene usuario asociado
        var usuario = usuarioRepositorio.getById(item.usuario._id)
        usuario?.let {
            //es nuevo
            if (usuario.listas.firstOrNull { it == item._id } == null) {
                it.listas.add(item._id)
                usuarioRepositorio.update(it)
            }

        }
    }

    suspend fun getAll(): List<ListaDTO> {
        var items = listaRepositorio.getAll()
        var canciones=cancionRepositorio.getAll()
        var itemsDTO = mutableListOf<ListaDTO>()
        items.forEach { item ->
            run {
                //se puede mejorar, no ir a buscarlo cada usuario, sino
                //almacenar y buscar primero en "cache"
                var usuario = usuarioRepositorio.getById(item.usuario)
                if (usuario != null)
                    itemsDTO.add(this.itemToDTO(item, usuario,canciones))
            }
        }
        return itemsDTO
    }
}