package ies.sequeros.vista.canciones


//import ies.sequeros.vistamodelo.CancionViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.vistamodelo.CancionesViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CancionFormulario(
    expandido: Boolean,
    editable: State<Boolean>,
    save: (item: Cancion) -> Unit,
    atras: () -> Unit,
    vm: CancionesViewModel = koinViewModel()
) {
    // Estados para los campos
    val selected by vm.selected.collectAsState()
    var titulo by remember { mutableStateOf("") }
    var artista by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf(0) }
    var anyo by remember { mutableStateOf(0) }
    var letra by remember { mutableStateOf("") }

    //boton guardar activo
    val enabled by remember {
        derivedStateOf {
            titulo.length > 5 && artista.length >= 8 && comentario.length >= 8 && duracion >= 0 &&
                    anyo >= 1700
        }
    }

    //cambio del selected en el viewModel
    LaunchedEffect(selected) {
        titulo = selected.titulo
        artista = selected.artista
        comentario = selected.comentario
        duracion = selected.duracion
        anyo = selected.anyo
        letra = selected.letra

    }
    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de nombre
            OutlinedTextField(
                value = titulo,
                onValueChange = {
                    titulo = it.replace("\t", "").replace("\n", "");
                },
                enabled = editable.value,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = artista, onValueChange = {
                    artista = it.replace("\t", "").replace("\n", "");
                }, enabled = editable.value,
                label = { Text("Artista") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = comentario, onValueChange = {
                    comentario = it.replace("\t", "").replace("\n", "");
                }, enabled = editable.value,
                label = { Text("Comentario") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = letra, onValueChange = {
                    letra = it.replace("\t", "").replace("\n", "");
                }, enabled = editable.value,
                label = { Text("Letra") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = if (duracion > 0) duracion.toString() else "", onValueChange = { it ->
                    if (it.length <= 3) {
                        var tempo = it.replace("\t", "").replace("\n", "").filter { it.isDigit() }
                        if (!tempo.equals("")) duracion = tempo.toInt()
                    }

                }, enabled = editable.value,

                label = { Text("Duración (segundos)") }, modifier = Modifier.fillMaxWidth()//.weight(0.8f)
            )
            OutlinedTextField(
                value = if (anyo > 0) anyo.toString() else "", onValueChange = { it ->
                    //if (it.length <= 3) {
                    var tempo = it.replace("\t", "").replace("\n", "").filter { it.isDigit() }
                    if (!tempo.equals("")) anyo = tempo.toInt()
                    //}

                }, enabled = editable.value,

                label = { Text("Año") }, modifier = Modifier.fillMaxWidth()//.weight(0.8f)
            )
            Row {
                Button(
                    onClick = {
                        var item = Cancion();
                        item._id = selected._id
                        item.titulo = titulo
                        item.comentario = comentario
                        item.anyo = anyo
                        item.letra = letra
                        item.duracion = duracion
                        item.artista = artista

                        vm.save(item)

                    },
                    //si se está en modo edición y formulario corrrecto
                    enabled = editable.value && enabled

                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Guardar",
                    )
                }
                Button(
                    onClick = {
                        atras()
                    },
                    modifier = Modifier.padding(start = 10.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Volver",
                    )
                }
            }
        }
    }
}