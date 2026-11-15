package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BookCatalog
import com.example.model.AppLanguage
import com.example.model.ContentCategory
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.OrnamentalGoldDivider
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.LocalHonarColors

@Composable
fun HomeScreen(
  uiState: HonarZistanUiState,
  onOpenChapter: (String) -> Unit,
  onOpenBookContents: () -> Unit,
  onOpenCategory: (ContentCategory) -> Unit,
  onOpenAllChapters: () -> Unit,
  onOpenAuthorSection: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current
  val currentChapter =
    uiState.chapters.find { it.id == uiState.lastReadChapterId } ?: uiState.chapters.first()
  val total = uiState.chapters.size.coerceAtLeast(1)
  val done = uiState.completedCount

  Column(modifier = modifier.fillMaxSize().testTag("home_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  پیشخوان و سرآغاز"
        else "Honar Zistan  →  Book Title & Opening"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        // 1. Editorial Book Title Page & Unobscured Frontispiece Plate
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.45f)),
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Text(
                text =
                  if (isFa) "${BookCatalog.BOOK_SUBTITLE_FA} • ${BookCatalog.AUTHOR_FA}"
                  else "${BookCatalog.BOOK_SUBTITLE_EN} • ${BookCatalog.AUTHOR_EN}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = if (isFa) BookCatalog.BOOK_TITLE_FA else BookCatalog.BOOK_TITLE_EN,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text =
                  if (isFa) {
                    "سفری برای شناخت خویشتن، فهم جهان و آموختن هنر بهتر زیستن — نه برای درس دادن، که برای سهیم شدن در تجربه‌های زندگی."
                  } else {
                    "A living journey through self-knowledge, loss, renewal, and the art of meaningful living—shared from real experience."
                  },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(16.dp))
              Image(
                painter = painterResource(id = R.drawable.img_hero_honar_zistan),
                contentDescription =
                  if (isFa) "نگاره‌ی سرآغاز کتاب هنر زیستن: حیاط آرام و سروها"
                  else "Honar Zistan frontispiece illustration: sunlit courtyard and cypresses",
                modifier = Modifier.fillMaxWidth().height(190.dp),
                contentScale = ContentScale.Crop,
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text =
                  if (isFa) "لوح سرآغاز — حیاطِ روشنِ حضور (نسخه نمونه ویرایشی)"
                  else "Frontispiece Plate — The Sunlit Courtyard of Presence (Curated Edition)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              OrnamentalGoldDivider()
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Button(
                  onClick = { onOpenChapter(currentChapter.id) },
                  modifier =
                    Modifier.weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("continue_reading_btn"),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "ادامه مطالعه" else "Continue Reading")
                }
                OutlinedButton(
                  onClick = onOpenBookContents,
                  modifier =
                    Modifier.weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("open_contents_btn"),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ListAlt,
                    contentDescription = null,
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "فهرست کتاب" else "Contents")
                }
              }
            }
          }
        }

        // 2. Quiet Reading Orientation Bar
        item {
          Surface(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text =
                    if (isFa) "جایگاه کنونی: ${currentChapter.titleFa}"
                    else "Current Chapter: ${currentChapter.titleEn}",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.weight(1f),
                )
                Text(
                  text =
                    if (isFa) "${done.toLocalizedDigits(lang)} از ${total.toLocalizedDigits(lang)} فصل"
                    else "$done / $total",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { done.toFloat() / total.toFloat() },
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
              )
            }
          }
        }

        // 3. Book Parts & Subject Divisions (Clear Editorial Architecture)
        item {
          SectionHeaderRow(
            title = if (isFa) "بخش‌های اصلی کتاب" else "Book Parts & Disciplines",
            subtitle =
              if (isFa) "ساختار چهارگانه‌ی هنر زیستن" else "Four interconnected volumes of reflection",
            action = {
              TextButton(
                onClick = onOpenAllChapters,
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Text(if (isFa) "همه فصل‌ها" else "All Chapters")
              }
            },
          )
        }

        items(uiState.parts, key = { it.id }) { part ->
          val partChapters = uiState.chapters.filter { it.partId == part.id }
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outline),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = if (isFa) part.titleFa else part.titleEn,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isFa) part.descriptionFa else part.descriptionEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              partChapters.forEach { ch ->
                Row(
                  modifier =
                    Modifier.fillMaxWidth()
                      .minimumInteractiveComponentSize()
                      .clickable { onOpenChapter(ch.id) }
                      .padding(vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text =
                        if (isFa) "فصل ${ch.number.toLocalizedDigits(lang)}: ${ch.titleFa}"
                        else "Chapter ${ch.number}: ${ch.titleEn}",
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CategoryBadge(category = ch.category, language = lang)
                  }
                  Text(
                    text =
                      if (isFa) "${ch.readingMinutes.toLocalizedDigits(lang)} دقیقه"
                      else "${ch.readingMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                if (ch != partChapters.last()) {
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
              }
            }
          }
        }

        // 4. Direct Subject Access
        item {
          SectionHeaderRow(
            title = if (isFa) "ورود مستقیم به موضوعات" else "Browse by Discipline",
          )
          Spacer(modifier = Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ContentCategory.entries.forEach { category ->
              Surface(
                modifier =
                  Modifier.fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable { onOpenCategory(category) },
                shape = MaterialTheme.shapes.small,
                color = extra.containerFor(category),
                border = BorderStroke(0.75.dp, extra.accentFor(category).copy(alpha = 0.35f)),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (isFa) category.titleFa else category.titleEn,
                      style = MaterialTheme.typography.titleMedium,
                      color = extra.accentFor(category),
                    )
                    Text(
                      text = if (isFa) category.subtitleFa else category.subtitleEn,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                  Text(
                    text = if (isFa) "مشاهده ←" else "Open →",
                    style = MaterialTheme.typography.labelLarge,
                    color = extra.accentFor(category),
                  )
                }
              }
            }
          }
        }

        // 5. Author Colophon Footer
        item {
          Surface(
            modifier =
              Modifier.fillMaxWidth()
                .minimumInteractiveComponentSize()
                .clickable { onOpenAuthorSection() },
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.4f)),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(
                text =
                  if (isFa) "تألیف: سیاوش علی‌میری"
                  else "Authored by Siavash Ali-Miri",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text =
                  if (isFa) "برای شناسنامه‌ی کتاب، نگاره‌ی قلم و تنظیمات مطالعه لمس کنید"
                  else "Tap for author colophon, reading settings & accessibility",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      }
    }
  }
}
