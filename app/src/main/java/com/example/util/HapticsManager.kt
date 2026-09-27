package com.example.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.model.HapticPulseType

class HapticsManager(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isEnabled: Boolean = true
    var intensity: String = "HEAVY" // LIGHT, MEDIUM, HEAVY

    fun trigger(type: HapticPulseType) {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return

        val amplitudeScale = when (intensity) {
            "LIGHT" -> 0.4f
            "MEDIUM" -> 0.7f
            else -> 1.0f
        }

        try {
            when (type) {
                HapticPulseType.BUTTON_CLICK -> vibratePattern(longArrayOf(0, 20), intArrayOf(0, (80 * amplitudeScale).toInt()))
                HapticPulseType.HERO_ATTACK -> vibratePattern(longArrayOf(0, 50), intArrayOf(0, (180 * amplitudeScale).toInt()))
                HapticPulseType.CRITICAL_HIT -> vibratePattern(
                    longArrayOf(0, 60, 40, 140),
                    intArrayOf(0, (220 * amplitudeScale).toInt(), 0, (255 * amplitudeScale).toInt())
                )
                HapticPulseType.DAMAGE_TAKEN -> vibratePattern(
                    longArrayOf(0, 70, 30, 90),
                    intArrayOf(0, (190 * amplitudeScale).toInt(), 0, (150 * amplitudeScale).toInt())
                )
                HapticPulseType.SPELL_CAST -> vibratePattern(
                    longArrayOf(0, 30, 20, 40, 20, 80),
                    intArrayOf(0, (100 * amplitudeScale).toInt(), 0, (160 * amplitudeScale).toInt(), 0, (240 * amplitudeScale).toInt())
                )
                HapticPulseType.VICTORY_FANFARE -> vibratePattern(
                    longArrayOf(0, 60, 80, 60, 80, 120, 60, 200),
                    intArrayOf(0, (150 * amplitudeScale).toInt(), 0, (180 * amplitudeScale).toInt(), 0, (220 * amplitudeScale).toInt(), 0, (255 * amplitudeScale).toInt())
                )
                HapticPulseType.DEFEAT_RUMBLE -> vibratePattern(
                    longArrayOf(0, 150, 60, 250),
                    intArrayOf(0, (200 * amplitudeScale).toInt(), 0, (120 * amplitudeScale).toInt())
                )
            }
        } catch (_: Exception) {
            // Safe fallback if device driver restricts custom amplitudes
            fallbackVibrate(60)
        }
    }

    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        if (vibrator == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val safeAmplitudes = amplitudes.map { it.coerceIn(0, 255) }.toIntArray()
            val effect = VibrationEffect.createWaveform(timings, safeAmplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, -1)
        }
    }

    private fun fallbackVibrate(milliseconds: Long) {
        if (vibrator == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(milliseconds)
        }
    }
}
