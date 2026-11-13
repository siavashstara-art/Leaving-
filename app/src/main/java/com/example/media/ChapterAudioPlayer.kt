package com.example.media

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.AppLanguage
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AudioPlaybackState(
  val activeChapterId: String? = null,
  val isPlaying: Boolean = false,
  val isAudioAvailable: Boolean = false,
  val playbackSpeed: Float = 1.0f,
  val currentLineIndex: Int = 0,
  val totalLines: Int = 0,
  val statusMessageFa: String = "در حال بررسی منبع صوتی دستگاه...",
  val statusMessageEn: String = "Checking device speech source...",
)

class ChapterAudioPlayer(context: Context) {
  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = null
  private var ttsInitialized = false
  private var queuedLines: List<String> = emptyList()

  private val _state = MutableStateFlow(AudioPlaybackState())
  val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

  init {
    try {
      tts =
        TextToSpeech(appContext) { status ->
          ttsInitialized = status == TextToSpeech.SUCCESS
          refreshAvailability(AppLanguage.PERSIAN)
          if (ttsInitialized) {
            tts?.setOnUtteranceProgressListener(
              object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                  val idx = utteranceId?.removePrefix("line_")?.toIntOrNull() ?: 0
                  _state.update { it.copy(isPlaying = true, currentLineIndex = idx) }
                }

                override fun onDone(utteranceId: String?) {
                  val idx = utteranceId?.removePrefix("line_")?.toIntOrNull() ?: 0
                  val nextIdx = idx + 1
                  if (nextIdx < queuedLines.size && _state.value.isPlaying) {
                    speakLine(nextIdx)
                  } else {
                    _state.update {
                      it.copy(
                        isPlaying = false,
                        currentLineIndex = 0,
                        statusMessageFa = "پخش فصل به پایان رسید",
                        statusMessageEn = "Chapter playback finished",
                      )
                    }
                  }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                  _state.update {
                    it.copy(
                      isPlaying = false,
                      statusMessageFa = "خطا در پخش صوتی؛ رونوشت کامل متنی در دسترس است",
                      statusMessageEn = "Playback error; full transcript remains available",
                    )
                  }
                }
              }
            )
          }
        }
    } catch (_: Throwable) {
      ttsInitialized = false
      _state.update {
        it.copy(
          isAudioAvailable = false,
          isPlaying = false,
          statusMessageFa = "فایل صوتی فصل موجود نیست و موتور گفتار در دسترس نمی‌باشد",
          statusMessageEn = "Audio unavailable on this device; full transcript is available",
        )
      }
    }
  }

  fun refreshAvailability(language: AppLanguage) {
    val engine = tts
    if (!ttsInitialized || engine == null) {
      _state.update {
        it.copy(
          isAudioAvailable = false,
          isPlaying = false,
          statusMessageFa = "منبع صوتی روی این دستگاه موجود نیست — رونوشت کامل در دسترس است",
          statusMessageEn = "Audio unavailable on this device — full transcript available",
        )
      }
      return
    }
    val locale = if (language == AppLanguage.PERSIAN) Locale.forLanguageTag("fa-IR") else Locale.US
    val avail = engine.isLanguageAvailable(locale)
    val supported =
      avail == TextToSpeech.LANG_AVAILABLE ||
        avail == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
        avail == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
    _state.update {
      it.copy(
        isAudioAvailable = supported,
        statusMessageFa =
          if (supported) "گوینده صوتی آماده پخش است"
          else "صدای فارسی روی این دستگاه نصب نیست — رونوشت کامل در دسترس است",
        statusMessageEn =
          if (supported) "On-device voice narration ready"
          else "Voice data unavailable for this language — full transcript available",
      )
    }
  }

  fun togglePlay(chapterId: String, transcript: String, language: AppLanguage) {
    refreshAvailability(language)
    val current = _state.value
    if (!current.isAudioAvailable || tts == null) {
      // Strictly never simulate playback when no real audio source exists
      _state.update { it.copy(isPlaying = false) }
      return
    }
    if (current.isPlaying && current.activeChapterId == chapterId) {
      stop()
      return
    }
    val lines = transcript.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    if (lines.isEmpty()) return

    val locale = if (language == AppLanguage.PERSIAN) Locale.forLanguageTag("fa-IR") else Locale.US
    tts?.setLanguage(locale)
    tts?.setSpeechRate(current.playbackSpeed)
    queuedLines = lines
    _state.update {
      it.copy(
        activeChapterId = chapterId,
        isPlaying = true,
        currentLineIndex = 0,
        totalLines = lines.size,
        statusMessageFa = "در حال پخش صوتی فصل...",
        statusMessageEn = "Playing chapter narration...",
      )
    }
    speakLine(0)
  }

  private fun speakLine(index: Int) {
    val line = queuedLines.getOrNull(index) ?: return
    tts?.speak(line, TextToSpeech.QUEUE_FLUSH, Bundle(), "line_$index")
  }

  fun setSpeed(speed: Float) {
    val clamped = speed.coerceIn(0.75f, 1.5f)
    tts?.setSpeechRate(clamped)
    _state.update { it.copy(playbackSpeed = clamped) }
  }

  fun stop() {
    tts?.stop()
    _state.update {
      it.copy(
        isPlaying = false,
        statusMessageFa =
          if (it.isAudioAvailable) "پخش متوقف شد"
          else "منبع صوتی در دسترس نیست",
        statusMessageEn =
          if (it.isAudioAvailable) "Playback paused" else "Audio unavailable",
      )
    }
  }

  fun release() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
