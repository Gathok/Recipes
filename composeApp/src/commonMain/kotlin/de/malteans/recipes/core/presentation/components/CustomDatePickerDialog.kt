package de.malteans.recipes.core.presentation.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.cancel
import recipes.composeapp.generated.resources.select_date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (LocalDate) -> Unit,
    initialSelectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toEpochDays() * 24L * 60L * 60L * 1000L,
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = { onSubmit(datePickerState.selectedDateMillis!!.toLocalDate()) },
            ) {
                Text(
                    text = stringResource(Res.string.select_date),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(Res.string.cancel),
                )
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.UTC): LocalDate {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone).date
}