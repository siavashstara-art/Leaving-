package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.ContentCategory
import com.example.model.ThemeMode

private val LightEditorialScheme =
  lightColorScheme(
    primary = EmeraldDeep,
    onPrimary = Color.White,
    primaryContainer = BookPaper,
    onPrimaryContainer = ForestDeep,
    secondary = AntiqueGold,
    onSecondary = Color.White,
    secondaryContainer = SoftGoldTint,
    onSecondaryContainer = ForestDeep,
    tertiary = LiteraryRose,
    onTertiary = Color.White,
    tertiaryContainer = LiteraryRoseTint,
    onTertiaryContainer = LiteraryRose,
    background = WarmIvory,
    onBackground = CharcoalInk,
    surface = ElevatedPaper,
    onSurface = CharcoalInk,
    surfaceVariant = BookPaper,
    onSurfaceVariant = CharcoalMuted,
    outline = AntiqueGold.copy(alpha = 0.40f),
    outlineVariant = CharcoalInk.copy(alpha = 0.14f),
  )

private val DarkEditorialScheme =
  darkColorScheme(
    primary = Color(0xFF68C29F),
    onPrimary = ForestMidnight,
    primaryContainer = ForestElevatedDark,
    onPrimaryContainer = BookPaper,
    secondary = AntiqueGoldDark,
    onSecondary = ForestMidnight,
    secondaryContainer = Color(0xFF282315),
    onSecondaryContainer = AntiqueGoldDark,
    tertiary = LiteraryRoseDark,
    onTertiary = ForestMidnight,
    tertiaryContainer = Color(0xFF2D1E23),
    onTertiaryContainer = LiteraryRoseDark,
    background = ForestMidnight,
    onBackground = Color(0xFFEEE8DC),
    surface = ForestSurfaceDark,
    onSurface = Color(0xFFEEE8DC),
    surfaceVariant = ForestElevatedDark,
    onSurfaceVariant = Color(0xFFB4C2BA),
    outline = AntiqueGoldDark.copy(alpha = 0.35f),
    outlineVariant = Color(0xFFEEE8DC).copy(alpha = 0.14f),
  )

private val HighContrastLightScheme =
  lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7EFEA),
    onPrimaryContainer = HighContrastLightOnBg,
    secondary = Color(0xFF5D4100),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3CC),
    onSecondaryContainer = HighContrastLightOnBg,
    tertiary = Color(0xFF63182B),
    onTertiary = Color.White,
    background = HighContrastLightBg,
    onBackground = HighContrastLightOnBg,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightOnBg,
    surfaceVariant = Color(0xFFEAE8DF),
    onSurfaceVariant = HighContrastLightOnBg,
    outline = HighContrastLightPrimary,
    outlineVariant = HighContrastLightOnBg,
  )

private val HighContrastDarkScheme =
  darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkBg,
    primaryContainer = Color(0xFF0E3024),
    onPrimaryContainer = HighContrastDarkOnBg,
    secondary = Color(0xFFFFDC7A),
    onSecondary = HighContrastDarkBg,
    secondaryContainer = Color(0xFF332806),
    onSecondaryContainer = Color(0xFFFFEEBD),
    tertiary = Color(0xFFFFAFC4),
    onTertiary = HighContrastDarkBg,
    background = HighContrastDarkBg,
    onBackground = HighContrastDarkOnBg,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkOnBg,
    surfaceVariant = Color(0xFF152720),
    onSurfaceVariant = HighContrastDarkOnBg,
    outline = HighContrastDarkPrimary,
    outlineVariant = HighContrastDarkOnBg,
  )

data class HonarExtraColors(
  val isDark: Boolean,
  val highContrast: Boolean,
  val goldAccent: Color,
  val poetryAccent: Color,
  val poetryContainer: Color,
  val quranAccent: Color,
  val quranContainer: Color,
  val wellbeingAccent: Color,
  val wellbeingContainer: Color,
) {
  fun accentFor(category: ContentCategory): Color =
    when (category) {
      ContentCategory.LIVING_ART -> if (isDark) Color(0xFF68C29F) else EmeraldDeep
      ContentCategory.POETRY -> poetryAccent
      ContentCategory.QURANIC_REFLECTION -> quranAccent
      ContentCategory.HEALTH_WELLBEING -> wellbeingAccent
    }

  fun containerFor(category: ContentCategory): Color =
    when (category) {
      ContentCategory.LIVING_ART -> if (isDark) ForestElevatedDark else BookPaper
      ContentCategory.POETRY -> poetryContainer
      ContentCategory.QURANIC_REFLECTION -> quranContainer
      ContentCategory.HEALTH_WELLBEING -> wellbeingContainer
    }
}

val LocalHonarColors =
  staticCompositionLocalOf {
    HonarExtraColors(
      isDark = false,
      highContrast = false,
      goldAccent = AntiqueGold,
      poetryAccent = LiteraryRose,
      poetryContainer = LiteraryRoseTint,
      quranAccent = QuranAzure,
      quranContainer = QuranAzureTint,
      wellbeingAccent = WellbeingSage,
      wellbeingContainer = WellbeingSageTint,
    )
  }

// Crisp editorial corners rather than childish oversized rounded boxes
private val EditorialShapes =
  Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
  )

@Composable
fun HonarZistanTheme(
  themeMode: ThemeMode = ThemeMode.LIGHT,
  fontScale: Float = 1.0f,
  highContrast: Boolean = false,
  content: @Composable () -> Unit,
) {
  val systemDark = isSystemInDarkTheme()
  val isDark =
    when (themeMode) {
      ThemeMode.SYSTEM -> systemDark
      ThemeMode.DARK -> true
      ThemeMode.LIGHT -> false
    }

  val colorScheme =
    when {
      highContrast && isDark -> HighContrastDarkScheme
      highContrast && !isDark -> HighContrastLightScheme
      isDark -> DarkEditorialScheme
      else -> LightEditorialScheme
    }

  val extraColors =
    if (isDark) {
      HonarExtraColors(
        isDark = true,
        highContrast = highContrast,
        goldAccent = if (highContrast) Color(0xFFFFDC7A) else AntiqueGoldDark,
        poetryAccent = LiteraryRoseDark,
        poetryContainer = Color(0xFF24181C),
        quranAccent = QuranAzureDark,
        quranContainer = Color(0xFF15222B),
        wellbeingAccent = WellbeingSageDark,
        wellbeingContainer = Color(0xFF14241E),
      )
    } else {
      HonarExtraColors(
        isDark = false,
        highContrast = highContrast,
        goldAccent = if (highContrast) Color(0xFF5D4100) else AntiqueGold,
        poetryAccent = LiteraryRose,
        poetryContainer = LiteraryRoseTint,
        quranAccent = QuranAzure,
        quranContainer = QuranAzureTint,
        wellbeingAccent = WellbeingSage,
        wellbeingContainer = WellbeingSageTint,
      )
    }

  CompositionLocalProvider(LocalHonarColors provides extraColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = buildHonarTypography(fontScale),
      shapes = EditorialShapes,
      content = content,
    )
  }
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  HonarZistanTheme(
    themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
    content = content,
  )
}
