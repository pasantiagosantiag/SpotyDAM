package ies.sequeros.modelo.repositorios.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.modelo.repositorios.AUsuarioRepositorio
import ies.sequeros.modelo.repositorios.MongoConnection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MongoUsuarioRepositorio(private val mongoConnection: MongoConnection) : AUsuarioRepositorio() {
    private val namedatabase = "damplaymusic"
    private val colectionname = "usuarios"
    override suspend fun getAll(): List<Usuario> {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            val doc = collection.find()
            return  doc.toList()
        }
        return emptyList()
    }

    override suspend fun remove(item: Usuario) {
        this.removeById(item._id)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun removeById(id: ObjectId) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            val query = Filters.eq("_id", id)

           var r= collection.deleteOne(query)

        }
    }



    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: ObjectId): Usuario? {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            val query = Filters.eq("_id", id)
            var resultado= collection.find(query) //"{nombre:'Juan'}" )
            return resultado.firstOrNull()
        }
        return null
    }

    override suspend fun update(item: Usuario) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            val query = Filters.eq("_id", item._id)
            var bson= Updates.combine(
                Updates.set("nombre",item.nombre),
                Updates.set("password",item.password),
                Updates.set("fechaalta",item.fechaalta),
                Updates.set("ultimaconexion",item.ultimaconexion),
                Updates.set("avatar",item.avatar),
                Updates.set("listas", item.listas)

            )
            collection.findOneAndUpdate(query,bson) //"{nombre:'Juan'}" )

        }

    }

    @ExperimentalUuidApi
    override suspend fun updateById(item: Usuario, id: ObjectId) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)
            val query = Filters.eq("_id", id)
            var bson= Updates.combine(
                Updates.set("nombre",item.nombre),
                Updates.set("password",item.password),
                Updates.set("fechaalta",item.fechaalta),
                Updates.set("ultimaconexion",item.ultimaconexion),
                Updates.set("avatar",item.avatar),
                Updates.set("listas",item.listas)

            )
            collection.findOneAndUpdate(query,bson)

        }
    }

    override suspend fun add(item: Usuario) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }

        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Usuario>(colectionname)

            val doc = collection.insertOne(item)

        }

    }

    override suspend fun save(item: Usuario) {
       if(item._id.toString()==Usuario.nuevo) {
           //se le pone un id nuevo
           item._id=ObjectId()
           this.add(item)
       }else
           this.update(item)
    }


}