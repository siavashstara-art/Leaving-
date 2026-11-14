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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.ManuscriptApprovalStatus
import com.example.ui.HonarZistanUiState
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.FriendlyEmptyState
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.LocalHonarColors
import java.util.Locale

@Composable
fun AudiobookListenerScreen(
  uiState: HonarZistanUiState,
  onSelectChapter: (String) -> Unit,
  onTogglePlayPause: (chapterId: String, segmentIndex: Int) -> Unit,
  onPlayFromPosition: (chapterId: String, segmentIndex: Int, positionMs: Long) -> Unit,
  onSeekTo: (Long) -> Unit,
  onSeekBy: (Long) -> Unit,
  onSpeedChange: (Float) -> Unit,
  onVolumeChange: (Float) -> Unit,
  onAddBookmark: (chapterId: String, label: String) -> Unit,
  onDeleteBookmark: (Long) -> Unit,
  onOpenStudioForChapter: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current

  val eligibleChapters =
    uiState.chapters.filter { it.approvalStatus == ManuscriptApprovalStatus.APPROVED }
  val activeChapter =
    eligibleChapters.find { it.id == uiState.selectedListenerChapterId }
      ?: eligibleChapters.first()
  val segments = uiState.segmentsByChapter[activeChapter.id].orEmpty()
  val player = uiState.audiobookPlayerState
  val isPlayingThisChapter =
    player.isPlaying && player.activeChapterId == activeChapter.id
  val currentSegIndex =
    if (player.activeChapterId == activeChapter.id) {
      player.activeSegmentIndex.coerceIn(0, (segments.size - 1).coerceAtLeast(0))
    } else 0
  val activeSegment = segments.getOrNull(currentSegIndex)
  val hasAnyAudioInChapter = segments.any { !it.audioFilePath.isNullOrBlank() }
  val savedProgress = uiState.listeningProgressMap[activeChapter.id]
  val chapterBookmarks = uiState.audioBookmarks.filter { it.chapterId == activeChapter.id }
  var showSynchronizedText by remember { mutableStateOf(true) }

  Column(modifier = modifier.fillMaxSize().testTag("audiobook_listener_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  کتاب صوتی  ←  فصل ${activeChapter.number.toLocalizedDigits(lang)}"
        else "Honar Zistan  →  Audiobook  →  Chapter ${activeChapter.number}"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("audiobook_listener_list"),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "کتاب صوتی هنر زیستن" else "The Art of Living — Audiobook",
            subtitle =
              if (isFa) "شنیدن فصل‌های تأییدشده همراه با متن هم‌زمان و نشانک‌گذاری"
              else "Listen to approved chapters with synchronized manuscript reading",
            action = {
              OutlinedButton(
                onClick = { onOpenStudioForChapter(activeChapter.id) },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("open_studio_from_listener_btn"),
              ) {
                Icon(imageVector = Icons.Outlined.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isFa) "استودیو" else "Studio")
              }
            },
          )
        }

        // 1. Chapter Selector List
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = if (isFa) "فهرست فصل‌های نسخه صوتی" else "Audiobook Chapter List",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              eligibleChapters.forEach { ch ->
                val prod = uiState.audioProductionMap[ch.id]
                val selected = ch.id == activeChapter.id
                Surface(
                  modifier =
                    Modifier.fillMaxWidth()
                      .minimumInteractiveComponentSize()
                      .clickable { onSelectChapter(ch.id) }
                      .testTag("listener_chapter_${ch.id}"),
                  shape = MaterialTheme.shapes.small,
                  color =
                    if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.background,
                  border =
                    BorderStroke(
                      width = if (selected) 1.dp else 0.5.dp,
                      color =
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                    ),
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Text(
                        text =
                          if (isFa) "فصل ${ch.number.toLocalizedDigits(lang)}: ${ch.titleFa}"
                          else "Ch. ${ch.number}: ${ch.titleEn}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                      )
                      Text(
                        text =
                          if (prod != null && prod.approvedVersion > 0) {
                            if (isFa) "تأیید v${prod.approvedVersion}" else "Approved v${prod.approvedVersion}"
                          } else {
                            if (isFa) "${prod?.readySegmentCount ?: 0} قطعه" else "${prod?.readySegmentCount ?: 0} seg"
                          },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                      )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = if (isFa) ch.excerptFa else ch.excerptEn,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      maxLines = 2,
                    )
                  }
                }
              }
            }
          }
        }

        // 2. Main Listener Player Card
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.5f)),
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Filled.GraphicEq,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (isFa) activeChapter.titleFa else activeChapter.titleEn,
                    style = MaterialTheme.typography.titleLarge,
                  )
                  Text(
                    text =
                      activeSegment?.let { if (isFa) it.titleFa else it.titleEn }
                        ?: if (isFa) "در حال آماده‌سازی قطعات" else "Preparing segments",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }

              if (!hasAnyAudioInChapter) {
                FriendlyEmptyState(
                  title =
                    if (isFa) "فایل صوتی این فصل هنوز تولید نشده است"
                    else "No audio recorded for this chapter yet",
                  message =
                    if (isFa) {
                      "برای ضبط صدای مؤلف، وارد کردن فایل استودیویی یا استفاده از گفتارخوان فارسی دستگاه، وارد «استودیوی گویندگی» شوید."
                    } else {
                      "Open Narration Studio to record Author Voice, import studio audio, or synthesize with device Persian TTS."
                    },
                )
              }

              // Resume from last listening position
              if (savedProgress != null && savedProgress.positionMs > 0L) {
                OutlinedButton(
                  onClick = {
                    onPlayFromPosition(
                      activeChapter.id,
                      savedProgress.lastSegmentIndex,
                      savedProgress.positionMs,
                    )
                  },
                  modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
                ) {
                  Text(
                    if (isFa) {
                      "ادامه از آخرین موقعیت (${formatDurationMs(savedProgress.positionMs)} — قطعه ${(savedProgress.lastSegmentIndex + 1).toLocalizedDigits(lang)})"
                    } else {
                      "Resume from ${formatDurationMs(savedProgress.positionMs)} (Segment ${savedProgress.lastSegmentIndex + 1})"
                    }
                  )
                }
              }

              // Seek Bar
              val durationMs = player.durationMs.takeIf { it > 0L && player.activeChapterId == activeChapter.id } ?: 1L
              val posMs = if (player.activeChapterId == activeChapter.id) player.positionMs else 0L
              Column {
                Slider(
                  value = posMs.coerceIn(0L, durationMs).toFloat(),
                  onValueChange = { onSeekTo(it.toLong()) },
                  valueRange = 0f..durationMs.toFloat(),
                  enabled = player.activeChapterId == activeChapter.id && player.durationMs > 0L,
                  modifier = Modifier.fillMaxWidth(),
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(
                    text = formatDurationMs(posMs),
                    style = MaterialTheme.typography.labelSmall,
                  )
                  Text(
                    text =
                      if (player.activeChapterId == activeChapter.id && player.durationMs > 0L) {
                        formatDurationMs(player.durationMs)
                      } else "00:00",
                    style = MaterialTheme.typography.labelSmall,
                  )
                }
              }

              // Primary Transport Controls
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                IconButton(
                  onClick = {
                    val prev = (currentSegIndex - 1).coerceAtLeast(0)
                    onPlayFromPosition(activeChapter.id, prev, 0L)
                  },
                  enabled = currentSegIndex > 0,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = if (isFa) "قطعه قبلی" else "Previous segment",
                  )
                }
                IconButton(
                  onClick = { onSeekBy(-10_000L) },
                  enabled = player.activeChapterId == activeChapter.id && player.durationMs > 0L,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Replay10,
                    contentDescription = if (isFa) "۱۰ ثانیه عقب" else "Seek back 10s",
                  )
                }
                Button(
                  onClick = { onTogglePlayPause(activeChapter.id, currentSegIndex) },
                  enabled = hasAnyAudioInChapter,
                  modifier = Modifier.minimumInteractiveComponentSize().testTag("listener_play_pause_btn"),
                ) {
                  Icon(
                    imageVector =
                      if (isPlayingThisChapter) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    when {
                      !hasAnyAudioInChapter -> if (isFa) "بدون فایل صوتی" else "No Audio"
                      isPlayingThisChapter -> if (isFa) "توقف" else "Pause"
                      else -> if (isFa) "پخش" else "Play"
                    }
                  )
                }
                IconButton(
                  onClick = { onSeekBy(10_000L) },
                  enabled = player.activeChapterId == activeChapter.id && player.durationMs > 0L,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Filled.Forward10,
                    contentDescription = if (isFa) "۱۰ ثانیه جلو" else "Seek forward 10s",
                  )
                }
                IconButton(
                  onClick = {
                    val next = (currentSegIndex + 1).coerceAtMost((segments.size - 1).coerceAtLeast(0))
                    onPlayFromPosition(activeChapter.id, next, 0L)
                  },
                  enabled = currentSegIndex < segments.lastIndex,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = if (isFa) "قطعه بعدی" else "Next segment",
                  )
                }
              }

              // Playback Speed & Volume
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = if (isFa) "سرعت:" else "Speed:",
                  style = MaterialTheme.typography.labelSmall,
                )
                listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { sp ->
                  FilterChip(
                    selected = player.playbackSpeed == sp,
                    onClick = { onSpeedChange(sp) },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    label = { Text("${sp}x") },
                  )
                }
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = if (isFa) "میزان صدا" else "Volume",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                  value = player.volume,
                  onValueChange = onVolumeChange,
                  valueRange = 0f..1f,
                  modifier = Modifier.weight(1f),
                )
              }

              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                OutlinedButton(
                  onClick = {
                    val label =
                      activeSegment?.let { if (isFa) it.titleFa else it.titleEn }
                        ?: if (isFa) "نشانک فصل ${activeChapter.number}" else "Ch. ${activeChapter.number} Bookmark"
                    onAddBookmark(activeChapter.id, label)
                  },
                  modifier = Modifier.minimumInteractiveComponentSize().testTag("add_audio_bookmark_btn"),
                ) {
                  Icon(imageVector = Icons.Outlined.BookmarkAdd, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "نشانک لحظه فعلی" else "Bookmark Timestamp")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = if (isFa) "متن هم‌زمان" else "Sync Text",
                    style = MaterialTheme.typography.labelLarge,
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Switch(
                    checked = showSynchronizedText,
                    onCheckedChange = { showSynchronizedText = it },
                  )
                }
              }
            }
          }
        }

        // 3. Saved Audio Bookmarks
        if (chapterBookmarks.isNotEmpty()) {
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.small,
              color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = if (isFa) "نشانک‌های صوتی این فصل" else "Audio Bookmarks",
                  style = MaterialTheme.typography.titleMedium,
                )
                chapterBookmarks.forEach { bm ->
                  Row(
                    modifier =
                      Modifier.fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable {
                          onPlayFromPosition(activeChapter.id, bm.segmentIndex, bm.positionMs)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Bookmark,
                      contentDescription = null,
                      tint = extra.goldAccent,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "${bm.label} (${formatDurationMs(bm.positionMs)})",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.weight(1f),
                    )
                    IconButton(
                      onClick = { onDeleteBookmark(bm.id) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = if (isFa) "حذف نشانک" else "Delete bookmark",
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // 4. Optional Synchronized Reading Text
        if (showSynchronizedText && segments.isNotEmpty()) {
          item {
            SectionHeaderRow(
              title = if (isFa) "متن هم‌زمان با گویندگی" else "Synchronized Reading Text",
              subtitle =
                if (isFa) "بند در حال پخش به صورت خودکار برجسته می‌شود"
                else "Tap any passage to inspect or listen",
            )
          }

          itemsIndexed(segments, key = { _, s -> s.segmentId }) { idx, seg ->
            val isActive =
              (player.activeChapterId == activeChapter.id && player.activeSegmentId == seg.segmentId) ||
                (player.activeChapterId != activeChapter.id && idx == 0)
            Surface(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable {
                    if (!seg.audioFilePath.isNullOrBlank()) {
                      onPlayFromPosition(activeChapter.id, idx, 0L)
                    }
                  },
              shape = MaterialTheme.shapes.small,
              color =
                if (isActive) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
              border =
                BorderStroke(
                  width = if (isActive) 1.25.dp else 0.5.dp,
                  color =
                    if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(
                    text = if (isFa) seg.titleFa else seg.titleEn,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                  )
                  Text(
                    text =
                      if (seg.durationMs > 0L) formatDurationMs(seg.durationMs)
                      else if (isFa) "بدون صوت" else "No audio",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Text(
                  text = if (isFa) seg.manuscriptTextFa else seg.manuscriptTextEn.ifBlank { seg.manuscriptTextFa },
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }
      }
    }
  }
}

fun formatDurationMs(ms: Long): String {
  val totalSec = (ms / 1000L).coerceAtLeast(0L)
  val min = totalSec / 60L
  val sec = totalSec % 60L
  return String.format(Locale.US, "%02d:%02d", min, sec)
}
