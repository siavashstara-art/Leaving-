package com.example.model

import androidx.annotation.DrawableRes

enum class AppLanguage {
  PERSIAN,
  ENGLISH,
}

enum class ThemeMode {
  SYSTEM,
  LIGHT,
  DARK,
}

enum class ContentCategory(
  val titleFa: String,
  val titleEn: String,
  val subtitleFa: String,
  val subtitleEn: String,
) {
  LIVING_ART(
    titleFa = "هنر زیستن و حضور",
    titleEn = "Art of Living",
    subtitleFa = "مبانی حضور و آگاهی روزمره",
    subtitleEn = "Foundations of mindful presence",
  ),
  POETRY(
    titleFa = "شعر و ادب",
    titleEn = "Poetry & Literature",
    subtitleFa = "غزل‌ها و سروده‌های برگزیده",
    subtitleEn = "Selected Persian poetry & literary verse",
  ),
  QURANIC_REFLECTION(
    titleFa = "تأملات قرآنی",
    titleEn = "Quranic Reflections",
    subtitleFa = "آیه، ترجمه، شرح و تدبر شخصی",
    subtitleEn = "Verse, translation, explanation & reflection",
  ),
  HEALTH_WELLBEING(
    titleFa = "سلامت و تندرستی",
    titleEn = "Health & Wellbeing",
    subtitleFa = "تعادل جسم، خواب و تنفس آگاهانه",
    subtitleEn = "Bodily balance, rest & mindful breath",
  ),
}

data class BookPart(
  val id: String,
  val number: Int,
  val titleFa: String,
  val titleEn: String,
  val descriptionFa: String,
  val descriptionEn: String,
)

sealed interface ReaderBlock {
  data class Prose(
    val textFa: String,
    val textEn: String,
  ) : ReaderBlock

  data class Poetry(
    val coupletsFa: List<Pair<String, String>>,
    val translationEn: String,
    val poetFa: String,
    val poetEn: String,
  ) : ReaderBlock

  data class QuranicVerse(
    val arabicText: String,
    val surahRefFa: String,
    val surahRefEn: String,
    val translationFa: String,
    val translationEn: String,
    val explanationFa: String,
    val explanationEn: String,
    val reflectionFa: String,
    val reflectionEn: String,
  ) : ReaderBlock

  data class Illustration(
    @DrawableRes val drawableRes: Int,
    val conceptFa: String,
    val conceptEn: String,
    val captionFa: String,
    val captionEn: String,
    val altTextFa: String,
    val altTextEn: String,
  ) : ReaderBlock
}

data class BookSection(
  val id: String,
  val number: Int,
  val titleFa: String,
  val titleEn: String,
  val blocks: List<ReaderBlock>,
)

data class Chapter(
  val id: String,
  val partId: String,
  val number: Int,
  val category: ContentCategory,
  val titleFa: String,
  val titleEn: String,
  val openingEpigraphFa: String,
  val openingEpigraphEn: String,
  val excerptFa: String,
  val excerptEn: String,
  val closingSummaryFa: String,
  val closingSummaryEn: String,
  val readingMinutes: Int,
  @DrawableRes val editorialPlateRes: Int? = null,
  val editorialPlateAltFa: String? = null,
  val editorialPlateAltEn: String? = null,
  val sections: List<BookSection>,
) {
  val blocks: List<ReaderBlock>
    get() = sections.flatMap { it.blocks }
}

data class AccessibilitySettings(
  val themeMode: ThemeMode = ThemeMode.LIGHT,
  val fontScale: Float = 1.0f,
  val largeTextEnabled: Boolean = false,
  val highContrastEnabled: Boolean = false,
  val reducedMotionEnabled: Boolean = false,
  val focusModeDefault: Boolean = false,
  val language: AppLanguage = AppLanguage.PERSIAN,
)
