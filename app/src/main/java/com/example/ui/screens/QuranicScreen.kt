package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.ReaderBlock
import com.example.ui.HonarZistanUiState
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow

@Composable
fun QuranicScreen(
  uiState: HonarZistanUiState,
  onOpenChapter: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN

  val verseItems =
    uiState.chapters.flatMap { chapter ->
      chapter.blocks.filterIsInstance<ReaderBlock.QuranicVerse>().map { verse ->
        Pair(chapter, verse)
      }
    }

  Column(modifier = modifier.fillMaxSize().testTag("quranic_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  دفتر دوم  ←  تأملات قرآنی"
        else "Honar Zistan  →  Part II  →  Quranic Reflections"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "تأملات قرآنی" else "Quranic Reflections",
            subtitle =
              if (isFa) "آیات برگزیده با تفکیک متن عربی، ترجمه، توضیح واژگانی، تأمل نویسنده و نشانی معتبر"
              else "Verified verses with separate translation, explanation & author reflection",
          )
        }

        items(verseItems) { (chapter, verse) ->
          Column {
            QuranicVerseEditorialBlock(
              block = verse,
              isFa = isFa,
              fontScale = uiState.settings.fontScale,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
              onClick = { onOpenChapter(chapter.id) },
              modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
            ) {
              Text(
                if (isFa) "مطالعه در بافتِ ${chapter.titleFa}" else "Open in ${chapter.titleEn}"
              )
            }
          }
        }
      }
    }
  }
}
