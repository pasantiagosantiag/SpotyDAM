package ies.sequeros.vista.listas


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sqllitecomposecine.ui.componentes.sesiones.UsuarioComboBox
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.dto.UsuarioDTO
import ies.sequeros.modelo.dto.UsuarioListaDTO
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.vista.commons.DatePickerFieldToModal
import ies.sequeros.vistamodelo.ListaViewModel
import ies.sequeros.vistamodelo.UsuarioViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.runBlocking
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBitmap
import org.jetbrains.skiko.toBufferedImage
import org.koin.compose.viewmodel.koinViewModel
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.imageio.ImageIO
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.exp


@OptIn(ExperimentalEncodingApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListaFormulario(
    expandido: Boolean,
    editable: State<Boolean>,
    save: (item: ListaDTO) -> Unit,
    atras: () -> Unit,
    vm: ListaViewModel = koinViewModel(),
    usuariosvm:UsuarioViewModel= koinViewModel ()
) {
    // Estados para los campos
    val selected by vm.selected.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var fechacreacion = remember { mutableStateOf(LocalDateTime.now()) }
    var imagenString by remember { mutableStateOf<String?>("") }
    var imagenName by remember { mutableStateOf<String?>("") }
    var imageMime by remember { mutableStateOf<String?>("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var usuario = remember { mutableStateOf(UsuarioDTO()) }

    val scrollState = rememberScrollState()

    //boton guardar activo
    val enabled by  remember {  derivedStateOf {
        nombre.length>5 && comentario.length>=8  && imagenName!="" && bitmap!=null
    } }
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image, //ImageAndVideo,
        mode = PickerMode.Single, //.Multiple(),
        title = "Seleccionar imagen",
    ) { file ->
        var     f = file?.path?.let { File(it) }
        if (f != null) {
            //se codifica
            imagenString = Base64.encode(f.readBytes())
            //se gaurda el nombre y el mime
            imagenName = f.name
            imageMime = Files.probeContentType(f.toPath())
            val decodedBytes = Base64.decode(imagenString!!)
            bitmap = ImageIO.read(ByteArrayInputStream(decodedBytes)).toBitmap()
        }
    }
    //cambio del selected en el viewModel
    LaunchedEffect(selected) {
        nombre = selected.nombre
        comentario = selected.comentario
        usuario.value=UsuarioDTO()
        usuario.value._id=selected.usuario._id
        usuario.value.nombre=selected.usuario.nombre

        //gestión de fechas
        if (selected.fechacreacion > 1L) {
            var date = LocalDateTime.ofEpochSecond(
                selected.fechacreacion,
                0,
                ZoneOffset.UTC
            ) //Instant.ofEpochSecond(selected.fechaalta).
            fechacreacion.value = date; //selected.fechaalta

        } else {
            fechacreacion.value = LocalDateTime.now()
        }
        if (selected.portada.imagen != null && selected.portada.mime != "") {
            val decodedBytes = Base64.decode(selected.portada.imagen.data)
            bitmap = ImageIO.read(ByteArrayInputStream(decodedBytes)).toBitmap()
            imageMime=selected.portada.mime
            imagenName = selected.portada.filename
        } else {
            bitmap = null
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it.replace("\t", "").replace("\n", "");
                },
                enabled = editable.value,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = comentario, onValueChange = {
                    comentario = it.replace("\t", "").replace("\n", "");
                }, enabled = editable.value,
                label = { Text("Comentario") }, modifier = Modifier.fillMaxWidth()
            )
            UsuarioComboBox(items = usuariosvm.items.collectAsState(), item = usuario) {
                usuario.value = it
            }
            DatePickerFieldToModal(
                modifier = Modifier,
                editable = editable,
                text = "Fecha creación",
                selectedDate = fechacreacion,
                onDateSelected = {}
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                bitmap?.let {
                    Image(it.toBufferedImage().toPainter(), "dfdsfdsf", modifier = Modifier.size(128.dp))
                }

                Button(
                    onClick = {
                        runBlocking {
                            launcher.launch()
                        }
                    }, enabled = editable.value

                ) {
                    Icon(
                        imageVector = Icons.Filled.Photo,
                        contentDescription = "Imagen",
                    )
                }
                Button(
                    onClick = {

                    }, enabled = (selected._id.toString()!=null) && enabled

                ) {
                    Icon(
                        imageVector = Icons.Filled.ViewList,
                        contentDescription = "Listas",
                    )
                }
                Button(
                    onClick = {
                        var item = ListaDTO();
                        //para ver si es alta o modificación
                        item._id=selected._id
                        item.nombre = nombre;
                        item.comentario = comentario;
                        item.fechacreacion = fechacreacion.value.toEpochSecond(ZoneOffset.UTC)
                        item.portada.imagen = Binary(imagenString!!.toByteArray())
                        item.portada.mime = imageMime.toString();
                        item.portada.filename = imagenName.toString()
                        item.usuario= usuario.value._id?.let { UsuarioListaDTO(it,usuario.value.nombre) }!!
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
               // if(!expandido)
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




            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),

               ) {
                Text("Canciones", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

               Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    selected.canciones.forEach {
                        Text("Canción: título ${it.titulo}, artista ${it.artista}")

                     }
                }
            }

        }

}