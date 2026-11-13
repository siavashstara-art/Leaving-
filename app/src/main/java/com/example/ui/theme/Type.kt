package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val VazirmatnFontFamily = FontFamily(Font(R.font.vazirmatn, FontWeight.Normal))
val AmiriFontFamily = FontFamily(Font(R.font.amiri, FontWeight.Normal))
val NastaliqFontFamily = FontFamily(Font(R.font.noto_nastaliq_urdu, FontWeight.Normal))

fun buildHonarTypography(scale: Float = 1.0f): Typography {
  val s = scale.coerceIn(0.85f, 1.45f)
  return Typography(
    displaySmall =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (30 * s).sp,
        lineHeight = (44 * s).sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (24 * s).sp,
        lineHeight = (36 * s).sp,
      ),
    headlineSmall =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (20 * s).sp,
        lineHeight = (30 * s).sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = (18 * s).sp,
        lineHeight = (28 * s).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (16 * s).sp,
        lineHeight = (24 * s).sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (16 * s).sp,
        lineHeight = (30 * s).sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (14 * s).sp,
        lineHeight = (24 * s).sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (14 * s).sp,
        lineHeight = (20 * s).sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (12 * s).sp,
        lineHeight = (18 * s).sp,
      ),
  )
}

val Typography = buildHonarTypography(1.0f)
