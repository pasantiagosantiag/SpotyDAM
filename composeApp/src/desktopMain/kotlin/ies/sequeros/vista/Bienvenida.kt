package ies.sequeros.vista


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.FontStyle
import spotydam.composeapp.generated.resources.Res
import spotydam.composeapp.generated.resources.subtitulo
import spotydam.composeapp.generated.resources.titulo

@Composable
fun Bienvenida() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
    Text(
        text = stringResource(Res.string.titulo),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 32.dp)
    )
    }
    Text(
        text = stringResource(Res.string.subtitulo),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.W300,
        modifier = Modifier.padding(bottom = 16.dp)
    )

}