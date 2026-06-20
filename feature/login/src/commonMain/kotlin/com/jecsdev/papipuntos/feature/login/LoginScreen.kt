package com.jecsdev.papipuntos.feature.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.component.LabeledDivider
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTextField
import com.jecsdev.papipuntos.designsystem.component.PrimaryActionButton
import com.jecsdev.papipuntos.designsystem.component.SegmentedToggle
import com.jecsdev.papipuntos.designsystem.component.SocialButton
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme

/** Auth entry modes shown in the segmented switch. */
enum class LoginMode(val label: String, val cta: String) {
    Login("Iniciar sesión", "Entrar 💖"),
    SignUp("Crear cuenta", "Crear cuenta 💖"),
}

/** Production entry point: owns the local form state and delegates to [LoginScreenContent]. */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onAuthenticated: () -> Unit = {},
) {
    var mode by remember { mutableStateOf(LoginMode.Login) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    LoginScreenContent(
        mode = mode,
        email = email,
        password = password,
        onModeChange = { mode = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSubmit = onAuthenticated,
        onContinueWithGoogle = onAuthenticated,
        onContinueWithApple = onAuthenticated,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun LoginScreenContent(
    mode: LoginMode,
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onModeChange: (LoginMode) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onContinueWithGoogle: () -> Unit = {},
    onContinueWithApple: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandLogo()

        Spacer(Modifier.height(20.dp))
        Text(
            text = brandTitle(),
            style = PapiPuntosTheme.typography.headlineSmall.copy(fontSize = 24.sp),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Premia los gestos lindos de tu pareja",
            style = PapiPuntosTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))
        SegmentedToggle(
            options = LoginMode.entries.map { it.label },
            selectedIndex = mode.ordinal,
            onSelect = { onModeChange(LoginMode.entries[it]) },
            activeContentColor = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(16.dp))
        PapiPuntosTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "tu@email.com",
            leadingIcon = PapiPuntosIcons.Mail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        Spacer(Modifier.height(10.dp))
        PapiPuntosTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Contraseña",
            leadingIcon = PapiPuntosIcons.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(Modifier.height(16.dp))
        PrimaryActionButton(
            text = mode.cta,
            onClick = onSubmit,
            gradient = listOf(
                PapiPuntosTheme.colors.mami,
                MaterialTheme.colorScheme.primary,
            ),
        )

        Spacer(Modifier.height(16.dp))
        LabeledDivider(text = "o continúa con")

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SocialButton(
                text = "Google",
                onClick = onContinueWithGoogle,
                modifier = Modifier.weight(1f),
                leading = {
                    Text(
                        text = "G",
                        style = PapiPuntosTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
            )
            SocialButton(
                text = "Apple",
                onClick = onContinueWithApple,
                modifier = Modifier.weight(1f),
                leading = {
                    Icon(
                        imageVector = PapiPuntosIcons.Apple,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "Al continuar aceptas los términos y la política de privacidad.",
            style = PapiPuntosTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** Rounded gradient tile holding the app's heart emblem (`from-mami to-papi`). */
@Composable
private fun BrandLogo() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .shadow(18.dp, PapiPuntosTheme.shapes.xxxl, spotColor = PapiPuntosTheme.colors.mami)
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(
                Brush.linearGradient(
                    listOf(PapiPuntosTheme.colors.mami, PapiPuntosTheme.colors.papi),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "💕", fontSize = 36.sp)
    }
}

/** "Papi & Mami Puntos" with the connector and "Puntos" tinted per profile. */
@Composable
private fun brandTitle() = buildAnnotatedString {
    append("Papi ")
    withStyle(SpanStyle(color = PapiPuntosTheme.colors.papi)) { append("&") }
    append(" Mami ")
    withStyle(SpanStyle(color = PapiPuntosTheme.colors.mami, fontWeight = FontWeight.ExtraBold)) {
        append("Puntos")
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    PapiPuntosTheme {
        LoginScreenContent(
            mode = LoginMode.Login,
            email = "",
            password = "",
        )
    }
}
