package ies.sequeros.vista.listas


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ListaBuscador(
    modifier: Modifier=Modifier,
    buscadornombre: MutableStateFlow<String>,
    //buscadordescripcion: MutableStateFlow<String>,

    ) {
    val nombre = buscadornombre.collectAsState()
    //val descripcion = buscadordescripcion.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth(),

        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(0.5f).padding(horizontal = 2.dp)
        ) {
            OutlinedTextField(
                value = nombre.value,
                //se elimina el tabulador
                onValueChange = { buscadornombre.value = it.replace("\t", "").replace("\n","") },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
        }
       /* Column(
            modifier = Modifier.weight(0.5f).padding(horizontal = 2.dp)
        ) {
            OutlinedTextField(
                value = descripcion.value,
                onValueChange = { buscadordescripcion.value = it.replace("\t", "").replace("\n","") },
                label = { Text("Descripcion") },
                modifier = Modifier.fillMaxWidth()
            )
        }*/


    }
}