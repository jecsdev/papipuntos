package com.jecsdev.papipuntos.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTextField
import com.jecsdev.papipuntos.designsystem.component.PrimaryActionButton
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import org.koin.compose.viewmodel.koinViewModel

/** Emojis offered as avatars while setting up the two profiles. */
val emojiOptions: List<String> =
    listOf("👨🏻", "👩🏻", "🧔🏻‍♂️", "👱🏻‍♀️", "🧑🏻", "👩🏻‍🦱", "🐻", "🦊", "🐰", "🐼")

private const val PIN_SIZE = 4

/** Editable state for one profile while it is being created. */
data class ProfileDraft(
    val player: Player,
    val name: String,
    val emoji: String,
    val pin: String = "",
    val pinConfirm: String = "",
) {
    val pinMismatch: Boolean
        get() = pin.length == PIN_SIZE && pinConfirm.length == PIN_SIZE && pin != pinConfirm

    val isComplete: Boolean
        get() = name.isNotBlank() && pin.length == PIN_SIZE && pin == pinConfirm
}

/**
 * First-run setup: name, avatar and PIN for both profiles of the couple, on a
 * single screen. Owns the two drafts and hands the visuals to
 * [ProfileSetupContent]. Saving routes through [AuthViewModel.authState]; `App.kt`
 * reacts to the resulting [com.jecsdev.papipuntos.model.AuthState] instead of a callback.
 */
@Composable
fun ProfileSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    var papi by remember { mutableStateOf(ProfileDraft(Player.Papi, name = "Mateo", emoji = "👨🏻")) }
    var mami by remember { mutableStateOf(ProfileDraft(Player.Mami, name = "Sofía", emoji = "👩🏻")) }
    var attemptedSave by remember { mutableStateOf(false) }

    val valid = papi.isComplete && mami.isComplete

    ProfileSetupContent(
        papi = papi,
        mami = mami,
        showValidationError = attemptedSave && !valid,
        onPapiChange = { papi = it },
        onMamiChange = { mami = it },
        onSave = {
            attemptedSave = true
            if (valid) {
                viewModel.saveProfiles(
                    Profile(Player.Papi, papi.name, papi.emoji, papi.pin),
                    Profile(Player.Mami, mami.name, mami.emoji, mami.pin),
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ProfileSetupContent(
    papi: ProfileDraft,
    mami: ProfileDraft,
    showValidationError: Boolean,
    modifier: Modifier = Modifier,
    onPapiChange: (ProfileDraft) -> Unit = {},
    onMamiChange: (ProfileDraft) -> Unit = {},
    onSave: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(PapiPuntosTheme.shapes.xxl)
                .background(
                    Brush.linearGradient(
                        listOf(PapiPuntosTheme.colors.mami, PapiPuntosTheme.colors.papi),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "💞", fontSize = 24.sp)
        }
        Text(
            text = "Configura los perfiles de la pareja",
            style = PapiPuntosTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Personaliza a cada quién para empezar",
            style = PapiPuntosTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )

        Spacer(Modifier.height(20.dp))
        SetupCard(draft = papi, roleLabel = "Papi 💙", onChange = onPapiChange)
        Spacer(Modifier.height(16.dp))
        SetupCard(draft = mami, roleLabel = "Mami 💗", onChange = onMamiChange)

        if (showValidationError) {
            Text(
                text = "Completa nombre y confirma el PIN de 4 dígitos en ambos perfiles.",
                style = PapiPuntosTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        Spacer(Modifier.height(20.dp))
        PrimaryActionButton(text = "Guardar perfiles 💖", onClick = onSave)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SetupCard(
    draft: ProfileDraft,
    roleLabel: String,
    modifier: Modifier = Modifier,
    onChange: (ProfileDraft) -> Unit = {},
) {
    val accent = draft.player.accent()
    val soft = draft.player.softAccent()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxxl)
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, accent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = draft.emoji, fontSize = 28.sp)
            }
            Column {
                Text(
                    text = roleLabel.uppercase(),
                    style = PapiPuntosTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        letterSpacing = 0.06.em,
                    ),
                    color = accent,
                )
                Text(
                    text = "Elige avatar, nombre y PIN",
                    style = PapiPuntosTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        EmojiPicker(
            selected = draft.emoji,
            accent = accent,
            soft = soft,
            onSelect = { onChange(draft.copy(emoji = it)) },
        )

        Spacer(Modifier.height(12.dp))
        PapiPuntosTextField(
            value = draft.name,
            onValueChange = { onChange(draft.copy(name = it)) },
            placeholder = "Nombre",
            leadingIcon = PapiPuntosIcons.Person,
        )

        Spacer(Modifier.height(12.dp))
        PinLabel("PIN de 4 dígitos")
        Spacer(Modifier.height(4.dp))
        PinField(
            value = draft.pin,
            accent = accent,
            onValueChange = { onChange(draft.copy(pin = it)) },
        )
        Spacer(Modifier.height(8.dp))
        PinLabel("Confirmar PIN")
        Spacer(Modifier.height(4.dp))
        PinField(
            value = draft.pinConfirm,
            accent = accent,
            onValueChange = { onChange(draft.copy(pinConfirm = it)) },
        )
        if (draft.pinMismatch) {
            Text(
                text = "Los PIN no coinciden",
                style = PapiPuntosTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun EmojiPicker(
    selected: String,
    accent: Color,
    soft: Color,
    onSelect: (String) -> Unit,
) {
    // 5-column grid of avatar choices; the active one gets the profile's tint.
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        emojiOptions.chunked(5).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { emoji ->
                    val active = emoji == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(PapiPuntosTheme.shapes.xl)
                            .background(if (active) soft else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (active) 2.dp else 0.dp,
                                color = if (active) accent else Color.Transparent,
                                shape = PapiPuntosTheme.shapes.xl,
                            )
                            .clickable { onSelect(emoji) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PinLabel(text: String) {
    Text(
        text = text,
        style = PapiPuntosTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/**
 * Four segmented cells filled as digits are typed. Built on a single hidden
 * BasicTextField (numeric keyboard, capped at 4 digits) so the whole row acts as
 * one input; the digits themselves are masked to dots.
 */
@Composable
private fun PinField(
    value: String,
    accent: Color,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit).take(PIN_SIZE)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(PIN_SIZE) { index ->
                    val filled = value.length > index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(PapiPuntosTheme.shapes.xl)
                            .background(MaterialTheme.colorScheme.background)
                            .border(
                                width = 1.dp,
                                color = if (filled) accent else MaterialTheme.colorScheme.outline,
                                shape = PapiPuntosTheme.shapes.xl,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (filled) {
                            Text(
                                text = "•",
                                style = PapiPuntosTheme.typography.titleMedium.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
    )
}

/** Per-profile accent colors, matching the couple's blue/pink split. */
@Composable
private fun Player.accent(): Color =
    if (this == Player.Papi) PapiPuntosTheme.colors.papi else PapiPuntosTheme.colors.mami

@Composable
private fun Player.softAccent(): Color =
    if (this == Player.Papi) PapiPuntosTheme.colors.papiSoft else PapiPuntosTheme.colors.mamiSoft

@Preview
@Composable
private fun ProfileSetupPreview() {
    PapiPuntosTheme {
        ProfileSetupContent(
            papi = ProfileDraft(Player.Papi, name = "Mateo", emoji = "👨🏻", pin = "12"),
            mami = ProfileDraft(Player.Mami, name = "Sofía", emoji = "👩🏻"),
            showValidationError = false,
        )
    }
}
