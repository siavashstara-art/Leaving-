package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BookCatalog
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.ui.HonarZistanUiState
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.OrnamentalGoldDivider
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.LocalHonarColors

@Composable
fun SettingsAuthorScreen(
  uiState: HonarZistanUiState,
  onThemeModeChange: (ThemeMode) -> Unit,
  onFontScaleChange: (Float) -> Unit,
  onLargeTextChange: (Boolean) -> Unit,
  onHighContrastChange: (Boolean) -> Unit,
  onReducedMotionChange: (Boolean) -> Unit,
  onFocusModeDefaultChange: (Boolean) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  modifier: Modifier = Modifier,
) {
  val settings = uiState.settings
  val isFa = settings.language == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current

  Column(modifier = modifier.fillMaxSize().testTag("settings_author_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  تنظیمات و شناسنامه‌ی اثر"
        else "Honar Zistan  →  Settings & Author Colophon"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("settings_author_list"),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "تنظیمات مطالعه و دسترس‌پذیری" else "Reading & Accessibility Settings",
            subtitle =
              if (isFa) "تنظیم اندازه‌ی قلم، کنتراست، حرکت و زبان کتاب"
              else "Adjust typography, contrast, motion, and reading direction",
          )
        }

        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              Text(
                text = if (isFa) "زبان و جهت چیدمان (RTL / LTR)" else "Language & Layout Direction",
                style = MaterialTheme.typography.titleMedium,
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                  selected = settings.language == AppLanguage.PERSIAN,
                  onClick = { onLanguageChange(AppLanguage.PERSIAN) },
                  modifier =
                    Modifier.minimumInteractiveComponentSize().testTag("lang_persian_chip"),
                  label = { Text("فارسی (RTL)") },
                )
                FilterChip(
                  selected = settings.language == AppLanguage.ENGLISH,
                  onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                  modifier =
                    Modifier.minimumInteractiveComponentSize().testTag("lang_english_chip"),
                  label = { Text("English (LTR)") },
                )
              }

              Text(
                text = if (isFa) "پوسته‌ی کاغذ و نور" else "Paper & Lighting Theme",
                style = MaterialTheme.typography.titleMedium,
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                  selected = settings.themeMode == ThemeMode.LIGHT,
                  onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                  modifier = Modifier.minimumInteractiveComponentSize().testTag("theme_light_chip"),
                  label = { Text(if (isFa) "عاجی روشن" else "Warm Ivory") },
                )
                FilterChip(
                  selected = settings.themeMode == ThemeMode.DARK,
                  onClick = { onThemeModeChange(ThemeMode.DARK) },
                  modifier = Modifier.minimumInteractiveComponentSize().testTag("theme_dark_chip"),
                  label = { Text(if (isFa) "جنگل شب" else "Forest Dark") },
                )
                FilterChip(
                  selected = settings.themeMode == ThemeMode.SYSTEM,
                  onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                  label = { Text(if (isFa) "سیستم" else "System") },
                )
              }

              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(
                    text = if (isFa) "اندازه‌ی قلم متن" else "Body Text Scale",
                    style = MaterialTheme.typography.titleMedium,
                  )
                  Text(
                    text = "${(settings.fontScale * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                  )
                }
                Slider(
                  value = settings.fontScale,
                  onValueChange = onFontScaleChange,
                  valueRange = 0.85f..1.45f,
                  modifier = Modifier.fillMaxWidth().testTag("font_scale_slider"),
                )
              }

              AccessibilityToggleRow(
                title = if (isFa) "متن درشت (Large Text)" else "Large Text Mode",
                subtitle =
                  if (isFa) "افزایش خوانایی برای مطالعه طولانی"
                  else "Boosts scale across prose, verse & UI",
                checked = settings.largeTextEnabled,
                onCheckedChange = onLargeTextChange,
                testTag = "toggle_large_text",
              )

              AccessibilityToggleRow(
                title = if (isFa) "کنتراست بالا (High Contrast)" else "High Contrast Mode",
                subtitle =
                  if (isFa) "تضاد پررنگ قلم و پس‌زمینه (WCAG AAA)"
                  else "Maximizes ink and rule contrast",
                checked = settings.highContrastEnabled,
                onCheckedChange = onHighContrastChange,
                testTag = "toggle_high_contrast",
              )

              AccessibilityToggleRow(
                title = if (isFa) "کاهش حرکت (Reduced Motion)" else "Reduced Motion",
                subtitle =
                  if (isFa) "حذف جابه‌جایی‌های غیرضروری"
                  else "Minimizes visual transitions",
                checked = settings.reducedMotionEnabled,
                onCheckedChange = onReducedMotionChange,
                testTag = "toggle_reduced_motion",
              )

              AccessibilityToggleRow(
                title = if (isFa) "حالت تمرکز آرام (ADHD Focus)" else "ADHD-Friendly Focus Mode",
                subtitle =
                  if (isFa) "پنهان کردن عناصر حاشیه‌ای در حین مطالعه"
                  else "Keeps reading pages focused on text alone",
                checked = settings.focusModeDefault,
                onCheckedChange = onFocusModeDefaultChange,
                testTag = "toggle_focus_mode",
              )
            }
          }
        }

        item {
          SectionHeaderRow(
            title = if (isFa) "درباره‌ی مؤلف و شناسنامه‌ی اثر" else "Author & Book Heritage",
            subtitle = if (isFa) "سیاوش علی‌میری • هنر زیستن" else "Siavash Ali-Miri • Honar Zistan",
          )
        }

        // Single non-repetitive editorial colophon plate
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.45f)),
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                  painter = painterResource(id = R.drawable.img_author_quill),
                  contentDescription =
                    if (isFa) "نگاره‌ی قلم نی، دوات برنجی و گل یاس"
                    else "Editorial still life of reed pen, brass inkwell, and jasmine",
                  modifier = Modifier.size(68.dp),
                  contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                  Text(
                    text = if (isFa) "سیاوش علی‌میری" else "Siavash Ali-Miri",
                    style = MaterialTheme.typography.headlineSmall,
                  )
                  Text(
                    text =
                      if (isFa) "مؤلف کتاب مصور زندگی | هنر زیستن"
                      else "Author of Honar Zistan — Illustrated Book of Living",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                  )
                }
              }
              OrnamentalGoldDivider()
              Text(
                text = if (isFa) BookCatalog.authorBioFa else BookCatalog.authorBioEn,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }
        }

        // Copyright and Author Portfolio Colophon
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.35f)),
          ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = if (isFa) "حقوق مادی و معنوی اثر" else "Copyright & Intellectual Property",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = if (isFa) 
                  "کلیه حقوق مادی و معنوی این اثر متعلق است به آقای اکبر علی‌میری (سیاوش)، توسعه‌دهنده."
                else 
                  "All material and intellectual rights of this work belong to Mr. Akbar Ali-Miri (Siavash), Developer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )

              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

              Text(
                text = if (isFa) "سایر آثار و محصولات از همین توسعه‌دهنده (سیاوش):" else "Other Works by the Same Developer (Siavash):",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = if (isFa) 
                  "• فرش بازار ایران و جهان (به یاد پدر مرحوم حاج حسین‌آقای علی‌میری، تاجر بزرگ فرش ایران و جهان)\n" +
                  "• لینک‌لینکوی (LinkLinko)\n" +
                  "• آموزش زبان فارسی به انگلیسی‌زبانان\n" +
                  "• نسل سوم بچه‌های ایرانی خارج از کشور (در پاسداشت زبان مادری‌ام فارسی)\n" +
                  "• ایبل‌سیتی فریلنسر (Able City Freelancer)\n" +
                  "• ایبل‌سیتی اجوکیشن (Able City Education)\n" +
                  "• توانا مسترها (Tavana Masters)\n" +
                  "• اکوسیستم آفرینش (آفرینا / Aferina)\n" +
                  "• شهر جدید نیومتا / متاورس جهان (FB New Meta)\n" +
                  "• ساختمان‌یار، سالن‌یار، کلینیک‌یار، تالار‌یار، دکورمیت\n" +
                  "• برنامه‌ی کمدی پایلت (Comedy Pilot)\n" +
                  "• شطرنج هم‌جهان\n" +
                  "• توانا فورک، توانا اگنت\n" +
                  "• هوش مصنوعی نکسوس (Nexus AI)\n" +
                  "• سرویس خدمات هوش مصنوعی\n" +
                  "• تی‌دیپلوی (T-Deploy)\n" +
                  "• طلایار (TalaYar)\n" +
                  "• روزگارد (Rozgard)\n" +
                  "• و ده‌ها برنامه‌ی دیگر برای فریلنسرها، تولیدکنندگان محتوا و توسعه‌دهندگان وب، اپلیکیشن و گیم.\n" +
                  "• اپلیکیشن اختصاصی بدنسازی برای همه رشته‌ها\n" +
                  "• کتاب جامع بزرگ بدنسازی برای همه رشته‌ها (کتاب اینترنتی / دیجیتال)"
                else
                  "• Iranian & Global Carpet Market (Dedicated to the late father Haj Hossein Ali-Miri, great carpet merchant)\n" +
                  "• LinkLinko\n" +
                  "• Persian Language Learning for English Speakers\n" +
                  "• 3rd Generation Iranian Youth Abroad (Honoring the Persian Mother Tongue)\n" +
                  "• Able City Freelancer & Education\n" +
                  "• Tavana Masters & Fork & Agent\n" +
                  "• Aferina Creation Ecosystem\n" +
                  "• New Meta City / FB New Meta Metaverse\n" +
                  "• SakhtemanYar, SalonYar, ClinicYar, TalarYar, DecorMate\n" +
                  "• Comedy Pilot, Universal Chess\n" +
                  "• Nexus AI & AI Services\n" +
                  "• T-Deploy, TalaYar, Rozgard\n" +
                  "• And dozens of other applications for freelancers, content creators, web/app/game developers.\n" +
                  "• Universal Bodybuilding Application for all disciplines\n" +
                  "• Comprehensive Grand Bodybuilding eBook (Digital Edition)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        item {
          Surface(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = if (isFa) "حریم خصوصی و نگهداری آفلاین" else "Offline Storage & Privacy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text =
                  if (isFa) {
                    "متن کتاب، آیات، اشعار، نشانه‌گذاری‌ها و یادداشت‌های شخصی شما به صورت کاملاً آفلاین روی دستگاه نگهداری می‌شوند. این برنامه بدون تبلیغات و بدون دسترسی غیرضروری است."
                  } else {
                    "Book text, verses, poetry, bookmarks, and personal notes stay offline and private on your device—with zero ads and zero unnecessary permissions."
                  },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AccessibilityToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
      Text(text = title, style = MaterialTheme.typography.titleMedium)
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.minimumInteractiveComponentSize().testTag(testTag),
    )
  }
}
