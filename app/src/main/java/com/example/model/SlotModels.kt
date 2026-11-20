package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldWin
import com.example.ui.theme.RubyGlow
import com.example.ui.theme.VegasGold

object SlotConstants {
    const val STARTING_BALANCE = 1000
    const val SPIN_COST = 10
    const val THREE_MATCH_PAYOUT = 100
    const val TWO_MATCH_PAYOUT = 20
    const val REEL_COUNT = 3
}

enum class SlotSymbol(
    val displayName: String,
    val accentColor: Color,
) {
    CHERRY(displayName = "Cherry", accentColor = RubyGlow),
    LEMON(displayName = "Lemon", accentColor = VegasGold),
    BELL(displayName = "Bell", accentColor = VegasGold),
    SEVEN(displayName = "Lucky 7", accentColor = EmeraldWin),
}

enum class SpinOutcome {
    IDLE,
    SPINNING,
    THREE_MATCH,
    TWO_MATCH,
    NO_MATCH,
    OUT_OF_COINS,
}

data class SpinEvaluation(
    val outcome: SpinOutcome,
    val payout: Int,
    val winningIndices: Set<Int>,
    val message: String,
)

data class SlotUiState(
    val balance: Int = SlotConstants.STARTING_BALANCE,
    val reels: List<SlotSymbol> = listOf(SlotSymbol.SEVEN, SlotSymbol.CHERRY, SlotSymbol.BELL),
    val lockedReels: List<Boolean> = listOf(true, true, true),
    val winningIndices: Set<Int> = emptySet(),
    val isSpinning: Boolean = false,
    val outcome: SpinOutcome = SpinOutcome.IDLE,
    val resultMessage: String = "Tap SPIN to try your luck!",
    val lastPayout: Int = 0,
    val isMuted: Boolean = false,
) {
    val canSpin: Boolean
        get() = !isSpinning && balance >= SlotConstants.SPIN_COST

    val isOutOfCoins: Boolean
        get() = !isSpinning && balance < SlotConstants.SPIN_COST
}

/**
 * Evaluates a 3-reel spin result according to the paytable:
 * - 3 matching symbols: +100 coins
 * - 2 matching symbols: +20 coins
 * - Otherwise: 0 coins
 */
fun evaluateSpin(reels: List<SlotSymbol>): SpinEvaluation {
    require(reels.size == SlotConstants.REEL_COUNT) {
        "Expected ${SlotConstants.REEL_COUNT} reels, got ${reels.size}"
    }

    val (r0, r1, r2) = reels
    return when {
        r0 == r1 && r1 == r2 -> SpinEvaluation(
            outcome = SpinOutcome.THREE_MATCH,
            payout = SlotConstants.THREE_MATCH_PAYOUT,
            winningIndices = setOf(0, 1, 2),
            message = "JACKPOT! Triple ${r0.displayName} wins +${SlotConstants.THREE_MATCH_PAYOUT} coins!",
        )
        r0 == r1 -> twoMatchResult(r0, setOf(0, 1))
        r1 == r2 -> twoMatchResult(r1, setOf(1, 2))
        r0 == r2 -> twoMatchResult(r0, setOf(0, 2))
        else -> SpinEvaluation(
            outcome = SpinOutcome.NO_MATCH,
            payout = 0,
            winningIndices = emptySet(),
            message = "No match this time — spin again!",
        )
    }
}

private fun twoMatchResult(symbol: SlotSymbol, indices: Set<Int>): SpinEvaluation =
    SpinEvaluation(
        outcome = SpinOutcome.TWO_MATCH,
        payout = SlotConstants.TWO_MATCH_PAYOUT,
        winningIndices = indices,
        message = "Nice! Double ${symbol.displayName} wins +${SlotConstants.TWO_MATCH_PAYOUT} coins!",
    )
