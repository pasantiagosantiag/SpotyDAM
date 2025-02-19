package ies.sequeros.vista.usuarios


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import ies.sequeros.modelo.entidades.Usuario
import ies.sequeros.vista.commons.DatePickerFieldToModal
import ies.sequeros.vista.listas.ListaItem
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


@OptIn(ExperimentalEncodingApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormulario(
    expandido: Boolean,
    editable: State<Boolean>,
    save: (item: Usuario) -> Unit,
    atras: () -> Unit,
    vm: UsuarioViewModel = koinViewModel()
) {
    // Estados para los campos
    val selected by vm.selected.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fechaalta = remember { mutableStateOf(LocalDateTime.now()) }
    var fechaultimaconexion = remember { mutableStateOf(LocalDateTime.now()) }

    var imagenString by remember { mutableStateOf<String?>("") }
    var imagenName by remember { mutableStateOf<String?>("") }
    var imageMime by remember { mutableStateOf<String?>("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    //boton guardar activo
    val enabled by  remember {  derivedStateOf {
        nombre.length>5 && password.length>=8  && imagenName!="" && bitmap!=null
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
        password = selected.password
        //gestión de fechas
        if (selected.fechaalta > 1L) {
            var date = LocalDateTime.ofEpochSecond(
                selected.fechaalta,
                0,
                ZoneOffset.UTC
            ) //Instant.ofEpochSecond(selected.fechaalta).
            fechaalta.value = date; //selected.fechaalta
            date = LocalDateTime.ofEpochSecond(
                selected.ultimaconexion,
                0,
                ZoneOffset.UTC
            ) //Instant.ofEpochSecond(selected.fechaalta).
            fechaultimaconexion.value = date; //selected.fechaalta
        } else {
            fechaultimaconexion.value = LocalDateTime.now()
            fechaalta.value = LocalDateTime.now()
        }
        if (selected.avatar.imagen != null && selected.avatar.mime != "") {
            val decodedBytes = Base64.decode(selected.avatar.imagen.data)
            bitmap = ImageIO.read(ByteArrayInputStream(decodedBytes)).toBitmap()
            imageMime=selected.avatar.mime
            imagenName = selected.avatar.filename
        } else {
            bitmap = null
        }
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
                value = nombre,
                onValueChange = {
                    nombre = it.replace("\t", "").replace("\n", "");
                },
                enabled = editable.value,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password, onValueChange = {
                    password = it.replace("\t", "").replace("\n", "");
                }, enabled = editable.value,
                label = { Text("Password") }, modifier = Modifier.fillMaxWidth()
            )

            DatePickerFieldToModal(
                modifier = Modifier,
                editable = editable,
                text = "Fecha alta",
                selectedDate = fechaalta,
                onDateSelected = {}
            )
            DatePickerFieldToModal(
                modifier = Modifier,
                editable = editable,
                text = "Última conexión",
                selectedDate = fechaultimaconexion,
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
                        var usuario = Usuario();
                        //para ver si es alta o modificación
                        usuario._id= selected._id!!
                        usuario.nombre = nombre;
                        usuario.password = password;
                        usuario.ultimaconexion = fechaultimaconexion.value.toEpochSecond(ZoneOffset.UTC)
                        usuario.fechaalta = fechaalta.value.toEpochSecond(ZoneOffset.UTC)
                        usuario.avatar.imagen = Binary(imagenString!!.toByteArray())
                        usuario.avatar.mime = imageMime.toString();
                        usuario.avatar.filename = imagenName.toString()
                        vm.save(usuario)

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start),

                ) {
                Text("Listas", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    selected.listas.forEach {

                        Text(" ${it.nombre}")

                    }
                }
            }


        }
    }
}