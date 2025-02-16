package ies.sequeros.modelo.repositorios.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
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
        this.removeById(item._id)
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
                Updates.set("portada",item.portada)
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
                Updates.set("usuario",item.usuario)

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
        println(item._id.toString())
       if(item._id.toString()==Lista.nuevo) {
           //se le pone un id nuevo
           item._id=ObjectId()
           this.add(item)
       }else
           this.update(item)
    }


}