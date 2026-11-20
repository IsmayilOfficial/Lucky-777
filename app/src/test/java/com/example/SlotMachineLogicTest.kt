package com.example

import com.example.audio.SlotAudioPlayer
import com.example.model.SlotConstants
import com.example.model.SlotSymbol
import com.example.model.SpinOutcome
import com.example.model.evaluateSpin
import com.example.viewmodel.SlotMachineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

private class FakeSoundPlayer : SlotAudioPlayer {
    override var isMuted: Boolean = false
    var spinTicks = 0
    var reelStops = 0
    var smallWins = 0
    var jackpots = 0
    var outOfCoins = 0
    var refills = 0
    var released = false

    override fun playSpinTick() { spinTicks++ }
    override fun playReelStop(reelIndex: Int) { reelStops++ }
    override fun playSmallWin() { smallWins++ }
    override fun playJackpot() { jackpots++ }
    override fun playOutOfCoins() { outOfCoins++ }
    override fun playCoinRefill() { refills++ }
    override fun release() { released = true }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SlotMachineLogicTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `evaluateSpin awards 100 coins on three matching symbols`() {
        val result = evaluateSpin(listOf(SlotSymbol.SEVEN, SlotSymbol.SEVEN, SlotSymbol.SEVEN))
        assertEquals(SpinOutcome.THREE_MATCH, result.outcome)
        assertEquals(SlotConstants.THREE_MATCH_PAYOUT, result.payout)
        assertEquals(setOf(0, 1, 2), result.winningIndices)
    }

    @Test
    fun `evaluateSpin awards 20 coins on two matching symbols`() {
        val pair01 = evaluateSpin(listOf(SlotSymbol.CHERRY, SlotSymbol.CHERRY, SlotSymbol.BELL))
        assertEquals(SpinOutcome.TWO_MATCH, pair01.outcome)
        assertEquals(20, pair01.payout)
        assertEquals(setOf(0, 1), pair01.winningIndices)

        val pair12 = evaluateSpin(listOf(SlotSymbol.LEMON, SlotSymbol.BELL, SlotSymbol.BELL))
        assertEquals(SpinOutcome.TWO_MATCH, pair12.outcome)
        assertEquals(20, pair12.payout)
        assertEquals(setOf(1, 2), pair12.winningIndices)

        val pair02 = evaluateSpin(listOf(SlotSymbol.LEMON, SlotSymbol.CHERRY, SlotSymbol.LEMON))
        assertEquals(SpinOutcome.TWO_MATCH, pair02.outcome)
        assertEquals(20, pair02.payout)
        assertEquals(setOf(0, 2), pair02.winningIndices)
    }

    @Test
    fun `evaluateSpin awards 0 coins when all symbols differ`() {
        val result = evaluateSpin(listOf(SlotSymbol.CHERRY, SlotSymbol.LEMON, SlotSymbol.BELL))
        assertEquals(SpinOutcome.NO_MATCH, result.outcome)
        assertEquals(0, result.payout)
        assertTrue(result.winningIndices.isEmpty())
    }

    @Test
    fun `viewModel starts with 1000 coins and toggles mute`() {
        val sound = FakeSoundPlayer()
        val vm = SlotMachineViewModel(soundPlayer = sound, random = Random(42))

        assertEquals(1000, vm.uiState.value.balance)
        assertFalse(vm.uiState.value.isMuted)

        vm.toggleMute()
        assertTrue(vm.uiState.value.isMuted)
        assertTrue(sound.isMuted)
    }

    @Test
    fun `viewModel deducts 10 coins per spin and updates balance on completion`() = runTest {
        val sound = FakeSoundPlayer()
        val vm = SlotMachineViewModel(soundPlayer = sound, random = Random(123))

        vm.spin()
        assertTrue(vm.uiState.value.isSpinning)
        assertEquals(990, vm.uiState.value.balance)

        advanceUntilIdle()

        val finished = vm.uiState.value
        assertFalse(finished.isSpinning)
        assertEquals(listOf(true, true, true), finished.lockedReels)
        assertEquals(990 + finished.lastPayout, finished.balance)
        assertEquals(3, sound.reelStops)
    }

    @Test
    fun `resetCoins restores 1000 coins and plays refill sound`() {
        val sound = FakeSoundPlayer()
        val vm = SlotMachineViewModel(soundPlayer = sound, random = Random(7))

        vm.resetCoins()
        assertEquals(1000, vm.uiState.value.balance)
        assertEquals(1, sound.refills)
    }
}
