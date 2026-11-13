package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AccessibilitySettings
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.honarDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "honar_zistan_prefs")

class PreferencesRepository(private val context: Context) {
  private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val LARGE_TEXT = booleanPreferencesKey("large_text")
    val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
    val FOCUS_MODE_DEFAULT = booleanPreferencesKey("focus_mode_default")
    val LANGUAGE = stringPreferencesKey("language")
  }

  val settingsFlow: Flow<AccessibilitySettings> =
    context.honarDataStore.data.map { prefs ->
      AccessibilitySettings(
        themeMode =
          prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.LIGHT,
        fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
        largeTextEnabled = prefs[Keys.LARGE_TEXT] ?: false,
        highContrastEnabled = prefs[Keys.HIGH_CONTRAST] ?: false,
        reducedMotionEnabled = prefs[Keys.REDUCED_MOTION] ?: false,
        focusModeDefault = prefs[Keys.FOCUS_MODE_DEFAULT] ?: false,
        language =
          prefs[Keys.LANGUAGE]?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
            ?: AppLanguage.PERSIAN,
      )
    }

  suspend fun updateThemeMode(mode: ThemeMode) {
    context.honarDataStore.edit { it[Keys.THEME_MODE] = mode.name }
  }

  suspend fun updateFontScale(scale: Float) {
    context.honarDataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.45f) }
  }

  suspend fun updateLargeText(enabled: Boolean) {
    context.honarDataStore.edit {
      it[Keys.LARGE_TEXT] = enabled
      if (enabled && (it[Keys.FONT_SCALE] ?: 1.0f) < 1.2f) {
        it[Keys.FONT_SCALE] = 1.25f
      } else if (!enabled && (it[Keys.FONT_SCALE] ?: 1.0f) > 1.15f) {
        it[Keys.FONT_SCALE] = 1.0f
      }
    }
  }

  suspend fun updateHighContrast(enabled: Boolean) {
    context.honarDataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
  }

  suspend fun updateReducedMotion(enabled: Boolean) {
    context.honarDataStore.edit { it[Keys.REDUCED_MOTION] = enabled }
  }

  suspend fun updateFocusModeDefault(enabled: Boolean) {
    context.honarDataStore.edit { it[Keys.FOCUS_MODE_DEFAULT] = enabled }
  }

  suspend fun updateLanguage(language: AppLanguage) {
    context.honarDataStore.edit { it[Keys.LANGUAGE] = language.name }
  }
}
