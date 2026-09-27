package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    var isEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.Default)

    enum class SoundEffect {
        ATTACK,
        CRITICAL,
        SPELL,
        HEAL,
        DEFEND,
        VICTORY,
        DEFEAT,
        BUTTON
    }

    fun play(effect: SoundEffect) {
        if (!isEnabled) return
        scope.launch {
            try {
                when (effect) {
                    SoundEffect.ATTACK -> playToneSequence(listOf(Pair(440, 40), Pair(330, 40), Pair(220, 50)))
                    SoundEffect.CRITICAL -> playNoiseBurst(120)
                    SoundEffect.SPELL -> playToneSequence(listOf(Pair(523, 50), Pair(659, 50), Pair(784, 60), Pair(1046, 100)))
                    SoundEffect.HEAL -> playToneSequence(listOf(Pair(587, 60), Pair(740, 60), Pair(880, 80)))
                    SoundEffect.DEFEND -> playToneSequence(listOf(Pair(260, 70), Pair(240, 70)))
                    SoundEffect.VICTORY -> playToneSequence(listOf(Pair(523, 80), Pair(659, 80), Pair(784, 80), Pair(1046, 200)))
                    SoundEffect.DEFEAT -> playToneSequence(listOf(Pair(392, 100), Pair(349, 100), Pair(311, 120), Pair(261, 240)))
                    SoundEffect.BUTTON -> playToneSequence(listOf(Pair(800, 25)))
                }
            } catch (_: Exception) {
                // Ignore audio track exceptions
            }
        }
    }

    private fun playToneSequence(tones: List<Pair<Int, Int>>) {
        val sampleRate = 22050
        val totalMs = tones.sumOf { it.second }
        val totalSamples = (sampleRate * totalMs / 1000)
        val buffer = ShortArray(totalSamples)

        var sampleIndex = 0
        for ((freq, durationMs) in tones) {
            val count = (sampleRate * durationMs / 1000)
            for (i in 0 until count) {
                if (sampleIndex >= buffer.size) break
                val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / freq)
                // 8-bit square/sine hybrid retro chiptune waveform
                val wave = if (sin(angle) > 0.0) 0.6 else -0.6
                // Fade out envelope
                val envelope = 1.0 - (i.toDouble() / count)
                buffer[sampleIndex++] = (wave * envelope * Short.MAX_VALUE * 0.35).toInt().toShort()
            }
        }

        playBuffer(buffer, sampleRate)
    }

    private fun playNoiseBurst(durationMs: Int) {
        val sampleRate = 22050
        val count = (sampleRate * durationMs / 1000)
        val buffer = ShortArray(count)
        val random = java.util.Random()
        for (i in 0 until count) {
            val env = 1.0 - (i.toDouble() / count)
            val noise = (random.nextDouble() * 2 - 1)
            buffer[i] = (noise * env * Short.MAX_VALUE * 0.4).toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
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
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        track.setNotificationMarkerPosition(buffer.size)
        track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(t: AudioTrack?) {
                t?.release()
            }
            override fun onPeriodicNotification(t: AudioTrack?) {}
        })
    }
}
