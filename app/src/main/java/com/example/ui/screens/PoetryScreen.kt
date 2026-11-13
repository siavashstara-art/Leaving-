package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.ReaderBlock
import com.example.ui.HonarZistanUiState
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.OrnamentalGoldDivider
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.LocalHonarColors
import com.example.ui.theme.NastaliqFontFamily

@Composable
fun PoetryScreen(
  uiState: HonarZistanUiState,
  onOpenChapter: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current
  val context = LocalContext.current

  val poetryEntries =
    uiState.chapters.flatMap { chapter ->
      chapter.blocks.filterIsInstance<ReaderBlock.Poetry>().map { poetry -> Pair(chapter, poetry) }
    }

  Column(modifier = modifier.fillMaxSize().testTag("poetry_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  دفتر دوم  ←  شعر و ادب"
        else "Honar Zistan  →  Part II  →  Poetry & Literature"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "شعر و ادب" else "Poetry & Literature",
            subtitle =
              if (isFa) "گزیده‌ی سروده‌های کتاب با خط نستعلیق و حاشیه متین"
              else "Selected Persian verse with selective Nastaliq typography",
          )
        }

        // Single dedicated nocturnal garden plate (non-repetitive)
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.45f)),
          ) {
            Column {
              Image(
                painter = painterResource(id = R.drawable.img_poetry_garden),
                contentDescription =
                  if (isFa) "نگارگری شبانه باغ شعر با سروها و ستارگان طلایی"
                  else "Nocturnal garden plate illustrating contemplative Persian poetry",
                modifier = Modifier.fillMaxWidth().height(170.dp),
                contentScale = ContentScale.Crop,
              )
              Text(
                text =
                  if (isFa) "لوح باغ شبانه — تمثیل روشنایی امید در شعر فارسی"
                  else "Nocturnal Garden Plate — Symbol of quiet light in Persian verse",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(12.dp),
              )
            }
          }
        }

        items(poetryEntries) { (chapter, poetry) ->
          Surface(
            modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.5f)),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(
                text = if (isFa) chapter.titleFa else chapter.titleEn,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isFa) poetry.poetFa else poetry.poetEn,
                style = MaterialTheme.typography.labelLarge,
                color = extra.poetryAccent,
                textAlign = TextAlign.Center,
              )
              OrnamentalGoldDivider()

              CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                  poetry.coupletsFa.forEach { (h1, h2) ->
                    Text(
                      text = "$h1\n$h2",
                      fontFamily = NastaliqFontFamily,
                      fontSize = (22 * uiState.settings.fontScale).sp,
                      lineHeight = (46 * uiState.settings.fontScale).sp,
                      textAlign = TextAlign.Center,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }

              if (!isFa) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = poetry.translationEn,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                )
              }

              Spacer(modifier = Modifier.height(18.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                OutlinedButton(
                  onClick = {
                    val shareBody = buildString {
                      appendLine(if (isFa) chapter.titleFa else chapter.titleEn)
                      appendLine()
                      poetry.coupletsFa.forEach { (h1, h2) ->
                        appendLine(h1)
                        appendLine(h2)
                        appendLine()
                      }
                      appendLine("— ${if (isFa) poetry.poetFa else poetry.poetEn}")
                    }
                    val intent =
                      Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                          Intent.EXTRA_SUBJECT,
                          if (isFa) chapter.titleFa else chapter.titleEn,
                        )
                        putExtra(Intent.EXTRA_TEXT, shareBody)
                      }
                    context.startActivity(
                      Intent.createChooser(
                        intent,
                        if (isFa) "اشتراک‌گذاری شعر" else "Share Poem",
                      )
                    )
                  },
                  modifier =
                    Modifier.weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("share_poem_btn_${chapter.id}"),
                ) {
                  Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "اشتراک‌گذاری" else "Share")
                }

                Button(
                  onClick = { onOpenChapter(chapter.id) },
                  modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                ) {
                  Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "مطالعه در فصل" else "Read Chapter")
                }
              }
            }
          }
        }
      }
    }
  }
}
