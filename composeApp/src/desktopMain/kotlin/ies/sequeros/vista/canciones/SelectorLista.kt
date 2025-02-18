package ies.sequeros.vista.canciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.vistamodelo.ListaViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SelectorLista(
    listavm: ListaViewModel,
    selected: StateFlow<Cancion>,
    onSelect: (ListaDTO)->Unit,

    ) {
    var listas = listavm.items.value
    listas.forEach {
        //solo se muestran las listas en los que no esta la canción
        if (it.canciones.firstOrNull { it._id == selected.value._id } == null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("${it.nombre} ${it._id}", modifier = Modifier.clickable { }.weight(0.40f))

                Button(onClick = {
                    onSelect(it)
                }, modifier = Modifier.weight(0.20f)) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                    )
                }

            }
        }
    }
}