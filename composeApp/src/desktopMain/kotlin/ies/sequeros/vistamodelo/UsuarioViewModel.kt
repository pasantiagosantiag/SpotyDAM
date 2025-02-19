package ies.sequeros.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.modelo.dto.ListaUsuarioDTO
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.servicios.eventsbus.DomainEvent
import ies.sequeros.servicios.eventsbus.EventBus
import ies.sequeros.servicios.usuarios.UsuariosService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UsuarioViewModel(private val usuariosService: UsuariosService, private val eventBus: EventBus) : ViewModel() {
    private var _selected = MutableStateFlow(UsuarioDTO())
    private var _items = MutableStateFlow<MutableList<UsuarioDTO>>(mutableListOf())

    val selected: StateFlow<UsuarioDTO> = _selected

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
            _items.value = usuariosService.getAll().toMutableList()
        }
        viewModelScope.launch {
            eventBus.events.collect { event ->
                when (event) {
                    is DomainEvent.CancionAdded -> {

                    }
                    is DomainEvent.CancionDeleted -> {
                                            }
                    is DomainEvent.CancionDeletedById -> {}
                    is DomainEvent.ListaDeleted -> {
                        _items.value.firstOrNull {
                            //se añade al usuario
                            it._id.toString() == event.lista.usuario.toString()
                        }?.let { item ->
                            item.listas.removeIf {
                                it._id.toString() == event.lista._id.toString()
                            }
                        }
                    }

                    is DomainEvent.ListaAdded -> {
                        _items.value.firstOrNull {
                            //se añade al usuario
                            it._id.toString() == event.lista.usuario.toString()
                        }?.let { item ->
                            var listadto = ListaUsuarioDTO()
                            listadto._id = event.lista._id
                            listadto.nombre = event.lista.nombre
                            listadto.portada = event.lista.portada
                            item.listas.add(listadto)
                        }
                    }

                    is DomainEvent.ListaUpdated -> _items.value.firstOrNull {
                        //se añade al usuario
                        it._id.toString() == event.lista.usuario.toString()
                    }?.let { item ->
                        var listadto = ListaUsuarioDTO()
                        listadto._id = event.lista._id
                        listadto.nombre = event.lista.nombre
                        listadto.portada = event.lista.portada
                        //se reemplaza
                        item.listas.replaceAll {
                            if (it._id.toString() == event.lista._id.toString())
                                listadto
                            else
                                it
                        }

                    }

                    is DomainEvent.UserDeleted -> {

                    }

                    is DomainEvent.CancionUpdated -> {

                    }

                    is DomainEvent.UsuarioDeletedById -> {

                    }
                }
            }


        }

    }

    fun unSelect() {
        _selected.value = UsuarioDTO()
        // _selected= MutableStateFlow(UsuarioDTO())
    }

    fun setSelected(usuario: UsuarioDTO) {
        _selected.value = usuario

    }

    fun save(item: Usuario) {

        viewModelScope.launch {
            var itemDTO = usuariosService.save(item)
            var nueva = _items.value.toMutableList()

            //existe en el listado, es una actulizacion
            if (_items.value.firstOrNull { it._id.toString() == item._id.toString() } != null) {
                //se sustituye el elementos
                var index = nueva.indexOfFirst {
                    it._id!!.equals(item._id)
                }
                if (index != -1) {
                    nueva[index] = (itemDTO)
                }

            } else {

                //es un nuevo elemento
                nueva.add(itemDTO)
            }
            //esto no debería ser necesario pero no termina de funcionar bien
            _items.value = mutableListOf()
            _items.value = nueva.toMutableList()


        }
    }

    fun remove(item: UsuarioDTO) {
        viewModelScope.launch {
            usuariosService.removeById(item._id!!)
            _items.value = _items.value.filter { it != item }.toMutableList()
        }
    }
}