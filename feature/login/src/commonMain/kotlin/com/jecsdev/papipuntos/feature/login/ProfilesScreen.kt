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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.ProfileAvatar
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * A selectable Netflix-style profile: the couple shares one account and each
 * member unlocks their side with a PIN. PIN validation is delegated to
 * [AuthViewModel.unlockProfile], so this UI-only model no longer carries the pin.
 */
data class ProfileOption(
    val player: Player,
    val name: String,
    val emoji: String,
    val roleLabel: String,
)

/** Fake profiles feeding the `@Preview`s only; production reads from [AuthViewModel.authState]. */
val sampleProfiles: List<ProfileOption> = listOf(
    ProfileOption(Player.Papi, "Mateo", "👨🏻", "Papi"),
    ProfileOption(Player.Mami, "Sofía", "👩🏻", "Mami"),
)

private const val PIN_LENGTH = 4

/**
 * Profile gate shown right after account login: pick who is using the app, then
 * enter that profile's PIN. Owns the selection + PIN entry state and delegates
 * the visuals to the stateless [ProfileSelectContent] / [PinEntryContent]. PIN
 * validation goes through [AuthViewModel.unlockProfile]; on success the repo
 * moves [com.jecsdev.papipuntos.model.AuthState] to `Active` and `App.kt` navigates on its own.
 */
@Composable
fun ProfilesScreen(
    modifier: Modifier = Modifier,
    profiles: List<Profile>,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val options = profiles.map {
        ProfileOption(
            player = it.player,
            name = it.name,
            emoji = it.emoji,
            roleLabel = if (it.player == Player.Papi) "Papi" else "Mami",
        )
    }
    var selected by remember { mutableStateOf<ProfileOption?>(null) }
    val current = selected

    if (current == null) {
        ProfileSelectContent(
            profiles = options,
            onSelect = { selected = it },
            onChangeAccount = { viewModel.logOut() },
            modifier = modifier,
        )
    } else {
        // PIN state is scoped to the chosen profile: switching profiles resets it.
        var pin by remember(current) { mutableStateOf("") }
        var error by remember(current) { mutableStateOf(false) }
        val vmError by viewModel.error.collectAsStateWithLifecycle()

        // On a wrong PIN, hold the red state briefly, then clear so they retry.
        LaunchedEffect(error) {
            if (error) {
                delay(500)
                pin = ""
                error = false
                viewModel.clearError()
            }
        }

        // Surfaces the repo's failure (wrong PIN) as the same red state the UI already drives locally.
        LaunchedEffect(vmError) {
            if (vmError != null) {
                error = true
            }
        }

        PinEntryContent(
            profile = current,
            pin = pin,
            error = error,
            onBack = { selected = null },
            onKey = onKey@{ key ->
                when {
                    key == KEY_DELETE -> if (!error) pin = pin.dropLast(1)
                    error || pin.length >= PIN_LENGTH -> Unit
                    else -> {
                        val next = pin + key
                        pin = next
                        if (next.length == PIN_LENGTH) {
                            viewModel.unlockProfile(current.player, next)
                        }
                    }
                }
            },
            modifier = modifier,
        )
    }
}

@Composable
private fun ProfileSelectContent(
    profiles: List<ProfileOption>,
    modifier: Modifier = Modifier,
    onSelect: (ProfileOption) -> Unit = {},
    onChangeAccount: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Nuestra pareja",
            style = PapiPuntosTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.em,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "¿Quién está usando la app?",
            style = PapiPuntosTheme.typography.titleLarge.copy(fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = "Elige tu perfil para continuar",
            style = PapiPuntosTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            profiles.forEach { profile ->
                ProfileCard(
                    profile = profile,
                    onClick = { onSelect(profile) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        CoupleLinkCard()

        Spacer(Modifier.weight(1f))
        Text(
            text = "Cambiar de cuenta",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clickable(onClick = onChangeAccount)
                .padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun ProfileCard(
    profile: ProfileOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = profile.player.accentColor()
    Column(
        modifier = modifier
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxxl)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProfileAvatar(
            emoji = profile.emoji,
            ringColor = accent.copy(alpha = 0.5f),
            size = 80.dp,
            ringWidth = 4.dp,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = profile.name,
                style = PapiPuntosTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = profile.roleLabel,
                style = PapiPuntosTheme.typography.labelMedium.copy(fontSize = 11.sp),
                color = accent,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = PapiPuntosIcons.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = "PIN 4 dígitos",
                style = PapiPuntosTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** The "linked as a couple" info card under the profile grid. */
@Composable
private fun CoupleLinkCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, PapiPuntosTheme.shapes.xxl)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(PapiPuntosTheme.shapes.xxl)
                .background(
                    Brush.linearGradient(
                        listOf(PapiPuntosTheme.colors.mamiSoft, PapiPuntosTheme.colors.papiSoft),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "💞", fontSize = 18.sp)
        }
        Text(
            text = "Vinculados como pareja",
            style = PapiPuntosTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = "Comparten puntos, historial y recompensas",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PinEntryContent(
    profile: ProfileOption,
    pin: String,
    error: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onKey: (String) -> Unit = {},
) {
    val accent = profile.player.accentColor()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PapiPuntosTopBar(title = "Ingresa tu PIN", onBack = onBack)

        Spacer(Modifier.height(24.dp))
        ProfileAvatar(
            emoji = profile.emoji,
            ringColor = accent.copy(alpha = 0.5f),
            size = 80.dp,
            ringWidth = 4.dp,
        )
        Text(
            text = "Hola, ${profile.name}",
            style = PapiPuntosTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = profile.roleLabel,
            style = PapiPuntosTheme.typography.labelMedium.copy(fontSize = 11.sp),
            color = accent,
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(PIN_LENGTH) { index ->
                PinDot(filled = pin.length > index, error = error, accent = accent)
            }
        }
        Text(
            text = if (error) "PIN incorrecto, intenta de nuevo" else "Ingresa tu PIN de 4 dígitos",
            style = PapiPuntosTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.height(24.dp))
        PinPad(
            onKey = onKey,
            modifier = Modifier.widthIn(max = 260.dp),
        )
    }
}

@Composable
private fun PinDot(
    filled: Boolean,
    error: Boolean,
    accent: Color,
) {
    val fill = when {
        error -> MaterialTheme.colorScheme.error
        filled -> accent
        else -> Color.Transparent
    }
    val ring = when {
        error -> MaterialTheme.colorScheme.error
        filled -> accent
        else -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(fill)
            .border(2.dp, ring, CircleShape),
    )
}

@Composable
private fun PinPad(
    onKey: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 3-column keypad: 1-9, an empty slot, 0, and delete.
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", KEY_DELETE)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        PinKey(key = key, onClick = { onKey(key) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(PapiPuntosTheme.shapes.xxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxl)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (key == KEY_DELETE) "⌫" else key,
            style = PapiPuntosTheme.typography.titleMedium.copy(fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Sentinel for the delete key so digits stay plain strings. */
private const val KEY_DELETE = "del"

/** Per-profile accent: Papi is blue, Mami is pink. */
@Composable
private fun Player.accentColor(): Color =
    if (this == Player.Papi) PapiPuntosTheme.colors.papi else PapiPuntosTheme.colors.mami

@Preview
@Composable
private fun ProfileSelectPreview() {
    PapiPuntosTheme {
        ProfileSelectContent(profiles = sampleProfiles)
    }
}

@Preview
@Composable
private fun PinEntryPreview() {
    PapiPuntosTheme {
        PinEntryContent(profile = sampleProfiles.first(), pin = "12", error = false)
    }
}
