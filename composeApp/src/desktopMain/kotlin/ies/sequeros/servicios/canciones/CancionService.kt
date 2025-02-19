package ies.sequeros.servicios.canciones

import ies.sequeros.modelo.entidades.Cancion

import ies.sequeros.modelo.repositorios.ACancionRepositorio
import ies.sequeros.modelo.repositorios.AListasRepositorio
import ies.sequeros.servicios.eventsbus.DomainEvent
import ies.sequeros.servicios.eventsbus.EventBus

import org.bson.types.ObjectId

class CancionService(
    private val listaRepositorio: AListasRepositorio,
    private val cancionRepositorio: ACancionRepositorio,
    private val eventBus: EventBus,
) {
    suspend fun add(item: Cancion) {
        cancionRepositorio.save(item)
        item._id = item._id
        eventBus.sendEvent(DomainEvent.CancionAdded(item))
    }

    suspend fun remove(item: Cancion) {
        //se borra en todas las listas la canciones en que aparezca
        var listas = listaRepositorio.getAll().filter { e ->
            e.canciones.firstOrNull { it == item._id } != null
        }.forEach { lista ->
            run {
                lista.canciones.removeIf { it == item._id }
                listaRepositorio.save(lista)
            }
        }
        cancionRepositorio.remove(item)
        eventBus.sendEvent(DomainEvent.CancionDeleted(item))
    }


    suspend fun removeById(id: ObjectId) {
        cancionRepositorio.removeById(id)
        eventBus.sendEvent(DomainEvent.CancionDeletedById(id))

    }
    suspend fun save(item: Cancion) {
    if(item._id==null) {
        cancionRepositorio.save(item)
        eventBus.sendEvent(DomainEvent.CancionDeleted(item))
    }else{
        cancionRepositorio.save(item)
        eventBus.sendEvent(DomainEvent.CancionUpdated(item))}


    }
    suspend fun getAll(): List<Cancion> {
        return cancionRepositorio.getAll()

    }
}