package ies.sequeros.vista.canciones

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ies.sequeros.modelo.entidades.Cancion
import ies.sequeros.vistamodelo.CancionesViewModel
import ies.sequeros.vistamodelo.ListaViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun CancionesMain(
    modifier: Modifier = Modifier,
    vm: CancionesViewModel = koinViewModel(),
    listavm: ListaViewModel = koinViewModel()
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Cancion>()
    val navController = rememberNavController()

    val elementos = vm.items.collectAsState()
    val formularioEditable = remember {
        mutableStateOf(false)
    }
    val isListAndDetailVisible =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded && navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded
    val searchview =
        navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

    /*BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }*/
    Scaffold(
        floatingActionButton = {
            if (searchview) {
                FloatingActionButton(onClick = {
                    vm.unSelect()
                    formularioEditable.value = true
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(horizontal = 2.dp)) {
                if (searchview) {

                    Text("Canciones")

                    CancionBuscador(
                        modifier = Modifier,
                        vm.buscadortitulo

                    )
                } else

                    Text("Editor de listas")

            }

            ListDetailPaneScaffold(modifier = Modifier,
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    Box {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 32.dp)
                        ) {

                            items(elementos.value.size) {
                                CancionItem(
                                    elementos.value[it],

                                    ver = {
                                        run {
                                            vm.setSelected(elementos.value.get(it))

                                            vm._selected.value=elementos.value[0]
                                             formularioEditable.value = false
                                            navController.navigate("canciones")
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                elementos.value.get(it)
                                            )
                                        }
                                    }, editar = {
                                        run {
                                            var tempo = elementos.value.get(it)
                                            vm.setSelected(tempo)
                                            formularioEditable.value = true
                                            navController.navigate("canciones")
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                elementos.value.get(it)
                                            )
                                        }
                                    }, borrar = {
                                        run {
                                            vm.remove(elementos.value.get(it))
                                        }
                                    },
                                    addLista = {
                                        run {
                                            vm.setSelected(elementos.value.get(it))
                                            navController.navigate("seleccionarlista")
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                elementos.value.get(it)
                                            )
                                        }
                                    })


                            }
                        }
                    }
                },
                detailPane = {

                    NavHost(
                        navController = navController,
                        startDestination = "canciones" // Pantalla inicial
                    ) {
                        composable("canciones") {
                            CancionFormulario(expandido = true, editable = formularioEditable, save = {
                                // vm.save(it)
                                //vm.unSelect()
                                if (navigator.canNavigateBack()) navigator.navigateBack()

                            }, atras = {
                                //run {
                                if (navigator.canNavigateBack()) navigator.navigateBack()
                                //}
                            }, vm=vm)
                        }
                        composable("seleccionarlista") {

                            SelectorLista(listavm, vm.selected) { listaDTO ->
                                run {
                                    listavm.addCancion(vm.selected.value, listaDTO)
                                    if (navigator.canNavigateBack()) navigator.navigateBack()

                                }
                            }// println("Se tiene que añadir la cancion " + vm.selected.value.titulo + " A la lista" + it.nombre)

                        }
                    }

                })
        }
    }
}
