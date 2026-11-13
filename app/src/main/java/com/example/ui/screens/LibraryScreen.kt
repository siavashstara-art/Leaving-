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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.ui.HonarZistanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.FriendlyEmptyState
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.LocalHonarColors

@Composable
fun LibraryScreen(
  uiState: HonarZistanUiState,
  onOpenChapter: (String) -> Unit,
  onToggleBookmark: (String) -> Unit,
  onSaveNote: (chapterId: String, content: String) -> Unit,
  onDeleteNote: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current
  var selectedTab by remember { mutableIntStateOf(0) }
  var showNewNoteDialog by remember { mutableStateOf(false) }
  var noteText by remember { mutableStateOf("") }
  var selectedChapterForNote by remember { mutableStateOf(uiState.chapters.first().id) }

  val bookmarkedChapters =
    uiState.chapters.filter { uiState.bookmarkedChapterIds.contains(it.id) }

  Column(modifier = modifier.fillMaxSize().testTag("library_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  موارد ذخیره‌شده و یادداشت‌ها"
        else "Honar Zistan  →  Saved Items & Notes"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
          SectionHeaderRow(
            title = if (isFa) "ذخیره‌شده‌ها و یادداشت‌ها" else "Saved Items & Notes",
            subtitle =
              if (isFa) "نشانه‌های مطالعه و یادداشت‌های شخصی شما"
              else "Bookmarked chapters & personal reflections",
            action = {
              Button(
                onClick = { showNewNoteDialog = true },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("add_note_fab_btn"),
              ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isFa) "یادداشت نو" else "New Note")
              }
            },
          )
        }

        TabRow(selectedTabIndex = selectedTab) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("tab_bookmarks"),
            text = {
              Text(
                if (isFa) "نشانه‌ها (${bookmarkedChapters.size})"
                else "Bookmarks (${bookmarkedChapters.size})"
              )
            },
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("tab_notes"),
            text = {
              Text(
                if (isFa) "یادداشت‌ها (${uiState.notes.size})" else "Notes (${uiState.notes.size})"
              )
            },
          )
        }

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 22.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          if (selectedTab == 0) {
            if (bookmarkedChapters.isEmpty()) {
              item {
                FriendlyEmptyState(
                  title =
                    if (isFa) "فصلی هنوز ذخیره نشده است" else "No saved chapters yet",
                  message =
                    if (isFa) "در حین مطالعه، نشانه‌ی بالای صفحه را انتخاب کنید تا فصل‌های دلخواهتان اینجا بمانند."
                    else "Select the bookmark icon while reading to keep chapters in this list.",
                )
              }
            } else {
              items(bookmarkedChapters, key = { it.id }) { chapter ->
                Surface(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clickable { onOpenChapter(chapter.id) }
                      .testTag("bookmark_card_${chapter.id}"),
                  shape = MaterialTheme.shapes.medium,
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.45f)),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      CategoryBadge(category = chapter.category, language = lang)
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = if (isFa) chapter.titleFa else chapter.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                      )
                      Text(
                        text = if (isFa) chapter.excerptFa else chapter.excerptEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                      )
                    }
                    IconButton(
                      onClick = { onToggleBookmark(chapter.id) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = if (isFa) "حذف نشان" else "Remove bookmark",
                        tint = extra.goldAccent,
                      )
                    }
                  }
                }
              }
            }
          } else {
            if (uiState.notes.isEmpty()) {
              item {
                FriendlyEmptyState(
                  title =
                    if (isFa) "دفتر یادداشت شما خالی است" else "Your reflection notes are empty",
                  message =
                    if (isFa) "تأملاتی که در حاشیه‌ی مطالعه می‌نویسید در این بخش نگهداری می‌شوند."
                    else "Reflections written while reading appear here.",
                )
              }
            } else {
              items(uiState.notes, key = { it.id }) { note ->
                val chapter = uiState.chapters.find { it.id == note.chapterId }
                Surface(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clickable { chapter?.let { onOpenChapter(it.id) } }
                      .testTag("note_card_${note.id}"),
                  shape = MaterialTheme.shapes.medium,
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.Top,
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text =
                          if (isFa) (chapter?.titleFa ?: "یادداشت آزاد")
                          else (chapter?.titleEn ?: "Reflection"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                      )
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                      )
                    }
                    IconButton(
                      onClick = { onDeleteNote(note.id) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = if (isFa) "حذف یادداشت" else "Delete note",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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

  if (showNewNoteDialog) {
    AlertDialog(
      onDismissRequest = { showNewNoteDialog = false },
      title = { Text(if (isFa) "ثبت یادداشت تازه" else "Add Personal Note") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = if (isFa) "مرتبط با فصل:" else "Linked chapter:",
            style = MaterialTheme.typography.labelLarge,
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.chapters.take(3).forEach { ch ->
              FilterChip(
                selected = selectedChapterForNote == ch.id,
                onClick = { selectedChapterForNote = ch.id },
                modifier = Modifier.minimumInteractiveComponentSize(),
                label = { Text(if (isFa) "فصل ${ch.number}" else "Ch. ${ch.number}") },
              )
            }
          }
          OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            label = { Text(if (isFa) "متن یادداشت شما" else "Your reflection") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("library_note_input"),
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (noteText.isNotBlank()) {
              onSaveNote(selectedChapterForNote, noteText)
              noteText = ""
              selectedTab = 1
              showNewNoteDialog = false
            }
          },
          modifier = Modifier.minimumInteractiveComponentSize().testTag("library_note_save_btn"),
        ) {
          Text(if (isFa) "ذخیره" else "Save")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showNewNoteDialog = false },
          modifier = Modifier.minimumInteractiveComponentSize(),
        ) {
          Text(if (isFa) "بستن" else "Cancel")
        }
      },
    )
  }
}
