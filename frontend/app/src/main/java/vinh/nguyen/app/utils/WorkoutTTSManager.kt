package vinh.nguyen.app.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Manages Text-to-Speech announcements for workout rep counting
 */
class WorkoutTTSManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    companion object {
        private const val TAG = "WorkoutTTSManager"

        // Milestone phrases (every 10 reps)
        private val MILESTONE_PHRASES = listOf(
            "Milestone! %d reps! Keep crushing it!",
            "Amazing! %d reps! You're on fire!",
            "Incredible! %d reps! Don't stop now!",
            "Fantastic! %d reps! You're unstoppable!",
            "Outstanding! %d reps! Keep pushing!"
        )
    }

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isInitialized = true
                Log.i(TAG, "TTS initialized successfully")
            } else {
                Log.e(TAG, "TTS initialization failed")
            }
        }
    }

    /**
     * Announces the current rep count
     * @param repCount The number of reps completed
     * @param isTTSEnabled Whether TTS is enabled in settings
     */
    fun announceRep(repCount: Int, isTTSEnabled: Boolean) {
        if (!isTTSEnabled || !isInitialized || repCount <= 0) {
            return
        }

        val announcement = if (repCount % 10 == 0) {
            // Milestone announcement (every 10 reps)
            MILESTONE_PHRASES.random().format(repCount)
        } else {
            // Regular rep announcement
            "$repCount reps"
        }

        speak(announcement)
    }

    /**
     * Speaks the given text using TTS
     */
    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        Log.d(TAG, "TTS: $text")
    }

    /**
     * Stops any ongoing speech
     */
    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
    }

    /**
     * Shuts down TTS engine - call this in onDestroy/onCleared
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
        Log.i(TAG, "TTS shutdown")
    }
}