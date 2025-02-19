package ies.sequeros.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.modelo.dto.CancionListaDTO
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.servicios.listas.ListaService

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListaViewModel (private val listaService: ListaService) : ViewModel() {
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
    }
    fun unSelect(){
        _selected.value = ListaDTO()
    }

    fun setSelected(item: ListaDTO) {
        _selected.value=item

    }
    fun addCancion(cancion: Cancion, lista:ListaDTO){
        var cancionDTO=CancionListaDTO(cancion._id,cancion.titulo,cancion.artista,cancion.duracion)
        if(!lista.canciones.contains(cancionDTO)){
            lista.canciones.add(cancionDTO)
            save(lista)
        }
    }
    fun save(item: ListaDTO) {

        viewModelScope.launch {
            listaService.save(item)
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
    fun remove(item: ListaDTO) {
        viewModelScope.launch {
            listaService.remove(item)
            _items.value = _items.value.filter { it != item }.toMutableList()
        }
    }
}