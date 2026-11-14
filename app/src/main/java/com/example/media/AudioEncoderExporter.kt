package com.example.media

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.provider.DocumentsContract
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class ImportedAudioInfo(
  val filePath: String,
  val extension: String,
  val durationMs: Long,
)

data class ExportedAudiobookResult(
  val exportedFiles: List<String>,
  val skippedMp3Notice: Boolean,
  val destinationLabel: String,
)

object AudioEncoderExporter {
  const val SAMPLE_RATE_HZ = 16000
  const val CHANNELS = 1
  const val BITS_PER_SAMPLE = 16

  fun writeWavFile(
    rawPcmFile: File,
    outputWavFile: File,
    sampleRate: Int = SAMPLE_RATE_HZ,
    channels: Int = CHANNELS,
  ) {
    val totalAudioLen = rawPcmFile.length()
    val totalDataLen = totalAudioLen + 36
    val byteRate = sampleRate * channels * BITS_PER_SAMPLE / 8L

    outputWavFile.parentFile?.mkdirs()
    FileOutputStream(outputWavFile).use { out ->
      out.write(buildWavHeader(totalAudioLen, totalDataLen, sampleRate, channels, byteRate))
      FileInputStream(rawPcmFile).use { input ->
        val buffer = ByteArray(8192)
        while (true) {
          val read = input.read(buffer)
          if (read <= 0) break
          out.write(buffer, 0, read)
        }
      }
    }
  }

  fun buildWavHeader(
    totalAudioLen: Long,
    totalDataLen: Long,
    sampleRate: Int,
    channels: Int,
    byteRate: Long,
  ): ByteArray {
    val header = ByteArray(44)
    val blockAlign = (channels * BITS_PER_SAMPLE / 8).toShort()
    header[0] = 'R'.code.toByte()
    header[1] = 'I'.code.toByte()
    header[2] = 'F'.code.toByte()
    header[3] = 'F'.code.toByte()
    ByteBuffer.wrap(header, 4, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(totalDataLen.toInt())
    header[8] = 'W'.code.toByte()
    header[9] = 'A'.code.toByte()
    header[10] = 'V'.code.toByte()
    header[11] = 'E'.code.toByte()
    header[12] = 'f'.code.toByte()
    header[13] = 'm'.code.toByte()
    header[14] = 't'.code.toByte()
    header[15] = ' '.code.toByte()
    ByteBuffer.wrap(header, 16, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(16)
    ByteBuffer.wrap(header, 20, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(1)
    ByteBuffer.wrap(header, 22, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(channels.toShort())
    ByteBuffer.wrap(header, 24, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(sampleRate)
    ByteBuffer.wrap(header, 28, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(byteRate.toInt())
    ByteBuffer.wrap(header, 32, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(blockAlign)
    ByteBuffer.wrap(header, 34, 2).order(ByteOrder.LITTLE_ENDIAN).putShort(BITS_PER_SAMPLE.toShort())
    header[36] = 'd'.code.toByte()
    header[37] = 'a'.code.toByte()
    header[38] = 't'.code.toByte()
    header[39] = 'a'.code.toByte()
    ByteBuffer.wrap(header, 40, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(totalAudioLen.toInt())
    return header
  }

  fun encodeWavToM4a(inputWav: File, outputM4a: File): Boolean {
    if (!inputWav.exists() || inputWav.length() <= 44L) return false
    var codec: MediaCodec? = null
    var muxer: MediaMuxer? = null
    return try {
      val sampleRate = readWavSampleRate(inputWav) ?: SAMPLE_RATE_HZ
      val channels = readWavChannels(inputWav) ?: CHANNELS
      val format =
        MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channels).apply {
          setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
          setInteger(MediaFormat.KEY_BIT_RATE, 96000)
          setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384)
        }
      outputM4a.parentFile?.mkdirs()
      codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
      codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
      codec.start()
      muxer = MediaMuxer(outputM4a.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

      val bufferInfo = MediaCodec.BufferInfo()
      var trackIndex = -1
      var muxerStarted = false
      var inputDone = false
      var outputDone = false
      var totalBytesFed = 0L
      val bytesPerSec = (sampleRate * channels * 2).toDouble().coerceAtLeast(1.0)

      FileInputStream(inputWav).use { fis ->
        fis.skip(44L)
        val chunk = ByteArray(4096)
        while (!outputDone) {
          if (!inputDone) {
            val inIdx = codec.dequeueInputBuffer(10_000L)
            if (inIdx >= 0) {
              val inBuf = codec.getInputBuffer(inIdx)
              inBuf?.clear()
              val read = fis.read(chunk)
              if (read <= 0) {
                codec.queueInputBuffer(inIdx, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                inputDone = true
              } else {
                inBuf?.put(chunk, 0, read)
                val ptsUs = ((totalBytesFed / bytesPerSec) * 1_000_000.0).toLong()
                codec.queueInputBuffer(inIdx, 0, read, ptsUs, 0)
                totalBytesFed += read
              }
            }
          }

          val outIdx = codec.dequeueOutputBuffer(bufferInfo, 10_000L)
          when {
            outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
              if (!muxerStarted) {
                trackIndex = muxer.addTrack(codec.outputFormat)
                muxer.start()
                muxerStarted = true
              }
            }
            outIdx >= 0 -> {
              val encoded = codec.getOutputBuffer(outIdx)
              if (encoded != null && bufferInfo.size > 0 && muxerStarted) {
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0) {
                  encoded.position(bufferInfo.offset)
                  encoded.limit(bufferInfo.offset + bufferInfo.size)
                  muxer.writeSampleData(trackIndex, encoded, bufferInfo)
                }
              }
              codec.releaseOutputBuffer(outIdx, false)
              if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                outputDone = true
              }
            }
          }
        }
      }
      if (muxerStarted) muxer.stop()
      outputM4a.exists() && outputM4a.length() > 0L
    } catch (_: Throwable) {
      false
    } finally {
      try {
        codec?.stop()
        codec?.release()
      } catch (_: Throwable) {}
      try {
        muxer?.release()
      } catch (_: Throwable) {}
    }
  }

  fun combineWavFiles(inputs: List<File>, outputWav: File): Boolean {
    val valid = inputs.filter { it.exists() && it.length() > 44L }
    if (valid.isEmpty()) return false
    val sampleRate = readWavSampleRate(valid.first()) ?: SAMPLE_RATE_HZ
    val channels = readWavChannels(valid.first()) ?: CHANNELS
    val compatible =
      valid.filter {
        (readWavSampleRate(it) ?: sampleRate) == sampleRate &&
          (readWavChannels(it) ?: channels) == channels
      }
    if (compatible.isEmpty()) return false

    val totalAudioLen = compatible.sumOf { (it.length() - 44L).coerceAtLeast(0L) }
    val totalDataLen = totalAudioLen + 36L
    val byteRate = sampleRate * channels * BITS_PER_SAMPLE / 8L

    outputWav.parentFile?.mkdirs()
    FileOutputStream(outputWav).use { out ->
      out.write(buildWavHeader(totalAudioLen, totalDataLen, sampleRate, channels, byteRate))
      val buffer = ByteArray(8192)
      compatible.forEach { file ->
        FileInputStream(file).use { input ->
          input.skip(44L)
          while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            out.write(buffer, 0, read)
          }
        }
      }
    }
    return outputWav.exists() && outputWav.length() > 44L
  }

  fun combineM4aFiles(inputs: List<File>, outputM4a: File): Boolean {
    val valid = inputs.filter { it.exists() && it.length() > 0L }
    if (valid.isEmpty()) return false
    var muxer: MediaMuxer? = null
    return try {
      outputM4a.parentFile?.mkdirs()
      muxer = MediaMuxer(outputM4a.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
      var trackIndex = -1
      var muxerStarted = false
      var timeOffsetUs = 0L
      val buffer = ByteBuffer.allocateDirect(256 * 1024)
      val info = MediaCodec.BufferInfo()

      valid.forEach { file ->
        val extractor = MediaExtractor()
        try {
          extractor.setDataSource(file.absolutePath)
          var audioTrack = -1
          for (i in 0 until extractor.trackCount) {
            val fmt = extractor.getTrackFormat(i)
            val mime = fmt.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("audio/")) {
              audioTrack = i
              if (!muxerStarted) {
                trackIndex = muxer.addTrack(fmt)
                muxer.start()
                muxerStarted = true
              }
              break
            }
          }
          if (audioTrack >= 0 && muxerStarted) {
            extractor.selectTrack(audioTrack)
            var lastPts = 0L
            while (true) {
              buffer.clear()
              val size = extractor.readSampleData(buffer, 0)
              if (size < 0) break
              val pts = extractor.sampleTime
              if (pts >= 0L) lastPts = pts
              info.offset = 0
              info.size = size
              info.presentationTimeUs = timeOffsetUs + pts.coerceAtLeast(0L)
              info.flags =
                if ((extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                  MediaCodec.BUFFER_FLAG_KEY_FRAME
                } else 0
              muxer.writeSampleData(trackIndex, buffer, info)
              extractor.advance()
            }
            timeOffsetUs += lastPts + 25_000L
          }
        } finally {
          extractor.release()
        }
      }
      if (muxerStarted) muxer.stop()
      outputM4a.exists() && outputM4a.length() > 0L
    } catch (_: Throwable) {
      false
    } finally {
      try {
        muxer?.release()
      } catch (_: Throwable) {}
    }
  }

  fun importUriToAppAudioDir(
    context: Context,
    sourceUri: Uri,
    targetDir: File,
    targetBaseName: String,
  ): ImportedAudioInfo? {
    return try {
      val mime = context.contentResolver.getType(sourceUri) ?: ""
      val ext =
        when {
          mime.contains("mpeg") || sourceUri.toString().lowercase().endsWith(".mp3") -> "mp3"
          mime.contains("wav") || sourceUri.toString().lowercase().endsWith(".wav") -> "wav"
          else -> "m4a"
        }
      targetDir.mkdirs()
      val outFile = File(targetDir, "$targetBaseName.$ext")
      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(outFile).use { output -> input.copyTo(output) }
      } ?: return null

      val durationMs = extractDurationMs(outFile)
      ImportedAudioInfo(filePath = outFile.absolutePath, extension = ext, durationMs = durationMs)
    } catch (_: Throwable) {
      null
    }
  }

  fun extractDurationMs(file: File): Long {
    if (!file.exists()) return 0L
    val retriever = MediaMetadataRetriever()
    return try {
      retriever.setDataSource(file.absolutePath)
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
    } catch (_: Throwable) {
      0L
    } finally {
      try {
        retriever.release()
      } catch (_: Throwable) {}
    }
  }

  fun exportFilesToSafTree(
    context: Context,
    treeUri: Uri,
    folderName: String,
    namedFiles: List<Pair<String, File>>,
  ): List<String> {
    val resolver = context.contentResolver
    val parentDocId = DocumentsContract.getTreeDocumentId(treeUri)
    val rootDocUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocId)
    val subFolderUri =
      DocumentsContract.createDocument(
        resolver,
        rootDocUri,
        DocumentsContract.Document.MIME_TYPE_DIR,
        folderName,
      ) ?: rootDocUri

    val exportedNames = mutableListOf<String>()
    namedFiles.forEach { (displayName, sourceFile) ->
      if (sourceFile.exists() && sourceFile.length() > 0L) {
        val mime =
          when (sourceFile.extension.lowercase()) {
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            else -> "audio/mp4"
          }
        val targetDocUri =
          DocumentsContract.createDocument(resolver, subFolderUri, mime, displayName)
        if (targetDocUri != null) {
          resolver.openOutputStream(targetDocUri)?.use { out ->
            FileInputStream(sourceFile).use { input -> input.copyTo(out) }
          }
          exportedNames.add(displayName)
        }
      }
    }
    return exportedNames
  }

  private fun readWavSampleRate(wavFile: File): Int? {
    if (wavFile.length() < 28L) return null
    FileInputStream(wavFile).use { fis ->
      val header = ByteArray(28)
      if (fis.read(header) < 28) return null
      return ByteBuffer.wrap(header, 24, 4).order(ByteOrder.LITTLE_ENDIAN).int
    }
  }

  private fun readWavChannels(wavFile: File): Int? {
    if (wavFile.length() < 24L) return null
    FileInputStream(wavFile).use { fis ->
      val header = ByteArray(24)
      if (fis.read(header) < 24) return null
      return ByteBuffer.wrap(header, 22, 2).order(ByteOrder.LITTLE_ENDIAN).short.toInt()
    }
  }
}
