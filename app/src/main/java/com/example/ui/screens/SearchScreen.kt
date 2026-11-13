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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.FriendlyEmptyState
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.LocalHonarColors

@Composable
fun SearchScreen(
  uiState: HonarZistanUiState,
  onQueryChange: (String) -> Unit,
  onOpenChapter: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current
  val results = uiState.searchResults

  Column(modifier = modifier.fillMaxSize().testTag("search_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  جستجو در متن و موضوعات"
        else "Honar Zistan  →  Search"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("search_results_list"),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "جستجو در کتاب" else "Search the Volume",
            subtitle =
              if (isFa) "جستجو در فصل‌ها، بندها، شعرها، آیات و مباحث تندرستی"
              else "Search chapters, sections, poetry, Quranic reflections & wellbeing",
          )
        }

        item {
          OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().testTag("search_input"),
            singleLine = true,
            label = {
              Text(
                if (isFa) "واژه‌ای بنویسید (مثلاً: آرامش، خواب، ذکر، غزل...)"
                else "Enter a keyword (e.g., serenity, sleep, ghazal...)"
              )
            },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
              if (uiState.searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { onQueryChange("") },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = if (isFa) "پاک کردن جستجو" else "Clear search",
                  )
                }
              }
            },
          )
        }

        if (results.isEmpty()) {
          item {
            FriendlyEmptyState(
              title = if (isFa) "عبارتی با این عنوان یافت نشد" else "No matching passages found",
              message =
                if (isFa) "واژه‌ی دیگری مانند «آرامش»، «تنفس»، «صبح» یا «دل» را جستجو کنید."
                else "Try another term such as serenity, breath, morning, or garden.",
            )
          }
        } else {
          items(results, key = { it.id }) { chapter ->
            val accent = extra.accentFor(chapter.category)
            Surface(
              modifier =
                Modifier.fillMaxWidth()
                  .minimumInteractiveComponentSize()
                  .clickable { onOpenChapter(chapter.id) }
                  .testTag("search_result_${chapter.id}"),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(0.75.dp, accent.copy(alpha = 0.4f)),
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  CategoryBadge(category = chapter.category, language = lang)
                  Text(
                    text =
                      if (isFa) "فصل ${chapter.number.toLocalizedDigits(lang)}"
                      else "Chapter ${chapter.number}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = if (isFa) chapter.titleFa else chapter.titleEn,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (isFa) chapter.excerptFa else chapter.excerptEn,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        }
      }
    }
  }
}
