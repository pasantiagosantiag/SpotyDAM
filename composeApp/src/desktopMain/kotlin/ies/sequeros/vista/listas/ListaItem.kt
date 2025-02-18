package ies.sequeros.vista.listas


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.entidades.Lista
import org.jetbrains.skiko.toBitmap
import org.jetbrains.skiko.toBufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ListaItem(
    item: ListaDTO,
    //context: Context,
    ver: () -> Unit,
    editar: () -> Unit,
    borrar: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val decodedBytes = Base64.decode(item.portada.imagen.data)
        var bitmap = ImageIO.read(ByteArrayInputStream(decodedBytes)).toBitmap()
        Image(bitmap.toBufferedImage().toPainter(), "dfdsfdsf", modifier = Modifier.size(48.dp))

        Text("${item.nombre}( ${item.canciones.size} )${item._id}", modifier = Modifier.clickable { ver() }.weight(0.40f))

        Button(onClick = { editar() }, modifier = Modifier.weight(0.20f)) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar",
            )
        }
        Button(onClick = { borrar() }, modifier = Modifier.weight(0.20f)) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Borrar",
            )
        }
    }
}