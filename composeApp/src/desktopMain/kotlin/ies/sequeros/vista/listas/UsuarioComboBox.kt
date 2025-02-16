package com.example.sqllitecomposecine.ui.componentes.sesiones

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ies.sequeros.modelo.dto.UsuarioDTO

import ies.sequeros.modelo.entidades.Usuario



@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UsuarioComboBox(items: State<List<UsuarioDTO>?>, item: State<UsuarioDTO>, alcambiar:   (UsuarioDTO) -> Unit) {


    // State to manage the selected item and menu visibility
    var selectedUsuario by remember { mutableStateOf<UsuarioDTO?>(item.value) }

    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
       item.let {
            selectedUsuario = it.value


        }

    }


    //selected.categoria?.let { Text( it.nombre) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        // Textfield that shows the selected item or a hint
        TextField(
            value = selectedUsuario?.nombre ?: "Selecciona un usuario",
            onValueChange = {

            },
            readOnly = true,
            label = { Text("Usuario") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        // Dropdown menu with the list of localidades
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.value?.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        run {
                            selectedUsuario = item
                             alcambiar(item)
                            expanded = false
                        }
                    },
                    text = { Text(text = item.nombre) },

                )

            }
        }
    }
}