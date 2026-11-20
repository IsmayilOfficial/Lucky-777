package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SlotAudioPlayer
import com.example.audio.SlotSoundEngine
import com.example.model.SlotConstants
import com.example.model.SlotSymbol
import com.example.model.SlotUiState
import com.example.model.SpinOutcome
import com.example.model.evaluateSpin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class SlotMachineViewModel(
    private val soundPlayer: SlotAudioPlayer,
    private val random: Random = Random.Default,
) : ViewModel() {

    constructor() : this(SlotSoundEngine(), Random.Default)

    private val _uiState = MutableStateFlow(SlotUiState())
    val uiState: StateFlow<SlotUiState> = _uiState.asStateFlow()

    private var spinJob: Job? = null

    fun spin() {
        val current = _uiState.value
        if (!current.canSpin) return

        val balanceAfterBet = current.balance - SlotConstants.SPIN_COST
        val finalReels = List(SlotConstants.REEL_COUNT) {
            SlotSymbol.entries[random.nextInt(SlotSymbol.entries.size)]
        }

        _uiState.update {
            it.copy(
                balance = balanceAfterBet,
                lockedReels = listOf(false, false, false),
                winningIndices = emptySet(),
                isSpinning = true,
                outcome = SpinOutcome.SPINNING,
                resultMessage = "Reels spinning… good luck!",
                lastPayout = 0,
            )
        }

        spinJob?.cancel()
        spinJob = viewModelScope.launch {
            val symbols = SlotSymbol.entries
            val totalSteps = 18
            val stepDelayMs = 65L

            for (step in 1..totalSteps) {
                val lockReel0 = step >= 8
                val lockReel1 = step >= 13
                val lockReel2 = step >= totalSteps

                val currentReels = listOf(
                    if (lockReel0) finalReels[0] else symbols[(step + random.nextInt(symbols.size)) % symbols.size],
                    if (lockReel1) finalReels[1] else symbols[(step + 1 + random.nextInt(symbols.size)) % symbols.size],
                    if (lockReel2) finalReels[2] else symbols[(step + 2 + random.nextInt(symbols.size)) % symbols.size],
                )

                val previousLocks = _uiState.value.lockedReels
                val newLocks = listOf(lockReel0, lockReel1, lockReel2)

                _uiState.update {
                    it.copy(
                        reels = currentReels,
                        lockedReels = newLocks,
                    )
                }

                when {
                    !previousLocks[0] && lockReel0 -> soundPlayer.playReelStop(0)
                    !previousLocks[1] && lockReel1 -> soundPlayer.playReelStop(1)
                    !previousLocks[2] && lockReel2 -> soundPlayer.playReelStop(2)
                    step % 2 == 0 -> soundPlayer.playSpinTick()
                }

                if (!lockReel2) {
                    delay(stepDelayMs)
                }
            }

            finishSpin(finalReels, balanceAfterBet)
        }
    }

    private fun finishSpin(finalReels: List<SlotSymbol>, balanceAfterBet: Int) {
        val evaluation = evaluateSpin(finalReels)
        val updatedBalance = balanceAfterBet + evaluation.payout
        val outOfCoins = updatedBalance < SlotConstants.SPIN_COST

        val finalOutcome = if (outOfCoins && evaluation.payout == 0) {
            SpinOutcome.OUT_OF_COINS
        } else {
            evaluation.outcome
        }

        val finalMessage = if (outOfCoins && evaluation.payout == 0) {
            "Out of coins! Tap Reset to refill 1,000 coins."
        } else {
            evaluation.message
        }

        _uiState.update {
            it.copy(
                balance = updatedBalance,
                reels = finalReels,
                lockedReels = listOf(true, true, true),
                winningIndices = evaluation.winningIndices,
                isSpinning = false,
                outcome = finalOutcome,
                resultMessage = finalMessage,
                lastPayout = evaluation.payout,
            )
        }

        when {
            evaluation.outcome == SpinOutcome.THREE_MATCH -> soundPlayer.playJackpot()
            evaluation.outcome == SpinOutcome.TWO_MATCH -> soundPlayer.playSmallWin()
            outOfCoins -> soundPlayer.playOutOfCoins()
        }
    }

    fun resetCoins() {
        if (_uiState.value.isSpinning) return
        _uiState.update {
            it.copy(
                balance = SlotConstants.STARTING_BALANCE,
                winningIndices = emptySet(),
                outcome = SpinOutcome.IDLE,
                resultMessage = "Fresh stack! 1,000 coins ready.",
                lastPayout = 0,
            )
        }
        soundPlayer.playCoinRefill()
    }

    fun toggleMute() {
        val nextMuted = !_uiState.value.isMuted
        soundPlayer.isMuted = nextMuted
        _uiState.update { it.copy(isMuted = nextMuted) }
    }

    override fun onCleared() {
        super.onCleared()
        spinJob?.cancel()
        soundPlayer.release()
    }
}
