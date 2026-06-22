package com.jecsdev.papipuntos.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.PrimaryActionButton
import com.jecsdev.papipuntos.designsystem.component.SegmentedToggle
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.profile.model.PlanFeature

/** Production entry point: owns the selected billing cycle. */
@Composable
fun PlansScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onStartTrial: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    var billing by remember { mutableStateOf(BillingCycle.Monthly) }
    PlansScreenContent(
        billing = billing,
        freeFeatures = PlansSampleData.free,
        premiumFeatures = PlansSampleData.premium,
        onBillingChange = { billing = it },
        onBack = onBack,
        onStartTrial = onStartTrial,
        onLogout = onLogout,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun PlansScreenContent(
    billing: BillingCycle,
    freeFeatures: List<PlanFeature>,
    premiumFeatures: List<PlanFeature>,
    modifier: Modifier = Modifier,
    onBillingChange: (BillingCycle) -> Unit = {},
    onBack: () -> Unit = {},
    onStartTrial: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
    ) {
        PapiPuntosTopBar(title = "Elige tu plan", onBack = onBack)

        Spacer(Modifier.height(12.dp))
        SegmentedToggle(
            options = BillingCycle.entries.map { it.label },
            selectedIndex = billing.ordinal,
            onSelect = { onBillingChange(BillingCycle.entries[it]) },
        )

        Spacer(Modifier.height(16.dp))
        FreePlanCard(features = freeFeatures)

        Spacer(Modifier.height(12.dp))
        PremiumPlanCard(
            billing = billing,
            features = premiumFeatures,
            onStartTrial = onStartTrial,
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Cerrar sesión",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(PapiPuntosTheme.shapes.xl)
                .clickable(onClick = onLogout)
                .padding(vertical = 12.dp),
        )
    }
}

@Composable
private fun FreePlanCard(features: List<PlanFeature>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxxl)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                PlanLabel("Gratis", MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                PriceLine(
                    price = "$0",
                    period = "/siempre",
                    priceStyle = PapiPuntosTheme.typography.headlineSmall,
                )
            }
            Pill(
                text = "Plan actual",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(16.dp))
        FeatureList(features = features)
    }
}

@Composable
private fun PremiumPlanCard(
    billing: BillingCycle,
    features: List<PlanFeature>,
    onStartTrial: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(
                Brush.linearGradient(
                    listOf(
                        PapiPuntosTheme.colors.premiumBg,
                        PapiPuntosTheme.colors.mamiSoft,
                        PapiPuntosTheme.colors.papiSoft,
                    ),
                ),
            )
            .border(2.dp, PapiPuntosTheme.colors.premiumBorder, PapiPuntosTheme.shapes.xxxl)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = PapiPuntosIcons.Crown,
                    contentDescription = null,
                    tint = PapiPuntosTheme.colors.premiumIcon,
                    modifier = Modifier.size(20.dp),
                )
                PlanLabel("Premium", PapiPuntosTheme.colors.premiumText)
            }
            Pill(
                text = "Recomendado",
                containerColor = PapiPuntosTheme.colors.premium,
                contentColor = Color.White,
                leadingIcon = PapiPuntosIcons.Sparkles,
            )
        }

        Spacer(Modifier.height(8.dp))
        PriceLine(
            price = billing.price,
            period = billing.period,
            priceStyle = PapiPuntosTheme.typography.displaySmall,
        )
        Text(
            text = "7 días de prueba gratis · cancela cuando quieras",
            style = PapiPuntosTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(16.dp))
        FeatureList(features = features)

        Spacer(Modifier.height(20.dp))
        PrimaryActionButton(
            text = "Empezar prueba gratis ✨",
            onClick = onStartTrial,
            gradient = listOf(
                PapiPuntosTheme.colors.premium,
                PapiPuntosTheme.colors.mami,
                MaterialTheme.colorScheme.primary,
            ),
            shadowColor = PapiPuntosTheme.colors.mami,
        )
    }
}

/** Uppercase plan tier label (`text-xs font-bold uppercase tracking-wider`). */
@Composable
private fun PlanLabel(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        style = PapiPuntosTheme.typography.labelMedium,
        color = color,
        letterSpacing = 0.08.em,
    )
}

/** A price followed by its smaller, muted period suffix on the same baseline. */
@Composable
private fun PriceLine(
    price: String,
    period: String,
    priceStyle: androidx.compose.ui.text.TextStyle,
) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = price,
            style = priceStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = period,
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
        )
    }
}

@Composable
private fun FeatureList(features: List<PlanFeature>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        features.forEach { feature ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = PapiPuntosIcons.Check,
                    contentDescription = null,
                    tint = if (feature.included) {
                        PapiPuntosTheme.colors.mami
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = feature.label,
                    style = PapiPuntosTheme.typography.bodySmall.copy(
                        fontWeight = if (feature.included) FontWeight.SemiBold else FontWeight.Normal,
                        textDecoration = if (feature.included) null else TextDecoration.LineThrough,
                    ),
                    color = if (feature.included) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

/** Small rounded chip, optionally with a leading icon. */
@Composable
private fun Pill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .clip(PapiPuntosTheme.shapes.pill)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp),
            )
        }
        Text(
            text = text,
            style = PapiPuntosTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun PlansScreenPreview() {
    PapiPuntosTheme {
        PlansScreenContent(
            billing = BillingCycle.Monthly,
            freeFeatures = PlansSampleData.free,
            premiumFeatures = PlansSampleData.premium,
        )
    }
}
