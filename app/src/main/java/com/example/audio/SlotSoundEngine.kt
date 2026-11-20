package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.sin

interface SlotAudioPlayer {
    var isMuted: Boolean
    fun playSpinTick()
    fun playReelStop(reelIndex: Int)
    fun playSmallWin()
    fun playJackpot()
    fun playOutOfCoins()
    fun playCoinRefill()
    fun release()
}

/**
 * Pure Kotlin PCM sound synthesizer using Android's AudioTrack.
 * Generates crisp retro casino sound effects at runtime without external audio files.
 */
class SlotSoundEngine : SlotAudioPlayer {

    override var isMuted: Boolean = false

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val sampleRate = 22050

    // Pre-synthesized PCM clips for low-latency playback
    private val spinTickClip by lazy { synthesizeTone(frequencyHz = 620.0, durationMs = 26, volume = 0.22f) }
    private val reelStopClips by lazy {
        listOf(
            synthesizeTone(frequencyHz = 330.0, durationMs = 65, volume = 0.32f),
            synthesizeTone(frequencyHz = 392.0, durationMs = 65, volume = 0.32f),
            synthesizeTone(frequencyHz = 494.0, durationMs = 75, volume = 0.36f),
        )
    }
    private val smallWinClip by lazy {
        synthesizeSequence(
            notes = listOf(523.25 to 90, 659.25 to 90, 783.99 to 170),
            volume = 0.36f,
        )
    }
    private val jackpotClip by lazy {
        synthesizeSequence(
            notes = listOf(
                523.25 to 80,
                659.25 to 80,
                783.99 to 80,
                1046.50 to 120,
                783.99 to 80,
                1046.50 to 240,
            ),
            volume = 0.42f,
        )
    }
    private val outOfCoinsClip by lazy {
        synthesizeSequence(
            notes = listOf(349.23 to 130, 261.63 to 220),
            volume = 0.32f,
        )
    }
    private val coinRefillClip by lazy {
        synthesizeSequence(
            notes = listOf(783.99 to 75, 1046.50 to 140),
            volume = 0.34f,
        )
    }

    override fun playSpinTick() = playClip(spinTickClip)

    override fun playReelStop(reelIndex: Int) {
        val clip = reelStopClips.getOrElse(reelIndex) { reelStopClips.last() }
        playClip(clip)
    }

    override fun playSmallWin() = playClip(smallWinClip)

    override fun playJackpot() = playClip(jackpotClip)

    override fun playOutOfCoins() = playClip(outOfCoinsClip)

    override fun playCoinRefill() = playClip(coinRefillClip)

    override fun release() {
        executor.shutdownNow()
    }

    private fun playClip(samples: ShortArray) {
        if (isMuted || executor.isShutdown) return
        executor.execute {
            runCatching {
                val bufferSizeBytes = samples.size * 2
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSizeBytes)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                val durationMs = (samples.size * 1000L) / sampleRate
                Thread.sleep(durationMs + 30L)
                track.stop()
                track.release()
            }
        }
    }

    private fun synthesizeTone(frequencyHz: Double, durationMs: Int, volume: Float): ShortArray {
        val count = (sampleRate * durationMs) / 1000
        val out = ShortArray(count)
        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val envelope = when {
                progress < 0.08 -> progress / 0.08
                else -> 1.0 - ((progress - 0.08) / 0.92)
            }.coerceIn(0.0, 1.0)
            val angle = 2.0 * PI * frequencyHz * i / sampleRate
            // Blend fundamental sine with subtle 2nd harmonic for a warm arcade timbre
            val wave = 0.8 * sin(angle) + 0.2 * sin(angle * 2.0)
            out[i] = (wave * envelope * volume * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    private fun synthesizeSequence(notes: List<Pair<Double, Int>>, volume: Float): ShortArray {
        val clips = notes.map { (freq, ms) -> synthesizeTone(freq, ms, volume) }
        val total = clips.sumOf { it.size }
        val result = ShortArray(total)
        var offset = 0
        for (clip in clips) {
            clip.copyInto(result, destinationOffset = offset)
            offset += clip.size
        }
        return result
    }
}
