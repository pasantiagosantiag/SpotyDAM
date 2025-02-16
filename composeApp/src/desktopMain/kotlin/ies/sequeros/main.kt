package ies.sequeros

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import spotydam.composeapp.generated.resources.Res
import spotydam.composeapp.generated.resources.music

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SpotyDAM",

        icon = painterResource(Res.drawable.music)
    ) {
        KoinApplication(application = {

            modules(appModule)
        }) {
            App(
                salir = {
                  this@application.exitApplication()
                }
            )
        }
    }
}