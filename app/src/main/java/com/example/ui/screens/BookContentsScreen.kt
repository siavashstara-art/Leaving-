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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits

@Composable
fun BookContentsScreen(
  uiState: HonarZistanUiState,
  onOpenChapterSection: (chapterId: String, sectionIndex: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN

  Column(modifier = modifier.fillMaxSize().testTag("book_contents_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  فهرست کامل و ساختار کتاب"
        else "Honar Zistan  →  Complete Book Contents"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "فهرست کامل کتاب" else "Complete Table of Contents",
            subtitle =
              if (isFa) "ساختار سلسله‌مراتبی: کتاب ← دفتر ← فصل ← بند"
              else "Editorial hierarchy: Book → Part → Chapter → Section",
          )
        }

        items(uiState.parts, key = { it.id }) { part ->
          val chaptersInPart = uiState.chapters.filter { it.partId == part.id }
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Text(
                text = if (isFa) part.titleFa else part.titleEn,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isFa) part.descriptionFa else part.descriptionEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(12.dp))

              chaptersInPart.forEach { chapter ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                  modifier =
                    Modifier.fillMaxWidth()
                      .minimumInteractiveComponentSize()
                      .clickable { onOpenChapterSection(chapter.id, 0) },
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text =
                        if (isFa) "فصل ${chapter.number.toLocalizedDigits(lang)}: ${chapter.titleFa}"
                        else "Chapter ${chapter.number}: ${chapter.titleEn}",
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CategoryBadge(category = chapter.category, language = lang)
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(start = 14.dp)) {
                  chapter.sections.forEachIndexed { idx, section ->
                    Text(
                      text =
                        if (isFa) "• ${section.titleFa}" else "• ${section.titleEn}",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier =
                        Modifier.fillMaxWidth()
                          .minimumInteractiveComponentSize()
                          .clickable { onOpenChapterSection(chapter.id, idx) }
                          .padding(vertical = 8.dp),
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
