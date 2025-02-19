package ies.sequeros.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.modelo.dto.CancionListaDTO
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.servicios.eventsbus.DomainEvent
import ies.sequeros.servicios.eventsbus.EventBus
import ies.sequeros.servicios.listas.ListaService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListaViewModel(
    private val listaService: ListaService,
    private val eventBus: EventBus
) : ViewModel() {
    private val _selected = MutableStateFlow(ListaDTO())
    private var _items = MutableStateFlow<MutableList<ListaDTO>>(mutableListOf())

    val selected: StateFlow<ListaDTO> = _selected

    //para el buscador
    var buscadornombre = MutableStateFlow("")
    val items = combine(_items, buscadornombre) { items, nombre ->
        items.filter {
            if (nombre.isBlank())
                true
            else {
                it.nombre.lowercase().contains(nombre.lowercase())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = _items.value
    )

    init {
        viewModelScope.launch {
            _items.value = listaService.getAll().toMutableList()
        }
        viewModelScope.launch {
            eventBus.events.collect { event ->
                when (event) {
                    is DomainEvent.CancionAdded -> {
                        _items.value.forEach {
                            it.canciones.forEach {
                                if (it._id.toString() == event.cancion._id.toString()) {
                                    it.titulo = event.cancion.titulo
                                    it.artista = event.cancion.artista
                                    it.duracion = event.cancion.duracion
                                }
                            }
                        }
                    }

                    is DomainEvent.CancionDeleted -> {
                        _items.value.forEach {
                            it.canciones.removeIf {
                                it._id == event.cancion._id
                            }
                        }
                    }

                    is DomainEvent.CancionDeletedById -> {
                        _items.value.forEach {
                            it.canciones.removeIf {
                                it._id == event.id
                            }
                        }
                    }

                    is DomainEvent.CancionUpdated ->
                        _items.value.forEach {
                            it.canciones.forEach {
                                if (it._id == event.cancion._id) {
                                    it.titulo = event.cancion.titulo
                                    it.artista = event.cancion.artista
                                    it.duracion = event.cancion.duracion

                                }

                            }
                        }

                    is DomainEvent.ListaAdded -> {}
                    is DomainEvent.ListaDeleted -> {

                    }

                    is DomainEvent.ListaUpdated -> {}
                    is DomainEvent.UserDeleted -> {
                        _items.value.removeIf { it.usuario._id == event.user._id }
                    }

                    is DomainEvent.UsuarioDeletedById -> {
                        var nueva = _items.value.toMutableList()

                        nueva.removeIf { it.usuario._id == event.id }
                        _items.value = mutableListOf()
                        _items.value = nueva.toMutableList()
                        println(_items.value.size)
                    }
                }


            }
        }
    }

    fun unSelect() {
        _selected.value = ListaDTO()
        this.eventBus
    }

    fun setSelected(item: ListaDTO) {
        _selected.value = item

    }

    fun addCancion(cancion: Cancion, lista: ListaDTO) {
        var cancionDTO = CancionListaDTO(cancion._id, cancion.titulo, cancion.artista, cancion.duracion)
        if (!lista.canciones.contains(cancionDTO)) {
            lista.canciones.add(cancionDTO)
            save(lista)

        }
    }

    fun save(item: ListaDTO) {

        viewModelScope.launch {
            listaService.save(item)
            var nueva = _items.value.toMutableList()

            //existe en el listado, es una actulizacion
            if (_items.value.firstOrNull { it._id.toString() == item._id.toString() } != null) {
                //se sustituye el elementos
                var index = nueva.indexOfFirst {
                    it._id!!.equals(item._id)
                }
                if (index != -1) {
                    nueva[index] = (item)
                }

            } else {
                //es un nuevo elemento
                nueva.add(item)
            }
            _items.value = mutableListOf()
            _items.value = nueva.toMutableList()

        }
    }

    fun remove(item: ListaDTO) {
        viewModelScope.launch {
            listaService.remove(item)
            _items.value = _items.value.filter { it != item }.toMutableList()
        }
    }
}