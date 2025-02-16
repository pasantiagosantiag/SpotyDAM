package ies.sequeros.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.window.core.layout.WindowWidthSizeClass
import ies.sequeros.vista.canciones.CancionesMain
import ies.sequeros.vista.listas.ListasMain
import ies.sequeros.vista.usuarios.UsuarioMain

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val visibleCompact: Boolean
) {
    HOME("Home", Icons.Default.Home, "Home", true),
    USUARIOS("Usuarios", Icons.Filled.Person, "Usuarios", true),
    LISTAS("Listas", Icons.Filled.ViewList, "Listas", true),
    CANCIONES("Canciones", Icons.Filled.MusicNote, "Canciones", true),
    GRAFICOS("Estadísticas", Icons.Filled.BarChart, "Estadísticas", false),
    SALIR("Salir", Icons.Filled.Logout, "Salir", true)
}

@Composable
fun Principal(modifier: Modifier = Modifier, salir: () -> Unit) {
    var seleted = remember { mutableStateOf(AppDestinations.HOME) }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(modifier = modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.error)) {
        NavigationSuiteScaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.error),
            navigationSuiteItems = {

                AppDestinations.entries.forEach {
                    if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT || it.visibleCompact == true) {
                        item(
                            icon = {
                                androidx.compose.material3.Icon(
                                    imageVector = it.icon,
                                    contentDescription = it.contentDescription,
                                    // tint = MaterialTheme.colorScheme.inversePrimary, // Cambia el color
                                    //modifier = Modifier.size(96.dp)// Cambia el tamaño
                                )
                            },
                            label = { Text(it.label) },

                            selected = seleted.value == it,
                            onClick = {
                                if (it == AppDestinations.SALIR)
                                    salir()
                                else
                                    seleted.value = it
                            }
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                    ) {
                    when(seleted.value) {
                        AppDestinations.HOME -> { Bienvenida() }
                        AppDestinations.USUARIOS -> { UsuarioMain()  }
                        AppDestinations.LISTAS -> { ListasMain() }
                        AppDestinations.CANCIONES -> { CancionesMain() }
                        AppDestinations.GRAFICOS -> { Bienvenida() }
                        AppDestinations.SALIR -> { Bienvenida() }
                    }

                }
            }
        }

    }
}