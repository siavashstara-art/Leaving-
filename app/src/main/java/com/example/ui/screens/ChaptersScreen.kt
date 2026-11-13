package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.ContentCategory
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.LocalHonarColors

@Composable
fun ChaptersScreen(
  uiState: HonarZistanUiState,
  onSelectCategory: (ContentCategory?) -> Unit,
  onOpenChapter: (String) -> Unit,
  onToggleBookmark: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current

  Column(modifier = modifier.fillMaxSize().testTag("chapters_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) {
          val cat = uiState.selectedCategory?.titleFa ?: "همه فصل‌ها"
          "کتاب هنر زیستن  ←  فصل‌ها  ←  $cat"
        } else {
          val cat = uiState.selectedCategory?.titleEn ?: "All Chapters"
          "Honar Zistan  →  Chapters  →  $cat"
        }
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "فصل‌های کتاب" else "Chapters",
            subtitle =
              if (isFa) "برگزیدن فصل بر اساس دفتر و موضوع"
              else "Select a chapter by book part or discipline",
          )
        }

        item {
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
              FilterChip(
                selected = uiState.selectedCategory == null,
                onClick = { onSelectCategory(null) },
                modifier = Modifier.minimumInteractiveComponentSize(),
                label = { Text(if (isFa) "همه فصل‌ها" else "All") },
              )
            }
            items(ContentCategory.entries) { category ->
              FilterChip(
                selected = uiState.selectedCategory == category,
                onClick = { onSelectCategory(category) },
                modifier = Modifier.minimumInteractiveComponentSize(),
                label = { Text(if (isFa) category.titleFa else category.titleEn) },
              )
            }
          }
        }

        items(uiState.filteredChapters, key = { it.id }) { chapter ->
          val part = uiState.partFor(chapter)
          val isBookmarked = uiState.bookmarkedChapterIds.contains(chapter.id)
          val isDone = uiState.progressMap[chapter.id]?.isCompleted == true
          val accent = extra.accentFor(chapter.category)

          Surface(
            modifier =
              Modifier.fillMaxWidth()
                .semantics(mergeDescendants = true) {}
                .clickable { onOpenChapter(chapter.id) }
                .testTag("chapter_card_${chapter.id}"),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border =
              BorderStroke(0.75.dp, accent.copy(alpha = if (extra.highContrast) 0.95f else 0.35f)),
          ) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text =
                      if (isFa) {
                        "${part?.titleFa ?: ""} • فصل ${chapter.number.toLocalizedDigits(lang)}"
                      } else {
                        "${part?.titleEn ?: ""} • Chapter ${chapter.number}"
                      },
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = if (isFa) chapter.titleFa else chapter.titleEn,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
                IconButton(
                  onClick = { onToggleBookmark(chapter.id) },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector =
                      if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription =
                      if (isFa) {
                        if (isBookmarked) "حذف نشان فصل" else "نشانه‌گذاری فصل"
                      } else {
                        if (isBookmarked) "Remove bookmark" else "Bookmark chapter"
                      },
                    tint =
                      if (isBookmarked) extra.goldAccent
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = if (isFa) chapter.openingEpigraphFa else chapter.openingEpigraphEn,
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (isFa) chapter.excerptFa else chapter.excerptEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                  CategoryBadge(category = chapter.category, language = lang)
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Outlined.Schedule,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text =
                        if (isFa) "${chapter.readingMinutes.toLocalizedDigits(lang)} دقیقه • ${chapter.sections.size.toLocalizedDigits(lang)} بند"
                        else "${chapter.readingMinutes} min • ${chapter.sections.size} sections",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                }
                if (isDone) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Filled.CheckCircle,
                      contentDescription = if (isFa) "خوانده شده" else "Completed",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (isFa) "مطالعه شد" else "Read",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
