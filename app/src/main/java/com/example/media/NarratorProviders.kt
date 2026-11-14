package com.example.media

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.AudioExportFormat
import com.example.model.NarratorType
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.abs
import kotlin.math.sqrt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

sealed interface NarrationGenerationResult {
  data class Success(
    val file: File,
    val format: AudioExportFormat,
    val durationMs: Long,
    val peakAmplitude: Int,
    val clippingDetected: Boolean,
  ) : NarrationGenerationResult

  data class Unavailable(
    val reasonFa: String,
    val reasonEn: String,
  ) : NarrationGenerationResult
}

/** Modular contract allowing AI device TTS, authorized Cloud TTS proxies, and Human/Author sources. */
interface NarratorProvider {
  val type: NarratorType

  suspend fun synthesizeSegment(
    segmentId: String,
    scriptTextFa: String,
    outputDir: File,
    preferredFormat: AudioExportFormat,
  ): NarrationGenerationResult
}

class DevicePersianTtsNarrator(context: Context) : NarratorProvider {
  override val type: NarratorType = NarratorType.AI_PERSIAN
  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = null
  private var initialized = false
  private var persianSupported = false

  init {
    try {
      tts =
        TextToSpeech(appContext) { status ->
          initialized = status == TextToSpeech.SUCCESS
          if (initialized) {
            val avail = tts?.isLanguageAvailable(Locale.forLanguageTag("fa-IR")) ?: -1
            persianSupported =
              avail == TextToSpeech.LANG_AVAILABLE ||
                avail == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                avail == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
          }
        }
    } catch (_: Throwable) {
      initialized = false
      persianSupported = false
    }
  }

  fun isPersianAvailable(): Boolean = initialized && persianSupported

  override suspend fun synthesizeSegment(
    segmentId: String,
    scriptTextFa: String,
    outputDir: File,
    preferredFormat: AudioExportFormat,
  ): NarrationGenerationResult =
    withContext(Dispatchers.IO) {
      val engine = tts
      if (!initialized || !persianSupported || engine == null) {
        return@withContext NarrationGenerationResult.Unavailable(
          reasonFa = "موتور گفتار فارسی (fa-IR) روی این دستگاه نصب نیست؛ می‌توانید صدای مؤلف را ضبط کنید یا فایل استودیویی وارد نمایید.",
          reasonEn = "Persian (fa-IR) voice pack is unavailable on this device. Record Author Voice or import studio audio instead.",
        )
      }
      outputDir.mkdirs()
      val wavFile = File(outputDir, "${segmentId}_tts.wav")
      engine.language = Locale.forLanguageTag("fa-IR")

      val synthesized =
        suspendCancellableCoroutine { cont ->
          val utteranceId = "synth_$segmentId"
          engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
              override fun onStart(id: String?) {}

              override fun onDone(id: String?) {
                if (id == utteranceId && cont.isActive) cont.resume(true)
              }

              @Deprecated("Deprecated in Java")
              override fun onError(id: String?) {
                if (id == utteranceId && cont.isActive) cont.resume(false)
              }
            }
          )
          val code = engine.synthesizeToFile(scriptTextFa, Bundle(), wavFile, utteranceId)
          if (code != TextToSpeech.SUCCESS && cont.isActive) {
            cont.resume(false)
          }
        }

      if (!synthesized || !wavFile.exists() || wavFile.length() <= 44L) {
        return@withContext NarrationGenerationResult.Unavailable(
          reasonFa = "تولید فایل صوتی از موتور TTS دستگاه ناموفق بود.",
          reasonEn = "Device TTS file synthesis failed.",
        )
      }

      val finalFile =
        if (preferredFormat == AudioExportFormat.M4A) {
          val m4a = File(outputDir, "${segmentId}_tts.m4a")
          if (AudioEncoderExporter.encodeWavToM4a(wavFile, m4a)) m4a else wavFile
        } else {
          wavFile
        }
      val finalFormat =
        if (finalFile.extension == "m4a") AudioExportFormat.M4A else AudioExportFormat.WAV
      val duration = AudioEncoderExporter.extractDurationMs(finalFile)
      NarrationGenerationResult.Success(
        file = finalFile,
        format = finalFormat,
        durationMs = duration,
        peakAmplitude = 0,
        clippingDetected = false,
      )
    }

  fun release() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}

/**
 * Modular contract for external Cloud Persian TTS providers (Azure fa-IR, Google Cloud Gemini-TTS,
 * ElevenLabs v3). Requires an author-managed authenticated backend proxy — client APKs never embed
 * vendor API secrets.
 */
class CloudTtsProviderAdapter : NarratorProvider {
  override val type: NarratorType = NarratorType.AI_PERSIAN

  override suspend fun synthesizeSegment(
    segmentId: String,
    scriptTextFa: String,
    outputDir: File,
    preferredFormat: AudioExportFormat,
  ): NarrationGenerationResult =
    NarrationGenerationResult.Unavailable(
      reasonFa =
        "سرویس ابری TTS (مانند Azure fa-IR یا Gemini-TTS) به سرور واسط امن نیاز دارد و کلید API هرگز در نسخه کلاینت قرار نمی‌گیرد.",
      reasonEn =
        "External Cloud TTS requires a secure server proxy; API credentials are never embedded in client APKs.",
    )
}

data class RecordingMeterState(
  val isRecording: Boolean = false,
  val activeSegmentId: String? = null,
  val elapsedMs: Long = 0L,
  val normalizedLevel: Float = 0f,
  val peakAmplitude: Int = 0,
  val clippingDetected: Boolean = false,
  val errorMessageFa: String? = null,
)

class AuthorVoiceRecorder {
  private val _meter = MutableStateFlow(RecordingMeterState())
  val meter: StateFlow<RecordingMeterState> = _meter.asStateFlow()

  private var audioRecord: AudioRecord? = null
  private var recordJob: Job? = null
  private var currentPcmFile: File? = null
  private var currentOutputDir: File? = null
  private var currentSegmentId: String? = null

  @SuppressLint("MissingPermission")
  fun startRecording(
    segmentId: String,
    outputDir: File,
    scope: CoroutineScope,
  ): Boolean {
    if (_meter.value.isRecording) return false
    val minBuffer =
      AudioRecord.getMinBufferSize(
        AudioEncoderExporter.SAMPLE_RATE_HZ,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
      )
    if (minBuffer <= 0) {
      _meter.update { it.copy(errorMessageFa = "سخت‌افزار ضبط صدا روی این دستگاه در دسترس نیست.") }
      return false
    }

    return try {
      val recorder =
        AudioRecord(
          MediaRecorder.AudioSource.MIC,
          AudioEncoderExporter.SAMPLE_RATE_HZ,
          AudioFormat.CHANNEL_IN_MONO,
          AudioFormat.ENCODING_PCM_16BIT,
          minBuffer * 2,
        )
      if (recorder.state != AudioRecord.STATE_INITIALIZED) {
        recorder.release()
        _meter.update { it.copy(errorMessageFa = "راه‌اندازی میکروفون ممکن نشد.") }
        return false
      }

      outputDir.mkdirs()
      val pcmFile = File(outputDir, "${segmentId}_raw.pcm")
      currentPcmFile = pcmFile
      currentOutputDir = outputDir
      currentSegmentId = segmentId
      audioRecord = recorder
      recorder.startRecording()

      val startAt = System.currentTimeMillis()
      _meter.value =
        RecordingMeterState(
          isRecording = true,
          activeSegmentId = segmentId,
          elapsedMs = 0L,
          normalizedLevel = 0f,
          peakAmplitude = 0,
          clippingDetected = false,
          errorMessageFa = null,
        )

      recordJob =
        scope.launch(Dispatchers.IO) {
          val shortBuf = ShortArray(minBuffer / 2)
          val byteBuf = ByteArray(shortBuf.size * 2)
          var peakMax = 0
          var clipped = false
          FileOutputStream(pcmFile).use { fos ->
            while (isActive && _meter.value.isRecording) {
              val read = recorder.read(shortBuf, 0, shortBuf.size)
              if (read > 0) {
                var sumSq = 0.0
                for (i in 0 until read) {
                  val s = shortBuf[i].toInt()
                  val a = abs(s)
                  if (a > peakMax) peakMax = a
                  if (a >= 32000) clipped = true
                  sumSq += (s * s).toDouble()
                  byteBuf[i * 2] = (s and 0xFF).toByte()
                  byteBuf[i * 2 + 1] = ((s ushr 8) and 0xFF).toByte()
                }
                fos.write(byteBuf, 0, read * 2)
                val rms = sqrt(sumSq / read.coerceAtLeast(1)) / 32768.0
                val level = (rms * 3.0).toFloat().coerceIn(0f, 1f)
                _meter.update {
                  it.copy(
                    elapsedMs = System.currentTimeMillis() - startAt,
                    normalizedLevel = level,
                    peakAmplitude = peakMax,
                    clippingDetected = clipped,
                  )
                }
              }
            }
          }
        }
      true
    } catch (_: SecurityException) {
      _meter.update { it.copy(errorMessageFa = "مجوز دسترسی به میکروفون داده نشده است.") }
      false
    } catch (_: Throwable) {
      _meter.update { it.copy(errorMessageFa = "خطا در شروع ضبط صوتی.") }
      false
    }
  }

  suspend fun stopRecordingAndFinalize(preferredFormat: AudioExportFormat): NarrationGenerationResult? {
    val segId = currentSegmentId ?: return null
    val outDir = currentOutputDir ?: return null
    val pcmFile = currentPcmFile ?: return null
    val peak = _meter.value.peakAmplitude
    val clipped = _meter.value.clippingDetected
    val elapsed = _meter.value.elapsedMs

    _meter.update { it.copy(isRecording = false) }
    try {
      recordJob?.join()
    } catch (_: Throwable) {}
    try {
      audioRecord?.stop()
      audioRecord?.release()
    } catch (_: Throwable) {}
    audioRecord = null

    return withContext(Dispatchers.IO) {
      if (!pcmFile.exists() || pcmFile.length() == 0L) {
        return@withContext NarrationGenerationResult.Unavailable(
          reasonFa = "صدایی ضبط نشد.",
          reasonEn = "No audio frames were captured.",
        )
      }
      val wavFile = File(outDir, "${segId}_rec.wav")
      AudioEncoderExporter.writeWavFile(pcmFile, wavFile)
      pcmFile.delete()

      val finalFile =
        if (preferredFormat == AudioExportFormat.M4A) {
          val m4aFile = File(outDir, "${segId}_rec.m4a")
          if (AudioEncoderExporter.encodeWavToM4a(wavFile, m4aFile)) m4aFile else wavFile
        } else {
          wavFile
        }
      val format =
        if (finalFile.extension == "m4a") AudioExportFormat.M4A else AudioExportFormat.WAV
      val duration = AudioEncoderExporter.extractDurationMs(finalFile).takeIf { it > 0L } ?: elapsed
      NarrationGenerationResult.Success(
        file = finalFile,
        format = format,
        durationMs = duration,
        peakAmplitude = peak,
        clippingDetected = clipped,
      )
    }
  }

  fun cancelIfRunning() {
    _meter.update { it.copy(isRecording = false) }
    recordJob?.cancel()
    try {
      audioRecord?.stop()
      audioRecord?.release()
    } catch (_: Throwable) {}
    audioRecord = null
    currentPcmFile?.delete()
  }
}
