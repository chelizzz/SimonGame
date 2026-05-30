package com.example.simongame

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin


class SoundManager {
    // Standard sample rate for audio CDs (44.1 kHz: 44,100 measurements per second)
    private val sampleRate = 44100

    // Reference: https://introcs.cs.princeton.edu/java/21function/Tone.java.html
    // Generates a PCM buffer containing a pure sine wave at the given frequency (hz).
    // Duration is set to 350 ms to match the SimonButton glow animation length.
    private fun audioData(hz: Double, ms: Int = 350): ShortArray {
        // Total number of audio samples required for the given duration
        val n = sampleRate * ms / 1000

        // Initialize the array and populate it by iterating over each sample index 'i'
        return ShortArray(n) { i ->
            // Fade-out effect applied to the last 20% of the samples to prevent an abrupt "click" when the sound stops
            val fade = if (i > n * 0.8) (n - i) / (n * 0.2) else 1.0

            // Scale the sine wave from [-1, 1] to the 16-bit PCM range [-32767, 32767]
            (sin(2.0 * PI * i * hz / sampleRate) * Short.MAX_VALUE * fade).toInt().toShort()
        }
    }

    // The map holds pre-calculated audio arrays of Shorts (16-bit) in RAM
    private val buffers = mapOf(
        "R"    to audioData(329.63),
        "G"    to audioData(261.63),
        "B"    to audioData(392.00),
        "M"    to audioData(349.23),
        "Y"    to audioData(440.00),
        "C"    to audioData(293.66),
        "fail" to audioData(200.00, 800) // bass sound of long duration (800ms)
    )


    // Keeps track of the currently active player instance of AudioTrack
    private var currentPlayer: AudioTrack? = null

    // Plays the sound associated with the provided key ("R", "G", etc.)
    fun play(key: String) {
        // Fetch the pre-calculated array from the map, exit if the key is invalid
        val buf = buffers[key] ?: return

        // Stops and destroys the previous instance with its associated sound to avoid overlapping
        currentPlayer?.stop()
        currentPlayer?.release()

        // Reference: https://developer.android.com/reference/android/media/AudioTrack.Builder
        // Build a new AudioTrack perfectly sized for this specific sound array
        currentPlayer = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME) // optimizes audio for gaming
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // indicates short UI/button sounds
                    .build()
            )
            // Reference: https://developer.android.com/reference/android/media/AudioFormat.Builder
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT) // high-quality audio
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) // consumes less resources than stereo
                    .build()
            )
            // Allocate the exact amount of RAM needed: multiplying by 2 because each Short occupies 2 Bytes
            .setBufferSizeInBytes(buf.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC) // in static mode there's no latency
            .build()

        // Loads the entire sound file into memory all at once before the call to play
        currentPlayer?.write(buf, 0, buf.size)

        // The sound is loaded into RAM and played
        currentPlayer?.play()
    }


    // Cleanup to prevent memory leaks when the app closes (called in GameViewModel.onCleared())
    fun release() {
        currentPlayer?.release()
        currentPlayer = null
    }
}