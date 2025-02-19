package ies.sequeros.modelo.repositorios.mongo

import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.modelo.repositorios.AListasRepositorio

import ies.sequeros.modelo.repositorios.MongoConnection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MongoListaRepositorio(private val mongoConnection: MongoConnection) : AListasRepositorio() {
    private val namedatabase = "damplaymusic"
    private val colectionname = "listas"
    override suspend fun getByUsuario(usuario: Usuario): List<Lista> {
      return getByUsuario(usuario._id)
    }

    override suspend fun getByUsuario(id: ObjectId): List<Lista> {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("usuario", id)

            return  collection.find(query).toList()

        }
        return listOf()
    }
    override suspend fun removeByUsuario(usuario: Usuario){
        this.removeByUsuarioId(usuario._id)

    }
    override suspend fun removeByUsuarioId(id: ObjectId) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("usuario", id)

             collection.deleteMany(query)

        }

    }
    override suspend fun getAllMongo(): List<ListaDTO> {
          if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            var elementos = collection.aggregate<UsuarioDTO>(
                listOf(
                    //final String from, final String localField, final String foreignField, final String as
                    Aggregates.lookup("canciones", "canciones", "_id", "canciones"),
                    Aggregates.project(
                        Projections.fields(
                            Projections.include(
                                Lista::_id.name,
                                Lista::nombre.name,
                                Lista::usuario.name,
                                Lista::fechacreacion.name,
                                Lista::comentario.name,
                                Lista::portada.name,
                                "usuario._id",
                                "usuario.nombre",
                                "canciones.id",
                                "canciones.artista",
                                "canciones.duracion"
                            ),
                            // Projections.excludeId()
                        )
                    )

                )
            ).toList()
        }
   return emptyList()
    }

    override suspend fun getAll(): List<Lista> {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val doc = collection.find()
            return doc.toList()
        }
        return emptyList()
    }

    override suspend fun remove(item: Lista) {
        item._id?.let { this.removeById(it) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun removeById(id: ObjectId) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", id)
           var r= collection.deleteOne(query)

        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: ObjectId): Lista? {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", id)
            var resultado= collection.find(query) //"{nombre:'Juan'}" )
            return resultado.firstOrNull()
        }
        return null
    }

    override suspend fun update(item: Lista) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", item._id)
            var bson= Updates.combine(
                Updates.set("nombre",item.nombre),
                Updates.set("comentario",item.comentario),
                Updates.set("fechaalta",item.fechacreacion),
                Updates.set("portada",item.portada),
                Updates.set("canciones",item.canciones)
            )
            collection.findOneAndUpdate(query,bson) //"{nombre:'Juan'}" )

        }

    }

    @ExperimentalUuidApi
    override suspend fun updateById(item: Lista, id: ObjectId) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Lista>(colectionname)
            val query = Filters.eq("_id", id)
            var bson= Updates.combine(
                Updates.set("nombre",item.nombre),
                Updates.set("comentario",item.comentario),
                Updates.set("fechaalta",item.fechacreacion),
                Updates.set("portada",item.portada),
                Updates.set("usuario",item.usuario),
                Updates.set("canciones",item.canciones)

            )
            collection.findOneAndUpdate(query,bson)

        }
    }

    override suspend fun add(item: Lista) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {

            val collection = it.getCollection<Lista>(colectionname)
            val doc = collection.insertOne(item)

        }

    }

    override suspend fun save(item: Lista) {

       if(item._id==null) {
           //se le pone un id nuevo
           item._id=ObjectId()
           this.add(item)
       }else
           this.update(item)
    }


}