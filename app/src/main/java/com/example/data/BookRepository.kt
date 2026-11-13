package com.example.data

import com.example.R
import com.example.data.local.BookUserDao
import com.example.data.local.BookmarkEntity
import com.example.data.local.NoteEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.ReadingProgressEntity
import com.example.model.AccessibilitySettings
import com.example.model.AppLanguage
import com.example.model.BookPart
import com.example.model.Chapter
import com.example.model.ReaderBlock
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.Flow

data class GalleryArtwork(
  val id: String,
  val drawableRes: Int,
  val titleFa: String,
  val titleEn: String,
  val captionFa: String,
  val captionEn: String,
  val chapterId: String,
)

class BookRepository(
  private val dao: BookUserDao,
  private val prefs: PreferencesRepository,
) {
  val parts: List<BookPart> = BookCatalog.parts
  val chapters: List<Chapter> = BookCatalog.chapters

  val galleryItems: List<GalleryArtwork> =
    listOf(
      GalleryArtwork(
        id = "art_hero",
        drawableRes = R.drawable.img_hero_honar_zistan,
        titleFa = "حیاطِ روشنِ حضور",
        titleEn = "Sunlit Courtyard of Presence",
        captionFa = "نگاره‌ی سرآغاز کتاب؛ حیاط آرام و سروستان در نور بامداد.",
        captionEn = "Book frontispiece: tranquil courtyard and cypress grove in morning light.",
        chapterId = "ch_01",
      ),
      GalleryArtwork(
        id = "art_poetry",
        drawableRes = R.drawable.img_poetry_garden,
        titleFa = "باغِ شبانه‌ی شعر",
        titleEn = "Night Garden of Poetry",
        captionFa = "نگاره‌ی دفتر شعر؛ ستارگان طلایی و شبِ آرامش.",
        captionEn = "Poetry plate: restrained gold stars above nocturnal garden.",
        chapterId = "ch_03",
      ),
      GalleryArtwork(
        id = "art_reflection",
        drawableRes = R.drawable.img_chapter_reflection,
        titleFa = "بامدادِ زیتون و انار",
        titleEn = "Morning of Olive & Pomegranate",
        captionFa = "نگاره‌ی تندرستی و سادگی؛ شاخه زیتون و فنجان صبحگاهی.",
        captionEn = "Wellbeing plate: olive branches and morning tea ritual.",
        chapterId = "ch_05",
      ),
      GalleryArtwork(
        id = "art_author",
        drawableRes = R.drawable.img_author_quill,
        titleFa = "قلم، دوات و یاس",
        titleEn = "Reed Pen, Inkwell & Jasmine",
        captionFa = "نگاره‌ی شناسنامه و قلمِ سیاوش علی‌میری.",
        captionEn = "Colophon emblem honoring author Siavash Ali-Miri.",
        chapterId = "ch_02",
      ),
    )

  val settings: Flow<AccessibilitySettings> = prefs.settingsFlow
  val bookmarks: Flow<List<BookmarkEntity>> = dao.observeBookmarks()
  val notes: Flow<List<NoteEntity>> = dao.observeNotes()
  val progress: Flow<List<ReadingProgressEntity>> = dao.observeProgress()

  suspend fun toggleBookmark(chapterId: String, currentlyBookmarked: Boolean) {
    if (currentlyBookmarked) {
      dao.removeBookmark(chapterId)
    } else {
      dao.addBookmark(BookmarkEntity(chapterId = chapterId))
    }
  }

  suspend fun saveNote(chapterId: String, content: String, existingId: Long = 0L) {
    val trimmed = content.trim()
    if (trimmed.isNotEmpty()) {
      dao.upsertNote(
        NoteEntity(
          id = existingId,
          chapterId = chapterId,
          content = trimmed,
          updatedAt = System.currentTimeMillis(),
        )
      )
    }
  }

  suspend fun deleteNote(noteId: Long) = dao.deleteNote(noteId)

  suspend fun markChapterRead(chapterId: String, isCompleted: Boolean) {
    dao.upsertProgress(
      ReadingProgressEntity(
        chapterId = chapterId,
        isCompleted = isCompleted,
        scrollFraction = if (isCompleted) 1.0f else 0.35f,
        lastReadAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun recordChapterOpened(chapterId: String, alreadyCompleted: Boolean) {
    dao.upsertProgress(
      ReadingProgressEntity(
        chapterId = chapterId,
        isCompleted = alreadyCompleted,
        scrollFraction = if (alreadyCompleted) 1.0f else 0.45f,
        lastReadAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun setThemeMode(mode: ThemeMode) = prefs.updateThemeMode(mode)

  suspend fun setFontScale(scale: Float) = prefs.updateFontScale(scale)

  suspend fun setLargeText(enabled: Boolean) = prefs.updateLargeText(enabled)

  suspend fun setHighContrast(enabled: Boolean) = prefs.updateHighContrast(enabled)

  suspend fun setReducedMotion(enabled: Boolean) = prefs.updateReducedMotion(enabled)

  suspend fun setFocusModeDefault(enabled: Boolean) = prefs.updateFocusModeDefault(enabled)

  suspend fun setLanguage(language: AppLanguage) = prefs.updateLanguage(language)

  fun buildTranscript(chapter: Chapter, language: AppLanguage): String {
    val sb = StringBuilder()
    sb.appendLine(if (language == AppLanguage.PERSIAN) chapter.titleFa else chapter.titleEn)
    sb.appendLine()
    chapter.sections.forEach { section ->
      sb.appendLine(if (language == AppLanguage.PERSIAN) section.titleFa else section.titleEn)
      section.blocks.forEach { block ->
        when (block) {
          is ReaderBlock.Prose -> {
            sb.appendLine(if (language == AppLanguage.PERSIAN) block.textFa else block.textEn)
          }
          is ReaderBlock.Poetry -> {
            if (language == AppLanguage.PERSIAN) {
              block.coupletsFa.forEach { (first, second) -> sb.appendLine("$first — $second") }
            } else {
              sb.appendLine(block.translationEn)
            }
          }
          is ReaderBlock.QuranicVerse -> {
            sb.appendLine(block.arabicText)
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.translationFa else block.translationEn
            )
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.explanationFa else block.explanationEn
            )
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.reflectionFa else block.reflectionEn
            )
          }
          is ReaderBlock.Illustration -> {
            sb.appendLine(
              "[${if (language == AppLanguage.PERSIAN) block.captionFa else block.captionEn}]"
            )
          }
        }
      }
      sb.appendLine()
    }
    return sb.toString().trim()
  }
}
