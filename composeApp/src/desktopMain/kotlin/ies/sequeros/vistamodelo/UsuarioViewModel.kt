package ies.sequeros.vistamodelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.servicios.usuarios.UsuariosService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UsuarioViewModel (private val usuariosService: UsuariosService) : ViewModel() {
    private var _selected = MutableStateFlow(UsuarioDTO())
    private var _usuarios = MutableStateFlow<MutableList<UsuarioDTO>>(mutableListOf())

    val selected: StateFlow<UsuarioDTO> = _selected
    //para el buscador
    var buscadornombre = MutableStateFlow("")

    val items = combine(_usuarios, buscadornombre) { items, nombre ->
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
        initialValue = _usuarios.value
    )

    init {
        viewModelScope.launch {
            _usuarios.value = usuariosService.getAll().toMutableList()
        }
    }
    fun unSelect(){
        _selected.value = UsuarioDTO()
       // _selected= MutableStateFlow(UsuarioDTO())
    }

    fun setSelected(usuario: UsuarioDTO) {
        _selected.value=usuario

    }
    fun save(item: Usuario) {

        viewModelScope.launch {
            var itemDTO=usuariosService.save(item)
            var nueva = _usuarios.value.toMutableList()

            //existe en el listado, es una actulizacion
            if(_usuarios.value.firstOrNull { it._id.toString() == item._id.toString() } != null) {
                //se sustituye el elementos
                var index= nueva.indexOfFirst {
                    it._id.equals(item._id) }
                if(index!=-1) {
                    nueva[index] = (itemDTO)
                }

            }else {

               //es un nuevo elemento
                nueva.add(itemDTO)
            }
            //esto no debería ser necesario pero no termina de funcionar bien
            _usuarios.value = mutableListOf()
            _usuarios.value =nueva.toMutableList()


        }
    }
    fun remove(item: UsuarioDTO) {
        viewModelScope.launch {
            usuariosService.removeById(item._id)
            _usuarios.value = _usuarios.value.filter { it != item }.toMutableList()
        }
    }
}