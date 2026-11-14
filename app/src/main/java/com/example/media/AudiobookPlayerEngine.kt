package com.example.media

import android.media.MediaPlayer
import android.media.PlaybackParams
import com.example.model.NarrationSegmentUi
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudiobookPlayerState(
  val activeChapterId: String? = null,
  val activeSegmentId: String? = null,
  val activeSegmentIndex: Int = 0,
  val isPlaying: Boolean = false,
  val positionMs: Long = 0L,
  val durationMs: Long = 0L,
  val playbackSpeed: Float = 1.0f,
  val volume: Float = 1.0f,
  val statusMessageFa: String = "قطعه دارای فایل صوتی یا گوینده آماده را انتخاب کنید",
  val statusMessageEn: String = "Select a narrated or recorded segment to play",
)

class AudiobookPlayerEngine(
  private val scope: CoroutineScope,
  private val onProgressTick: (chapterId: String, segmentId: String, segIndex: Int, positionMs: Long) -> Unit,
) {
  private val _state = MutableStateFlow(AudiobookPlayerState())
  val state: StateFlow<AudiobookPlayerState> = _state.asStateFlow()

  private var mediaPlayer: MediaPlayer? = null
  private var tickerJob: Job? = null
  private var activePlaylist: List<NarrationSegmentUi> = emptyList()

  fun playSegment(
    chapterId: String,
    segments: List<NarrationSegmentUi>,
    segmentIndex: Int,
    startPositionMs: Long = 0L,
  ) {
    activePlaylist = segments
    val seg = segments.getOrNull(segmentIndex) ?: return
    val path = seg.audioFilePath
    val file = path?.let { File(it) }

    if (file == null || !file.exists() || file.length() == 0L) {
      stop()
      _state.update {
        it.copy(
          activeChapterId = chapterId,
          activeSegmentId = seg.segmentId,
          activeSegmentIndex = segmentIndex,
          isPlaying = false,
          positionMs = 0L,
          durationMs = 0L,
          statusMessageFa = "این قطعه هنوز فایل صوتی ضبط‌شده یا تولیدشده ندارد.",
          statusMessageEn = "No recorded or synthesized audio file exists for this segment yet.",
        )
      }
      return
    }

    releasePlayerOnly()
    try {
      val mp = MediaPlayer()
      mp.setDataSource(file.absolutePath)
      mp.setVolume(_state.value.volume, _state.value.volume)
      mp.prepare()
      applySpeed(mp, _state.value.playbackSpeed)
      if (startPositionMs > 0L && startPositionMs < mp.duration) {
        mp.seekTo(startPositionMs.toInt())
      }
      mp.setOnCompletionListener {
        val nextIndex = segmentIndex + 1
        val nextSeg = activePlaylist.getOrNull(nextIndex)
        if (nextSeg?.audioFilePath != null && File(nextSeg.audioFilePath).exists()) {
          playSegment(chapterId, activePlaylist, nextIndex, 0L)
        } else {
          tickerJob?.cancel()
          _state.update {
            it.copy(
              isPlaying = false,
              positionMs = it.durationMs,
              statusMessageFa = "پخش این بخش به پایان رسید.",
              statusMessageEn = "Playback completed.",
            )
          }
        }
      }
      mp.start()
      mediaPlayer = mp
      _state.update {
        it.copy(
          activeChapterId = chapterId,
          activeSegmentId = seg.segmentId,
          activeSegmentIndex = segmentIndex,
          isPlaying = true,
          positionMs = startPositionMs,
          durationMs = mp.duration.toLong().coerceAtLeast(1L),
          statusMessageFa = "در حال پخش: ${seg.titleFa}",
          statusMessageEn = "Playing: ${seg.titleEn}",
        )
      }
      startTicker(chapterId, seg.segmentId, segmentIndex)
    } catch (_: Throwable) {
      _state.update {
        it.copy(
          isPlaying = false,
          statusMessageFa = "خطا در باز کردن فایل صوتی قطعه.",
          statusMessageEn = "Failed to open audio file for this segment.",
        )
      }
    }
  }

  fun togglePlayPause(
    chapterId: String,
    segments: List<NarrationSegmentUi>,
    segmentIndex: Int,
  ) {
    val mp = mediaPlayer
    if (mp != null && _state.value.activeSegmentId == segments.getOrNull(segmentIndex)?.segmentId) {
      if (mp.isPlaying) {
        mp.pause()
        tickerJob?.cancel()
        val pos = mp.currentPosition.toLong()
        _state.update {
          it.copy(
            isPlaying = false,
            positionMs = pos,
            statusMessageFa = "پخش متوقف شد",
            statusMessageEn = "Paused",
          )
        }
        segments.getOrNull(segmentIndex)?.let { seg ->
          onProgressTick(chapterId, seg.segmentId, segmentIndex, pos)
        }
      } else {
        mp.start()
        _state.update {
          it.copy(
            isPlaying = true,
            statusMessageFa = "در حال پخش...",
            statusMessageEn = "Playing...",
          )
        }
        segments.getOrNull(segmentIndex)?.let { seg ->
          startTicker(chapterId, seg.segmentId, segmentIndex)
        }
      }
    } else {
      playSegment(chapterId, segments, segmentIndex, 0L)
    }
  }

  fun seekTo(positionMs: Long) {
    val mp = mediaPlayer ?: return
    val clamped = positionMs.coerceIn(0L, mp.duration.toLong().coerceAtLeast(0L))
    mp.seekTo(clamped.toInt())
    _state.update { it.copy(positionMs = clamped) }
  }

  fun seekBy(deltaMs: Long) {
    val mp = mediaPlayer ?: return
    seekTo(mp.currentPosition.toLong() + deltaMs)
  }

  fun setSpeed(speed: Float) {
    val clamped = speed.coerceIn(0.75f, 2.0f)
    _state.update { it.copy(playbackSpeed = clamped) }
    mediaPlayer?.let { applySpeed(it, clamped) }
  }

  fun setVolume(volume: Float) {
    val clamped = volume.coerceIn(0f, 1f)
    _state.update { it.copy(volume = clamped) }
    mediaPlayer?.setVolume(clamped, clamped)
  }

  fun stop() {
    tickerJob?.cancel()
    releasePlayerOnly()
    _state.update { it.copy(isPlaying = false) }
  }

  fun release() {
    stop()
  }

  private fun applySpeed(mp: MediaPlayer, speed: Float) {
    try {
      val params = mp.playbackParams ?: PlaybackParams()
      params.speed = speed
      mp.playbackParams = params
    } catch (_: Throwable) {}
  }

  private fun startTicker(chapterId: String, segmentId: String, segmentIndex: Int) {
    tickerJob?.cancel()
    tickerJob =
      scope.launch(Dispatchers.Main) {
        while (isActive) {
          val mp = mediaPlayer
          if (mp != null && mp.isPlaying) {
            val pos = mp.currentPosition.toLong()
            _state.update { it.copy(positionMs = pos) }
            onProgressTick(chapterId, segmentId, segmentIndex, pos)
          }
          delay(400L)
        }
      }
  }

  private fun releasePlayerOnly() {
    try {
      mediaPlayer?.stop()
    } catch (_: Throwable) {}
    try {
      mediaPlayer?.release()
    } catch (_: Throwable) {}
    mediaPlayer = null
  }
}
