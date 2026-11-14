package com.example

import com.example.data.BookCatalog
import com.example.media.AudioEncoderExporter
import com.example.media.AudiobookScriptEngine
import com.example.model.ManuscriptApprovalStatus
import java.io.File
import kotlin.io.path.createTempDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AudiobookSystemTest {

  @Test
  fun scriptEngine_segmentsChapter_normalizesPersian_and_detectsHashDrift() {
    val ch1 = BookCatalog.chapters.first { it.id == "ch_01" }
    val segments = AudiobookScriptEngine.segmentChapter(ch1)
    assertTrue(segments.size >= 3)
    assertEquals("ch_01_seg_0", segments.first().segmentId)

    val originalHash = segments[1].sourceHash
    val edited =
      AudiobookScriptEngine.normalizePersianNarrationPunctuation(
        segments[1].preparedScriptFa + " مکث کوتاه."
      )
    val editedHash = AudiobookScriptEngine.sha256(edited)
    assertNotEquals(originalHash, editedHash)

    val normalizedChars =
      AudiobookScriptEngine.normalizePersianNarrationPunctuation("كتاب  خوب ياد")
    assertEquals("کتاب خوب یاد", normalizedChars)
    assertEquals("CHAPTER_001.m4a", AudiobookScriptEngine.chapterManifestFileName(1, "M4A"))
  }

  @Test
  fun catalogApprovalStatus_gatesDraftAndIncompleteChapters() {
    val ch1 = BookCatalog.chapters.first { it.id == "ch_01" }
    val ch7 = BookCatalog.chapters.first { it.id == "ch_07" }
    val ch8 = BookCatalog.chapters.first { it.id == "ch_08" }
    assertEquals(ManuscriptApprovalStatus.APPROVED, ch1.approvalStatus)
    assertEquals(ManuscriptApprovalStatus.DRAFT_PENDING_REVIEW, ch7.approvalStatus)
    assertEquals(ManuscriptApprovalStatus.INCOMPLETE, ch8.approvalStatus)
    assertFalse(ch7.approvalStatus == ManuscriptApprovalStatus.APPROVED)
  }

  @Test
  fun audioExporter_buildsValidWavHeaders_and_combinesChapters() {
    val tmpDir = createTempDirectory("audiobook_wav_test").toFile()
    try {
      val pcm1 = File(tmpDir, "seg1.pcm").apply { writeBytes(ByteArray(320) { 1 }) }
      val pcm2 = File(tmpDir, "seg2.pcm").apply { writeBytes(ByteArray(640) { 2 }) }
      val wav1 = File(tmpDir, "seg1.wav")
      val wav2 = File(tmpDir, "seg2.wav")
      AudioEncoderExporter.writeWavFile(pcm1, wav1)
      AudioEncoderExporter.writeWavFile(pcm2, wav2)

      assertEquals(44L + 320L, wav1.length())
      assertEquals("RIFF", wav1.readBytes().copyOfRange(0, 4).toString(Charsets.US_ASCII))
      assertEquals("WAVE", wav1.readBytes().copyOfRange(8, 12).toString(Charsets.US_ASCII))

      val combined = File(tmpDir, "CHAPTER_001.wav")
      assertTrue(AudioEncoderExporter.combineWavFiles(listOf(wav1, wav2), combined))
      assertEquals(44L + 320L + 640L, combined.length())
    } finally {
      tmpDir.deleteRecursively()
    }
  }
}
