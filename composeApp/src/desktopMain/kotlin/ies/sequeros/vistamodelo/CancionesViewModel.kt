package ies.sequeros.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.servicios.canciones.CancionService
import ies.sequeros.servicios.listas.ListaService

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CancionesViewModel (private val cancionService: CancionService) : ViewModel() {
    private val _selected = MutableStateFlow(Cancion())
    private var _items = MutableStateFlow<MutableList<Cancion>>(mutableListOf())

    val selected: StateFlow<Cancion> = _selected
    //para el buscador
    var buscadortitulo = MutableStateFlow("")

    val items = combine(_items, buscadortitulo) { items, titulo ->
        items.filter {
            if (titulo.isBlank())
                true
            else {
                it.titulo.lowercase().contains(titulo.lowercase())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = _items.value
    )

    init {
        viewModelScope.launch {
            _items.value = cancionService.getAll().toMutableList()
        }
    }
    fun unSelect(){
        _selected.value = Cancion()
    }

    fun setSelected(item: Cancion) {
        _selected.value=item

    }

    fun save(item: Cancion) {

        viewModelScope.launch {
            cancionService.save(item)
            var nueva = _items.value.toMutableList()

            //existe en el listado, es una actulizacion
            if(_items.value.firstOrNull { it._id.toString() == item._id.toString() } != null) {
                //se sustituye el elementos
                var index= nueva.indexOfFirst {
                    it._id!!.equals(item._id) }
                if(index!=-1) {
                    nueva[index] = (item)
                }

            }else {
                //es un nuevo elemento
                nueva.add(item)
            }
            _items.value = mutableListOf()
            _items.value =nueva.toMutableList()

        }
    }
    fun remove(item: Cancion) {
        viewModelScope.launch {
            cancionService.remove(item)
            _items.value = _items.value.filter { it != item }.toMutableList()
        }
    }
}