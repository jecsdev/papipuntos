package com.jecsdev.papipuntos.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jecsdev.papipuntos.domain.action.ActionRepository
import com.jecsdev.papipuntos.domain.reward.RewardRepository
import com.jecsdev.papipuntos.feature.profile.model.Achievement
import com.jecsdev.papipuntos.model.Action
import com.jecsdev.papipuntos.model.ActionStatus
import com.jecsdev.papipuntos.model.Player
import com.jecsdev.papipuntos.model.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val papiName: String = "Papi",
    val mamiName: String = "Mami",
    val papiAvatar: String = "💙",
    val mamiAvatar: String = "💗",
    val papiPoints: Int = 0,
    val mamiPoints: Int = 0,
    val streakDays: Int = 0,
    val achievements: List<Achievement> = emptyList(),
)

/** Derives the profile summary solely from persisted profiles, approved actions, and redemptions. */
class ProfileViewModel(
    private val activePlayer: Player,
    profiles: List<Profile>,
    actionRepository: ActionRepository,
    rewardRepository: RewardRepository,
) : ViewModel() {
    private val papi = profiles.firstOrNull { it.player == Player.Papi }
    private val mami = profiles.firstOrNull { it.player == Player.Mami }

    val uiState = combine(actionRepository.observeAll(), rewardRepository.observeRedemptions()) { actions, redemptions ->
        val approved = actions.filter { it.status == ActionStatus.APPROVED }
        ProfileUiState(
            papiName = papi?.name ?: "Papi",
            mamiName = mami?.name ?: "Mami",
            papiAvatar = papi?.emoji ?: "💙",
            mamiAvatar = mami?.emoji ?: "💗",
            papiPoints = approved.pointsFor(Player.Papi) - redemptions.spentBy(Player.Papi),
            mamiPoints = approved.pointsFor(Player.Mami) - redemptions.spentBy(Player.Mami),
            streakDays = approved.streakDays(),
            achievements = approved.achievementsFor(activePlayer),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private fun List<Action>.pointsFor(player: Player) = filter { it.beneficiary == player }.sumOf { it.points }

    private fun List<com.jecsdev.papipuntos.model.Redemption>.spentBy(player: Player) =
        filter { it.player == player }.sumOf { it.rewardCost }

    private fun List<Action>.streakDays(): Int {
        val days = map { it.createdAtEpochMillis / MILLIS_PER_DAY }.distinct().sortedDescending()
        return days.zipWithNext().takeWhile { (later, earlier) -> later - earlier == 1L }.size + if (days.isEmpty()) 0 else 1
    }

    private fun List<Action>.achievementsFor(player: Player): List<Achievement> {
        val mine = filter { it.beneficiary == player }
        return buildList {
            if (mine.size >= 7) add(Achievement("🏆", "Primera semana"))
            if (mine.size >= 100) add(Achievement("💯", "100 acciones"))
            if (mine.any { it.label.contains("masaje", ignoreCase = true) }) add(Achievement("💆", "Maestro del masaje"))
            if (mine.any { it.label.contains("cocina", ignoreCase = true) || it.emoji == "🍳" }) add(Achievement("🍳", "Chef de la casa"))
            if (mine.any { it.label.contains("cita", ignoreCase = true) || it.label.contains("salida", ignoreCase = true) }) add(Achievement("💕", "Cita perfecta"))
            if (mine.any { it.label.contains("noche", ignoreCase = true) }) add(Achievement("🌙", "Detalle nocturno"))
        }
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
