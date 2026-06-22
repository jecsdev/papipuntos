package com.jecsdev.papipuntos.feature.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jecsdev.papipuntos.designsystem.component.PapiPuntosTopBar
import com.jecsdev.papipuntos.designsystem.component.ProfileAvatar
import com.jecsdev.papipuntos.designsystem.icon.PapiPuntosIcons
import com.jecsdev.papipuntos.designsystem.theme.PapiPuntosTheme
import com.jecsdev.papipuntos.feature.profile.model.Achievement

/** Production entry point. Profile is a read-only summary, so it just forwards nav. */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
) {
    ProfileScreenContent(
        papiName = ProfileSampleData.PAPI_NAME,
        mamiName = ProfileSampleData.MAMI_NAME,
        papiAvatar = ProfileSampleData.PAPI_AVATAR,
        mamiAvatar = ProfileSampleData.MAMI_AVATAR,
        papiPoints = ProfileSampleData.PAPI_POINTS,
        mamiPoints = ProfileSampleData.MAMI_POINTS,
        streakDays = ProfileSampleData.STREAK_DAYS,
        achievements = ProfileSampleData.achievements,
        onBack = onBack,
        onOpenPlans = onOpenPlans,
        modifier = modifier,
    )
}

/** Stateless, preview-friendly content with flat parameters. */
@Composable
fun ProfileScreenContent(
    papiName: String,
    mamiName: String,
    papiAvatar: String,
    mamiAvatar: String,
    papiPoints: Int,
    mamiPoints: Int,
    streakDays: Int,
    achievements: List<Achievement>,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
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
        PapiPuntosTopBar(title = "Nuestro perfil", onBack = onBack)

        Spacer(Modifier.height(12.dp))
        CoupleHero(
            papiName = papiName,
            mamiName = mamiName,
            papiAvatar = papiAvatar,
            mamiAvatar = mamiAvatar,
            papiPoints = papiPoints,
            mamiPoints = mamiPoints,
            streakDays = streakDays,
        )

        Spacer(Modifier.height(16.dp))
        PremiumBanner(onClick = onOpenPlans)

        Spacer(Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = PapiPuntosIcons.Trophy,
                contentDescription = null,
                tint = PapiPuntosTheme.colors.premiumIcon,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Logros desbloqueados",
                style = PapiPuntosTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.height(12.dp))
        AchievementsGrid(achievements = achievements)
    }
}

/** The two profiles facing each other on a soft gradient card, plus the streak chip. */
@Composable
private fun CoupleHero(
    papiName: String,
    mamiName: String,
    papiAvatar: String,
    mamiAvatar: String,
    papiPoints: Int,
    mamiPoints: Int,
    streakDays: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PapiPuntosTheme.shapes.xxxl)
            .background(
                Brush.linearGradient(
                    listOf(
                        PapiPuntosTheme.colors.mamiSoft,
                        MaterialTheme.colorScheme.background,
                        PapiPuntosTheme.colors.papiSoft,
                    ),
                ),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxxl)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PersonSummary(
                avatar = papiAvatar,
                name = papiName,
                points = papiPoints,
                color = PapiPuntosTheme.colors.papi,
            )
            Text(text = "💞", fontSize = 24.sp)
            PersonSummary(
                avatar = mamiAvatar,
                name = mamiName,
                points = mamiPoints,
                color = PapiPuntosTheme.colors.mami,
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PapiPuntosTheme.shapes.xxl)
                .background(Color.White.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = PapiPuntosIcons.Flame,
                contentDescription = null,
                tint = PapiPuntosTheme.colors.streak,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "$streakDays días",
                style = PapiPuntosTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "de racha haciendo gestos lindos",
                style = PapiPuntosTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PersonSummary(
    avatar: String,
    name: String,
    points: Int,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileAvatar(
            emoji = avatar,
            ringColor = color.copy(alpha = 0.4f),
            size = 64.dp,
            ringWidth = 4.dp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = PapiPuntosTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "$points pts",
            style = PapiPuntosTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = color,
        )
    }
}

/** Free-tier upsell row leading to the plans screen. */
@Composable
private fun PremiumBanner(onClick: () -> Unit) {
    val shape = PapiPuntosTheme.shapes.xxl
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(PapiPuntosTheme.colors.premiumBorder, PapiPuntosTheme.colors.mami),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PapiPuntosIcons.Crown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mejorar a Premium",
                style = PapiPuntosTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Desbloquea acciones y recompensas ilimitadas",
                style = PapiPuntosTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "Ver planes",
            style = PapiPuntosTheme.typography.labelMedium,
            color = PapiPuntosTheme.colors.mami,
        )
    }
}

/** Three-column grid of unlocked achievements. */
@Composable
private fun AchievementsGrid(achievements: List<Achievement>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        achievements.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { achievement ->
                    AchievementCell(achievement = achievement, modifier = Modifier.weight(1f))
                }
                // Keep the last row left-aligned if it is not full.
                repeat(3 - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementCell(achievement: Achievement, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(PapiPuntosTheme.shapes.xxl)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), PapiPuntosTheme.shapes.xxl)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = achievement.emoji, fontSize = 24.sp)
        Text(
            text = achievement.label,
            style = PapiPuntosTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    PapiPuntosTheme {
        ProfileScreenContent(
            papiName = ProfileSampleData.PAPI_NAME,
            mamiName = ProfileSampleData.MAMI_NAME,
            papiAvatar = ProfileSampleData.PAPI_AVATAR,
            mamiAvatar = ProfileSampleData.MAMI_AVATAR,
            papiPoints = ProfileSampleData.PAPI_POINTS,
            mamiPoints = ProfileSampleData.MAMI_POINTS,
            streakDays = ProfileSampleData.STREAK_DAYS,
            achievements = ProfileSampleData.achievements,
        )
    }
}
