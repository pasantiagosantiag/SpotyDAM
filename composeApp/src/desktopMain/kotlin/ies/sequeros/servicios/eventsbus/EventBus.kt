package ies.sequeros.servicios.eventsbus

import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.bson.types.ObjectId

class EventBus {
    private val _events = MutableSharedFlow<DomainEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DomainEvent> = _events

    suspend fun sendEvent(event: DomainEvent) {
        _events.emit(event)
    }
}

sealed class DomainEvent {
    data class UserDeleted(val user: Usuario) : DomainEvent()
    data class UsuarioDeletedById(val id: ObjectId):DomainEvent()
    data class ListaDeleted(val lista: Lista):DomainEvent()
    data class ListaAdded(val lista: Lista): DomainEvent()
    data class ListaUpdated(val lista: Lista): DomainEvent()
    data class CancionDeleted(val cancion: Cancion):DomainEvent()
    data class CancionAdded(val cancion: Cancion):DomainEvent()
    data class CancionUpdated(val cancion: Cancion):DomainEvent()
    data class CancionDeletedById(val id: ObjectId):DomainEvent()

}