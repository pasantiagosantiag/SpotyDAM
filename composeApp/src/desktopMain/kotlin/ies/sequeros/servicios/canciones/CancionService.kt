package ies.sequeros.servicios.canciones

import ies.sequeros.modelo.entidades.Cancion

import ies.sequeros.modelo.repositorios.ACancionRepositorio
import ies.sequeros.modelo.repositorios.AListasRepositorio

import org.bson.types.ObjectId

class CancionService( private val listaRepositorio: AListasRepositorio, private val cancionRepositorio:ACancionRepositorio) {
    suspend fun add(item: Cancion) {
        cancionRepositorio.save(item)
        item._id = item._id

    }

    suspend fun remove(item: Cancion) {
        //se elimina del usuario
       /* var usuario = usuarioRepositorio.getById(item.usuario._id)
        usuario?.let {
            it.items.remove(item._id)
            usuarioRepositorio.update(it)

        }*/
        //se elimina de la item
        cancionRepositorio.remove(item)

    }

    suspend fun addCancion( item: Cancion) {

        cancionRepositorio.save(item)
    }

    suspend fun removeById(id: ObjectId) {

        cancionRepositorio.removeById(id)

    }

    suspend fun save(item: Cancion) {

        cancionRepositorio.save(item)


    }



    suspend fun getAll(): List<Cancion> {
        return  cancionRepositorio.getAll()

    }
}