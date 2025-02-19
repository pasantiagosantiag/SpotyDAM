package ies.sequeros.modelo.repositorios.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ies.sequeros.modelo.entidades.Cancion

import ies.sequeros.modelo.repositorios.ACancionRepositorio


import ies.sequeros.modelo.repositorios.MongoConnection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MongoCancionRepositorio(private val mongoConnection: MongoConnection) : ACancionRepositorio() {
    private val namedatabase = "damplaymusic"
    private val colectionname = "canciones"
    override suspend fun getAll(): List<Cancion> {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val doc = collection.find()
            return doc.toList()
        }
        return emptyList()
    }

    override suspend fun remove(item: Cancion) {
        item._id?.let { this.removeById(it) }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun removeById(id: ObjectId) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val query = Filters.eq("_id", id)

           var r= collection.deleteOne(query)

        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getById(id: ObjectId): Cancion? {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val query = Filters.eq("_id", id)
            var resultado= collection.find(query) //"{nombre:'Juan'}" )
            return resultado.firstOrNull()
        }
        return null
    }

    override suspend fun update(item: Cancion) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val query = Filters.eq("_id", item._id)
            var bson= Updates.combine(
                Updates.set("anyo",item.anyo),
                Updates.set("titulo",item.titulo),
                Updates.set("letra",item.letra),
                Updates.set("artista",item.artista),
                Updates.set("duracion",item.duracion)

            )
            collection.findOneAndUpdate(query,bson) //"{nombre:'Juan'}" )

        }

    }

    @ExperimentalUuidApi
    override suspend fun updateById(item: Cancion, id: ObjectId) {
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val query = Filters.eq("_id", id)
            var bson= Updates.combine(
                Updates.set("anyo",item.anyo),
                Updates.set("titulo",item.titulo),
                Updates.set("letra",item.letra),
                Updates.set("artista",item.artista),
                Updates.set("duracion",item.duracion)

            )
            collection.findOneAndUpdate(query,bson)

        }
    }

    override suspend fun add(item: Cancion) {
        if (!mongoConnection.isOpen()) {
            mongoConnection.conect()
        }
        val db = mongoConnection.getDatabase(namedatabase)
        db?.let {
            val collection = it.getCollection<Cancion>(colectionname)
            val doc = collection.insertOne(item)

        }

    }

    override suspend fun save(item: Cancion) {

       if(item._id==null) {
           //se le pone un id nuevo
           item._id=ObjectId()
           this.add(item)
       }else
           this.update(item)
    }


}