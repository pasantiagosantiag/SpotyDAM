package ies.sequeros.vista.listas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ies.sequeros.modelo.dto.ListaDTO
import ies.sequeros.modelo.entidades.Lista
import ies.sequeros.vista.Bienvenida
import ies.sequeros.vistamodelo.ListaViewModel
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListasMain(modifier: Modifier = Modifier, vm: ListaViewModel = koinViewModel()) {
    val navigator = rememberListDetailPaneScaffoldNavigator<ListaDTO>()
    val elementos = vm.items.collectAsState()
    val formularioEditable = remember { mutableStateOf(false)
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

                    Text("Listas")

                   ListaBuscador(
                       modifier = Modifier,
                       vm.buscadornombre

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
                                ListaItem(
                                    elementos.value[it],

                                    ver = {
                                        run {
                                            vm.setSelected(elementos.value.get(it))
                                            formularioEditable.value = false

                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                elementos.value.get(it)
                                            )
                                        }
                                    }, editar = {
                                        run {
                                            vm.setSelected(elementos.value.get(it))
                                            formularioEditable.value = true
                                            navigator.navigateTo(
                                                ListDetailPaneScaffoldRole.Detail,
                                                elementos.value.get(it)
                                            )
                                        }
                                    }, borrar = {
                                        run {
                                            vm.remove(elementos.value.get(it))
                                        }
                                    })


                            }
                        }
                    }
                },
                detailPane = {
                    Column(
                        modifier = Modifier

                    ){
                    ListaFormulario(expandido = true, editable = formularioEditable, save = {
                       // vm.save(it)
                        vm.unSelect()
                        if (navigator.canNavigateBack()) navigator.navigateBack()

                    }, atras = {
                        //run {
                        if (navigator.canNavigateBack()) navigator.navigateBack()
                        //}
                    })
                    }

                })
        }
    }
}