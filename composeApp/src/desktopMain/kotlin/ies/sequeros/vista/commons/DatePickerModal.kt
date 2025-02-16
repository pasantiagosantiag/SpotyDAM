package ies.sequeros.vista.commons


import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(

    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    selectedDate: MutableState<LocalDateTime>,
    editable: State<Boolean>,
    text:String="Fecha",
    onDateSelected: (Long?) -> Unit
) {
    // var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }
    LaunchedEffect(editable) {


    }
    OutlinedTextField(value = selectedDate?.let { convertMillisToDate(it) } ?: "",
        onValueChange = { },
        label = { Text(text) },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        enabled = editable.value,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                if (editable.value == true) awaitEachGesture {

                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            })

    if (showModal && editable.value) {
        DatePickerModal(onDateSelected = {
            //para el componente
            onDateSelected(it)
            it?.let { it1 ->
                selectedDate.value =
                    Instant.ofEpochMilli(it1).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
        }, onDismiss = { showModal = false })
    }
}

fun localDateTimeToString(localDateTime: LocalDateTime, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun convertMillisToDate(fecha: MutableState<LocalDateTime>): String {
    val localDateTime = fecha.value // LocalDateTime.now() // Obtiene la fecha y hora actual
    val tempo = localDateTimeToString(localDateTime, "dd-MM-yyyy")
    return tempo;

}