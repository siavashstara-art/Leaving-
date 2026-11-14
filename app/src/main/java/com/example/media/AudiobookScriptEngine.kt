package com.example.media

import com.example.model.Chapter
import com.example.model.ReaderBlock
import java.security.MessageDigest
import java.util.Locale

data class NarrationSegmentDraft(
  val segmentId: String,
  val chapterId: String,
  val sectionId: String,
  val index: Int,
  val titleFa: String,
  val titleEn: String,
  val manuscriptTextFa: String,
  val manuscriptTextEn: String,
  val preparedScriptFa: String,
  val sourceHash: String,
)

object AudiobookScriptEngine {

  fun sha256(input: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  fun normalizePersianNarrationPunctuation(raw: String): String {
    return raw
      .replace('ي', 'ی')
      .replace('ك', 'ک')
      .replace(Regex("[ \\t]+"), " ")
      .replace(" ,", "، ")
      .replace(" ;", "؛ ")
      .replace("؟", "؟ ")
      .replace(Regex("\\n{2,}"), "\n")
      .trim()
  }

  fun chapterManifestFileName(chapterNumber: Int, extension: String): String =
    "CHAPTER_%03d.%s".format(Locale.US, chapterNumber, extension.lowercase(Locale.US))

  fun computeChapterHash(chapter: Chapter): String {
    val raw = buildString {
      append(chapter.id)
      append('|')
      append(chapter.titleFa)
      append('|')
      append(chapter.openingEpigraphFa)
      chapter.sections.forEach { sec ->
        append('|')
        append(sec.id)
        append('|')
        append(sec.titleFa)
        sec.blocks.forEach { block ->
          append('|')
          append(extractBlockTextFa(block))
        }
      }
    }
    return sha256(normalizePersianNarrationPunctuation(raw))
  }

  fun segmentChapter(chapter: Chapter): List<NarrationSegmentDraft> {
    val result = mutableListOf<NarrationSegmentDraft>()
    var idx = 0

    // Opening segment (Title + Epigraph)
    val introFa =
      normalizePersianNarrationPunctuation(
        "فصل ${chapter.number}: ${chapter.titleFa}.\n${chapter.openingEpigraphFa}"
      )
    val introEn = "Chapter ${chapter.number}: ${chapter.titleEn}. ${chapter.openingEpigraphEn}"
    result.add(
      NarrationSegmentDraft(
        segmentId = "${chapter.id}_seg_${idx}",
        chapterId = chapter.id,
        sectionId = "${chapter.id}_intro",
        index = idx,
        titleFa = "سرآغاز فصل ${chapter.number}",
        titleEn = "Chapter ${chapter.number} Opening",
        manuscriptTextFa = introFa,
        manuscriptTextEn = introEn,
        preparedScriptFa = introFa,
        sourceHash = sha256(introFa),
      )
    )
    idx++

    // Section-by-section segments keeping natural sentence & couplet boundaries
    chapter.sections.forEach { section ->
      section.blocks.forEachIndexed { blockIdx, block ->
        val textFa = extractBlockTextFa(block)
        val textEn = extractBlockTextEn(block)
        if (textFa.isNotBlank()) {
          val chunks = splitLongPersianPassage(textFa, maxChars = 320)
          chunks.forEachIndexed { chunkIdx, chunk ->
            val normalized = normalizePersianNarrationPunctuation(chunk)
            val suffix = if (chunks.size > 1) " (${chunkIdx + 1}/${chunks.size})" else ""
            result.add(
              NarrationSegmentDraft(
                segmentId = "${chapter.id}_seg_${idx}",
                chapterId = chapter.id,
                sectionId = section.id,
                index = idx,
                titleFa = "${section.titleFa}$suffix",
                titleEn = "${section.titleEn}$suffix",
                manuscriptTextFa = chunk,
                manuscriptTextEn = if (chunkIdx == 0) textEn else "",
                preparedScriptFa = normalized,
                sourceHash = sha256(normalized),
              )
            )
            idx++
          }
        }
      }
    }

    // Closing summary segment
    val closingFa = normalizePersianNarrationPunctuation(chapter.closingSummaryFa)
    result.add(
      NarrationSegmentDraft(
        segmentId = "${chapter.id}_seg_${idx}",
        chapterId = chapter.id,
        sectionId = "${chapter.id}_closing",
        index = idx,
        titleFa = "جمع‌بندی فصل ${chapter.number}",
        titleEn = "Chapter ${chapter.number} Closing",
        manuscriptTextFa = closingFa,
        manuscriptTextEn = chapter.closingSummaryEn,
        preparedScriptFa = closingFa,
        sourceHash = sha256(closingFa),
      )
    )

    return result
  }

  private fun extractBlockTextFa(block: ReaderBlock): String =
    when (block) {
      is ReaderBlock.Prose -> block.textFa
      is ReaderBlock.Poetry ->
        block.coupletsFa.joinToString("\n") { (h1, h2) -> "$h1، $h2" }
      is ReaderBlock.QuranicVerse ->
        "${block.surahRefFa}.\n${block.arabicText}.\nترجمه: ${block.translationFa}.\nتوضیح: ${block.explanationFa}.\nتأمل نویسنده: ${block.reflectionFa}"
      is ReaderBlock.Illustration -> "" // Visual plates are non-spoken unless author adds script manually
    }

  private fun extractBlockTextEn(block: ReaderBlock): String =
    when (block) {
      is ReaderBlock.Prose -> block.textEn
      is ReaderBlock.Poetry -> block.translationEn
      is ReaderBlock.QuranicVerse ->
        "${block.surahRefEn}. ${block.translationEn} ${block.explanationEn} ${block.reflectionEn}"
      is ReaderBlock.Illustration -> ""
    }

  private fun splitLongPersianPassage(text: String, maxChars: Int): List<String> {
    if (text.length <= maxChars) return listOf(text)
    val sentences = text.split(Regex("(?<=[.؟!\n])\\s+")).filter { it.isNotBlank() }
    val chunks = mutableListOf<String>()
    val current = StringBuilder()
    sentences.forEach { sentence ->
      if (current.isNotEmpty() && current.length + sentence.length + 1 > maxChars) {
        chunks.add(current.toString().trim())
        current.clear()
      }
      if (current.isNotEmpty()) current.append(' ')
      current.append(sentence)
    }
    if (current.isNotEmpty()) chunks.add(current.toString().trim())
    return if (chunks.isEmpty()) listOf(text) else chunks
  }
}
