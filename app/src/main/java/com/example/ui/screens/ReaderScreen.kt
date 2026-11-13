package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CenterFocusWeak
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookCatalog
import com.example.model.AppLanguage
import com.example.model.Chapter
import com.example.model.ReaderBlock
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.OrnamentalGoldDivider
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalHonarColors
import com.example.ui.theme.NastaliqFontFamily

@Composable
fun ReaderScreen(
  chapter: Chapter,
  initialSectionIndex: Int,
  uiState: HonarZistanUiState,
  transcript: String,
  onBack: () -> Unit,
  onOpenBookContents: () -> Unit,
  onToggleBookmark: () -> Unit,
  onToggleCompleted: () -> Unit,
  onSaveNote: (String) -> Unit,
  onToggleFocusMode: () -> Unit,
  onFontScaleChange: (Float) -> Unit,
  onToggleAudio: () -> Unit,
  onChangeAudioSpeed: (Float) -> Unit,
  onNavigateChapter: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current
  val focusMode = uiState.focusModeActiveInReader
  val isBookmarked = uiState.bookmarkedChapterIds.contains(chapter.id)
  val isDone = uiState.progressMap[chapter.id]?.isCompleted == true
  val part = uiState.partFor(chapter)
  val audioState = uiState.audioState
  val isAudioPlayingThis = audioState.isPlaying && audioState.activeChapterId == chapter.id

  var selectedSectionIndex by
    remember(chapter.id, initialSectionIndex) {
      mutableIntStateOf(initialSectionIndex.coerceIn(0, (chapter.sections.size - 1).coerceAtLeast(0)))
    }
  val activeSection = chapter.sections.getOrNull(selectedSectionIndex) ?: chapter.sections.first()

  var showNoteDialog by remember { mutableStateOf(false) }
  var showTranscript by remember { mutableStateOf(false) }
  var noteInput by remember { mutableStateOf("") }

  val chapterIndex = uiState.chapters.indexOfFirst { it.id == chapter.id }.coerceAtLeast(0)
  val prevChapter = uiState.chapters.getOrNull(chapterIndex - 1)
  val nextChapter = uiState.chapters.getOrNull(chapterIndex + 1)

  Column(modifier = modifier.fillMaxSize().testTag("reader_screen")) {
    // Persistent Top Reader Bar with explicit labels
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextButton(
            onClick = onBack,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("reader_back_btn"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isFa) "بازگشت" else "Back")
          }
          Spacer(modifier = Modifier.weight(1f))
          TextButton(
            onClick = onOpenBookContents,
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Text(if (isFa) "فهرست" else "Contents")
          }
          IconButton(
            onClick = onToggleFocusMode,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("focus_mode_toggle_btn"),
          ) {
            Icon(
              imageVector =
                if (focusMode) Icons.Filled.CenterFocusStrong else Icons.Outlined.CenterFocusWeak,
              contentDescription = if (isFa) "حالت تمرکز آرام" else "Toggle Focus Mode",
              tint =
                if (focusMode) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("reader_bookmark_btn"),
          ) {
            Icon(
              imageVector =
                if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isFa) "نشانه‌گذاری فصل" else "Bookmark chapter",
              tint = if (isBookmarked) extra.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          IconButton(
            onClick = { showNoteDialog = true },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("reader_add_note_btn"),
          ) {
            Icon(
              imageVector = Icons.Outlined.EditNote,
              contentDescription = if (isFa) "افزودن یادداشت" else "Add note",
            )
          }
        }

        // Persistent Hierarchy Breadcrumb: Book -> Part -> Chapter -> Section
        LocationBreadcrumbBar(
          trail =
            if (isFa) {
              "${part?.titleFa ?: ""}  ←  فصل ${chapter.number.toLocalizedDigits(lang)}  ←  ${activeSection.titleFa}"
            } else {
              "${part?.titleEn ?: ""}  →  Ch. ${chapter.number}  →  ${activeSection.titleEn}"
            }
        )
      }
    }

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
      ) {
        // 1. Editorial Chapter Opening
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              CategoryBadge(category = chapter.category, language = lang)
              Text(
                text =
                  if (isFa) "فصل ${chapter.number.toLocalizedDigits(lang)} از ${uiState.chapters.size.toLocalizedDigits(lang)}"
                  else "Chapter ${chapter.number} of ${uiState.chapters.size}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Text(
              text = if (isFa) chapter.titleFa else chapter.titleEn,
              style = MaterialTheme.typography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = if (isFa) chapter.openingEpigraphFa else chapter.openingEpigraphEn,
              style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
              color = MaterialTheme.colorScheme.primary,
            )
            OrnamentalGoldDivider()
          }
        }

        // 2. Section Switcher + Font Scale Controls (hidden in Focus Mode)
        if (!focusMode) {
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(chapter.sections) { idx, section ->
                  FilterChip(
                    selected = selectedSectionIndex == idx,
                    onClick = { selectedSectionIndex = idx },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    label = { Text(if (isFa) section.titleFa else section.titleEn) },
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = if (isFa) "اندازه‌ی قلم:" else "Text size:",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  OutlinedButton(
                    onClick = { onFontScaleChange(uiState.settings.fontScale - 0.1f) },
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("reader_font_dec"),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                  ) {
                    Text("A-")
                  }
                  Text(
                    text = "${(uiState.settings.fontScale * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                  )
                  OutlinedButton(
                    onClick = { onFontScaleChange(uiState.settings.fontScale + 0.1f) },
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("reader_font_inc"),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                  ) {
                    Text("A+")
                  }
                }
              }
            }
          }

          // 3. Honest Audio Narration Panel (no simulated playback!)
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.small,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                  ) {
                    Icon(
                      imageVector =
                        if (audioState.isAudioAvailable) Icons.Outlined.GraphicEq
                        else Icons.AutoMirrored.Outlined.VolumeOff,
                      contentDescription = null,
                      tint =
                        if (audioState.isAudioAvailable) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = if (isFa) "خوانش صوتی فصل" else "Audio Playback",
                        style = MaterialTheme.typography.titleMedium,
                      )
                      Text(
                        text =
                          if (isFa) audioState.statusMessageFa else audioState.statusMessageEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                      )
                    }
                  }
                  Button(
                    onClick = onToggleAudio,
                    enabled = audioState.isAudioAvailable,
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("audio_play_btn"),
                  ) {
                    Icon(
                      imageVector =
                        if (isAudioPlayingThis) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                      contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text =
                        when {
                          !audioState.isAudioAvailable -> if (isFa) "ناموجود" else "Unavailable"
                          isAudioPlayingThis -> if (isFa) "توقف" else "Pause"
                          else -> if (isFa) "پخش" else "Play"
                        }
                    )
                  }
                }

                if (audioState.isAudioAvailable) {
                  val progressFraction =
                    if (audioState.totalLines > 0) {
                      (audioState.currentLineIndex + 1f) / audioState.totalLines.toFloat()
                    } else 0f
                  LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                  )
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Text(
                      text = if (isFa) "سرعت:" else "Speed:",
                      style = MaterialTheme.typography.labelSmall,
                    )
                    listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                      FilterChip(
                        selected = audioState.playbackSpeed == speed,
                        onClick = { onChangeAudioSpeed(speed) },
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        label = { Text("${speed}x") },
                      )
                    }
                  }
                }

                TextButton(
                  onClick = { showTranscript = !showTranscript },
                  modifier =
                    Modifier.minimumInteractiveComponentSize().testTag("transcript_toggle_btn"),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text =
                      if (showTranscript) {
                        if (isFa) "بستن رونوشت متنی" else "Hide Audio Transcript"
                      } else {
                        if (isFa) "مشاهده رونوشت متنی کامل" else "View Full Transcript"
                      }
                  )
                }

                AnimatedVisibility(visible = showTranscript) {
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                  ) {
                    Text(
                      text = transcript,
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.padding(12.dp),
                    )
                  }
                }
              }
            }
          }
        }

        // 4. Independent Active Section Header
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
          ) {
            Column {
              Text(
                text = if (isFa) activeSection.titleFa else activeSection.titleEn,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.height(6.dp))
              HorizontalDivider(color = extra.goldAccent.copy(alpha = 0.45f))
            }
          }
        }

        // 5. Active Section Body Blocks
        itemsIndexed(activeSection.blocks) { _, block ->
          when (block) {
            is ReaderBlock.Prose -> {
              Text(
                text = if (isFa) block.textFa else block.textEn,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
              )
            }

            is ReaderBlock.Poetry -> {
              Surface(
                modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                shape = MaterialTheme.shapes.medium,
                color = extra.poetryContainer,
                border = BorderStroke(0.75.dp, extra.poetryAccent.copy(alpha = 0.4f)),
              ) {
                Column(
                  modifier = Modifier.fillMaxWidth().padding(22.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                  Text(
                    text = if (isFa) block.poetFa else block.poetEn,
                    style = MaterialTheme.typography.labelLarge,
                    color = extra.poetryAccent,
                  )
                  OrnamentalGoldDivider()
                  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                      block.coupletsFa.forEach { (h1, h2) ->
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
                      text = block.translationEn,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      textAlign = TextAlign.Center,
                    )
                  }
                }
              }
            }

            is ReaderBlock.QuranicVerse -> {
              QuranicVerseEditorialBlock(
                block = block,
                isFa = isFa,
                fontScale = uiState.settings.fontScale,
              )
            }

            is ReaderBlock.Illustration -> {
              if (!focusMode) {
                Surface(
                  modifier = Modifier.fillMaxWidth(),
                  shape = MaterialTheme.shapes.small,
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                  Column {
                    Image(
                      painter = painterResource(id = block.drawableRes),
                      contentDescription = if (isFa) block.altTextFa else block.altTextEn,
                      modifier = Modifier.fillMaxWidth().height(190.dp),
                      contentScale = ContentScale.Crop,
                    )
                    Column(modifier = Modifier.padding(12.dp)) {
                      Text(
                        text = if (isFa) block.captionFa else block.captionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text =
                          if (isFa) "مضمون نگاره: ${block.conceptFa}"
                          else "Editorial concept: ${block.conceptEn}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // 6. Section Pager (within Chapter)
        if (chapter.sections.size > 1) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              if (selectedSectionIndex > 0) {
                OutlinedButton(
                  onClick = { selectedSectionIndex -= 1 },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Text(if (isFa) "بند قبلی فصل" else "Previous Section")
                }
              } else {
                Spacer(modifier = Modifier.width(1.dp))
              }

              if (selectedSectionIndex < chapter.sections.lastIndex) {
                Button(
                  onClick = { selectedSectionIndex += 1 },
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Text(if (isFa) "بند بعدی فصل" else "Next Section")
                }
              }
            }
          }
        }

        // 7. Editorial Chapter Ending & Inter-Chapter Navigation
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.4f)),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Text(
                text = if (isFa) chapter.closingSummaryFa else chapter.closingSummaryEn,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Button(
                onClick = onToggleCompleted,
                modifier =
                  Modifier.fillMaxWidth().minimumInteractiveComponentSize().testTag("mark_read_btn"),
              ) {
                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text =
                    if (isDone) {
                      if (isFa) "به عنوان مطالعه‌شده ثبت شد" else "Marked as Read"
                    } else {
                      if (isFa) "ثبت پایان مطالعه‌ی این فصل" else "Mark Chapter as Read"
                    }
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                if (prevChapter != null) {
                  OutlinedButton(
                    onClick = { onNavigateChapter(prevChapter.id) },
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                  ) {
                    Text(if (isFa) "فصل قبلی" else "Previous Chapter")
                  }
                }
                if (nextChapter != null) {
                  OutlinedButton(
                    onClick = { onNavigateChapter(nextChapter.id) },
                    modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                  ) {
                    Text(if (isFa) "فصل بعدی" else "Next Chapter")
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  if (showNoteDialog) {
    AlertDialog(
      onDismissRequest = { showNoteDialog = false },
      title = {
        Text(
          text = if (isFa) "یادداشت شخصی برای این فصل" else "Personal Reflection Note",
          style = MaterialTheme.typography.titleLarge,
        )
      },
      text = {
        OutlinedTextField(
          value = noteInput,
          onValueChange = { noteInput = it },
          label = { Text(if (isFa) "تأمل خود را بنویسید..." else "Write your reflection...") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("note_dialog_input"),
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (noteInput.isNotBlank()) {
              onSaveNote(noteInput)
              noteInput = ""
              showNoteDialog = false
            }
          },
          modifier = Modifier.minimumInteractiveComponentSize().testTag("note_dialog_save_btn"),
        ) {
          Text(if (isFa) "ذخیره یادداشت" else "Save")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showNoteDialog = false },
          modifier = Modifier.minimumInteractiveComponentSize(),
        ) {
          Text(if (isFa) "انصراف" else "Cancel")
        }
      },
    )
  }
}

@Composable
fun QuranicVerseEditorialBlock(
  block: ReaderBlock.QuranicVerse,
  isFa: Boolean,
  fontScale: Float,
  modifier: Modifier = Modifier,
) {
  val extra = LocalHonarColors.current
  Surface(
    modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
    shape = MaterialTheme.shapes.medium,
    color = extra.quranContainer,
    border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.55f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // Verified Reference Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = if (isFa) "آیه شریفه" else "Quranic Verse",
          style = MaterialTheme.typography.labelLarge,
          color = extra.quranAccent,
        )
        Text(
          text = if (isFa) block.surahRefFa else block.surahRefEn,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      // Compact, dignified Arabic Original Verse (always RTL)
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
          text = block.arabicText,
          fontFamily = AmiriFontFamily,
          fontSize = (22 * fontScale).sp,
          lineHeight = (38 * fontScale).sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        )
      }

      HorizontalDivider(color = extra.goldAccent.copy(alpha = 0.35f))

      // 1. Translation
      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
          text = if (isFa) "ترجمه" else "Translation",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = extra.quranAccent,
        )
        Text(
          text = if (isFa) block.translationFa else block.translationEn,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      // 2. Explanation
      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
          text = if (isFa) "توضیح واژگانی و مضمون" else "Linguistic & Thematic Explanation",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold,
          color = extra.quranAccent,
        )
        Text(
          text = if (isFa) block.explanationFa else block.explanationEn,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      // 3. Author's Personal Reflection
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, extra.goldAccent.copy(alpha = 0.35f)),
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text =
              if (isFa) "تأمل نویسنده (${BookCatalog.AUTHOR_FA})"
              else "Author's Personal Reflection (${BookCatalog.AUTHOR_EN})",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = if (isFa) block.reflectionFa else block.reflectionEn,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}
