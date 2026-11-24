package com.jecsdev.papipuntos.feature.addaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

@Composable
internal fun NewPendingActionDialog(
    label: String,
    points: String,
    emoji: String,
    isSubmitting: Boolean,
    errorMessage: String?,
    onLabelChange: (String) -> Unit,
    onPointsChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val validPoints = points.toIntOrNull()?.let { it > 0 } == true
    val invalidPoints = points.isNotEmpty() && !validPoints
    val fieldShape = PapiPuntosTheme.materialShapes.medium
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colors.onTertiary,
        focusedLabelColor = colors.onTertiary,
        unfocusedBorderColor = colors.onSurfaceVariant.copy(alpha = 0.7f),
        unfocusedContainerColor = colors.surfaceVariant,
        cursorColor = colors.onTertiary,
    )

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Surface(
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
            shape = PapiPuntosTheme.shapes.xxl,
            color = colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(colors.tertiary, fieldShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(PapiPuntosIcons.Add, contentDescription = null, tint = colors.onTertiary)
                    }
                    Text(
                        "Nuevo pendiente",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() },
                    )
                }
                Text(
                    "Tu pareja debe aprobarlo para que sumes los puntos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = label,
                    onValueChange = onLabelChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Pendiente") },
                    placeholder = { Text("Ej. Preparé el desayuno") },
                    singleLine = true,
                    enabled = !isSubmitting,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    shape = fieldShape,
                    colors = fieldColors,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = points,
                        onValueChange = onPointsChange,
                        modifier = Modifier.weight(1f),
                        label = { Text("Puntos") },
                        placeholder = { Text("Ej. 25") },
                        singleLine = true,
                        enabled = !isSubmitting,
                        isError = invalidPoints,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        shape = fieldShape,
                        colors = fieldColors,
                    )
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = onEmojiChange,
                        modifier = Modifier.weight(1f),
                        label = { Text("Emoji") },
                        placeholder = { Text("✨") },
                        supportingText = { Text("Opcional") },
                        singleLine = true,
                        enabled = !isSubmitting,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = fieldShape,
                        colors = fieldColors,
                    )
                }
                val formError = if (invalidPoints) "Usa un número entero mayor que cero." else errorMessage
                if (formError != null) {
                    Text(formError, style = MaterialTheme.typography.bodySmall, color = colors.onTertiary)
                }
                Button(
                    onClick = onSubmit,
                    enabled = !isSubmitting && label.isNotBlank() && validPoints,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = fieldShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onSurface,
                        disabledContainerColor = colors.surfaceVariant,
                        disabledContentColor = colors.onSurfaceVariant,
                    ),
                ) {
                    Text(
                        if (isSubmitting) "Enviando…" else "Enviar para aprobación",
                        textAlign = TextAlign.Center,
                    )
                }
                TextButton(onClick = onDismiss, enabled = !isSubmitting, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Cancelar", color = if (isSubmitting) colors.onSurfaceVariant else colors.onSurface)
                }
            }
        }
    }
}
