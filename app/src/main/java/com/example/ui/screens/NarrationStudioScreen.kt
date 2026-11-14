package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.model.AppLanguage
import com.example.model.AudioExportFormat
import com.example.model.Chapter
import com.example.model.ManuscriptApprovalStatus
import com.example.model.NarrationSegmentUi
import com.example.model.NarratorType
import com.example.model.VoiceCharacteristic
import com.example.ui.HonarZistanUiState
import com.example.ui.components.EditorialReadingColumn
import com.example.ui.components.LocationBreadcrumbBar
import com.example.ui.components.SectionHeaderRow
import com.example.ui.components.toLocalizedDigits
import com.example.ui.theme.LocalHonarColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NarrationStudioScreen(
  uiState: HonarZistanUiState,
  onSelectChapter: (String) -> Unit,
  onPrepareScript: (Chapter) -> Unit,
  onUpdateNarratorSelection: (String, NarratorType, VoiceCharacteristic, Boolean) -> Unit,
  onSelectExportFormat: (AudioExportFormat) -> Unit,
  onUpdateSegmentScript: (segmentId: String, scriptFa: String) -> Unit,
  onGenerateWithDeviceTts: (Chapter, NarrationSegmentUi) -> Unit,
  onStartRecording: (Chapter, NarrationSegmentUi) -> Unit,
  onStopRecording: (chapterId: String) -> Unit,
  onImportAudioForSegment: (chapterId: String, segmentId: String, uri: Uri) -> Unit,
  onTogglePlaySegment: (chapterId: String, segmentIndex: Int) -> Unit,
  onToggleQualityCheckBit: (chapterId: String, bitIndex: Int) -> Unit,
  onApproveChapterAudio: (Chapter) -> Unit,
  onExportPackageToTreeUri: (Uri) -> Unit,
  onClearFeedback: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val lang = uiState.settings.language
  val isFa = lang == AppLanguage.PERSIAN
  val extra = LocalHonarColors.current

  val selectedChapter =
    uiState.chapters.find { it.id == uiState.selectedStudioChapterId } ?: uiState.chapters.first()
  val isChapterApprovedForAudio =
    selectedChapter.approvalStatus == ManuscriptApprovalStatus.APPROVED
  val production = uiState.audioProductionMap[selectedChapter.id]
  val segments = uiState.segmentsByChapter[selectedChapter.id].orEmpty()
  val archives = uiState.versionArchivesByChapter[selectedChapter.id].orEmpty()
  val meter = uiState.recordingMeter
  val playerState = uiState.audiobookPlayerState

  var pendingImportSegmentId by remember { mutableStateOf<String?>(null) }
  var pendingRecordSegment by remember { mutableStateOf<NarrationSegmentUi?>(null) }
  var editingScriptSegment by remember { mutableStateOf<NarrationSegmentUi?>(null) }
  var scriptEditText by remember { mutableStateOf("") }
  var permissionDeniedNotice by remember { mutableStateOf(false) }

  val audioPermissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      val targetSeg = pendingRecordSegment
      pendingRecordSegment = null
      if (granted && targetSeg != null) {
        onStartRecording(selectedChapter, targetSeg)
      } else if (!granted) {
        permissionDeniedNotice = true
      }
    }

  val importAudioLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      val segId = pendingImportSegmentId
      pendingImportSegmentId = null
      if (uri != null && segId != null) {
        onImportAudioForSegment(selectedChapter.id, segId, uri)
      }
    }

  val exportTreeLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { treeUri ->
      if (treeUri != null) {
        onExportPackageToTreeUri(treeUri)
      }
    }

  Column(modifier = modifier.fillMaxSize().testTag("narration_studio_screen")) {
    LocationBreadcrumbBar(
      trail =
        if (isFa) "کتاب هنر زیستن  ←  استودیوی تولید کتاب صوتی  ←  فصل ${selectedChapter.number.toLocalizedDigits(lang)}"
        else "Honar Zistan  →  Narration Studio  →  Chapter ${selectedChapter.number}"
    )

    EditorialReadingColumn(modifier = Modifier.weight(1f)) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("narration_studio_list"),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        item {
          SectionHeaderRow(
            title = if (isFa) "استودیوی تولید کتاب صوتی" else "Persian Audiobook Studio",
            subtitle =
              if (isFa) "انتخاب گوینده، آماده‌سازی متن، ضبط یا واردکردن صدا، بازبینی کیفیت، تأیید و خروجی"
              else "Narrator selection, script sync, recording/import, quality review, approval & export",
          )
        }

        // Feedback Banner
        uiState.studioFeedback?.let { fb ->
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.small,
              color =
                if (fb.isError) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer,
              border =
                BorderStroke(
                  0.75.dp,
                  if (fb.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                ),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = if (isFa) fb.messageFa else fb.messageEn,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onClearFeedback) { Text(if (isFa) "بستن" else "Dismiss") }
              }
            }
          }
        }

        if (permissionDeniedNotice) {
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.small,
              color = MaterialTheme.colorScheme.errorContainer,
            ) {
              Text(
                text =
                  if (isFa) "دسترسی میکروفون رد شد؛ می‌توانید فایل ضبط‌شده را با گزینه «وارد کردن فایل» بارگذاری کنید."
                  else "Microphone permission was denied; you can still import pre-recorded audio files.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
              )
            }
          }
        }

        // 1. Chapter Selection (Shows Approved vs Unapproved/Incomplete)
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = if (isFa) "۱. انتخاب فصل یا دست‌نوشته:" else "1. Select Chapter:",
              style = MaterialTheme.typography.titleMedium,
            )
            LazyRow(
              modifier = Modifier.testTag("studio_chapter_row"),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              items(uiState.chapters, key = { it.id }) { ch ->
                val badge =
                  if (ch.approvalStatus == ManuscriptApprovalStatus.APPROVED) "" else " 🔒"
                FilterChip(
                  selected = ch.id == selectedChapter.id,
                  onClick = { onSelectChapter(ch.id) },
                  modifier =
                    Modifier.minimumInteractiveComponentSize().testTag("studio_chapter_chip_${ch.id}"),
                  label = {
                    Text(
                      if (isFa) "فصل ${ch.number.toLocalizedDigits(lang)}$badge"
                      else "Ch. ${ch.number}$badge"
                    )
                  },
                )
              }
            }
          }
        }

        // Unapproved / Incomplete Gate Banner
        if (!isChapterApprovedForAudio) {
          item {
            Surface(
              modifier = Modifier.fillMaxWidth().testTag("unapproved_chapter_lock_banner"),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.errorContainer,
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text =
                      if (isFa) "قفل امانت‌داری: ${selectedChapter.approvalStatus.titleFa}"
                      else "Locked: ${selectedChapter.approvalStatus.titleEn}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                  )
                }
                Text(
                  text =
                    if (isFa) {
                      "«${selectedChapter.titleFa}» هنوز به تایید نهایی مؤلف نرسیده یا ناقص است. مطابق اصول نشر کتاب، دست‌نوشته‌های تاییدنشده هرگز به صورت خودکار به کتاب صوتی نهایی تبدیل نمی‌شوند."
                    } else {
                      "\"${selectedChapter.titleEn}\" is incomplete or awaiting final author approval and cannot enter final audiobook production."
                    },
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onErrorContainer,
                )
              }
            }
          }
        } else {
          // 2. Narrator & Voice Characteristics Selection
          item {
            val currentNarrator = production?.narratorType ?: NarratorType.AUTHOR_VOICE
            val currentChar = production?.voiceCharacteristic ?: VoiceCharacteristic.WARM_FRIENDLY
            val authorAuth = production?.authorVoiceAuthorized ?: false

            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                  text = if (isFa) "۲. سیستم انتخاب گوینده و لحن" else "2. Narrator & Voice Characteristics",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.primary,
                )
                NarratorType.entries.forEach { nType ->
                  val sel = currentNarrator == nType
                  Surface(
                    modifier =
                      Modifier.fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable {
                          onUpdateNarratorSelection(selectedChapter.id, nType, currentChar, authorAuth)
                        }
                        .testTag("narrator_option_${nType.name}"),
                    shape = MaterialTheme.shapes.small,
                    color =
                      if (sel) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.background,
                    border =
                      BorderStroke(
                        if (sel) 1.dp else 0.5.dp,
                        if (sel) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                      ),
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text(
                        text = if (isFa) nType.titleFa else nType.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                      )
                      Text(
                        text = if (isFa) nType.descriptionFa else nType.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                      )
                    }
                  }
                }

                Text(
                  text = if (isFa) "ویژگی لحن گویندگی:" else "Voice Characteristic:",
                  style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  VoiceCharacteristic.entries.forEach { ch ->
                    FilterChip(
                      selected = currentChar == ch,
                      onClick = {
                        onUpdateNarratorSelection(selectedChapter.id, currentNarrator, ch, authorAuth)
                      },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                      label = { Text(if (isFa) ch.titleFa else ch.titleEn) },
                    )
                  }
                }

                if (currentNarrator == NarratorType.AUTHOR_VOICE) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Text(
                      text =
                        if (isFa) "تأیید مجوز ضبط صدای مؤلف (سیاوش علی‌میری)"
                        else "Authorize author's real voice recording",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.weight(1f),
                    )
                    Switch(
                      checked = authorAuth,
                      onCheckedChange = { checked ->
                        onUpdateNarratorSelection(
                          selectedChapter.id,
                          currentNarrator,
                          currentChar,
                          checked,
                        )
                      },
                      modifier = Modifier.testTag("author_voice_auth_switch"),
                    )
                  }
                }

                // Technology & Cost transparency badge
                Surface(
                  modifier = Modifier.fillMaxWidth(),
                  shape = MaterialTheme.shapes.small,
                  color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.CloudOff,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text =
                        if (isFa) {
                          val ttsStatus =
                            if (uiState.devicePersianTtsAvailable) "موتور گفتار فارسی دستگاه آماده است."
                            else "موتور گفتار فارسی روی این دستگاه نصب نیست."
                          "$ttsStatus برای سرویس ابری تجاری (Azure fa-IR / Gemini-TTS) سرور واسط امن نیاز است و کلید API در اپ قرار نمی‌گیرد."
                        } else {
                          val ttsStatus =
                            if (uiState.devicePersianTtsAvailable) "Device Persian TTS ready."
                            else "Device Persian TTS unavailable."
                          "$ttsStatus External cloud TTS requires a secure proxy; API keys are never stored in client code."
                        },
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                }
              }
            }
          }

          // 3. Prepare Script & Live Mic Meter
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text =
                        if (isFa) "۳. قطعات متن و وضعیت هم‌زمانی"
                        else "3. Narration Segments & Sync",
                      style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                      text =
                        if (isFa) {
                          "وضعیت: ${production?.workflowStatus?.titleFa ?: ""} • آماده: ${production?.readySegmentCount ?: 0}/${segments.size}"
                        } else {
                          "Status: ${production?.workflowStatus?.titleEn ?: ""} • Ready: ${production?.readySegmentCount ?: 0}/${segments.size}"
                        },
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                    )
                  }
                  OutlinedButton(
                    onClick = { onPrepareScript(selectedChapter) },
                    modifier = Modifier.minimumInteractiveComponentSize().testTag("prepare_script_btn"),
                  ) {
                    Text(if (isFa) "آماده‌سازی متن" else "Prepare Script")
                  }
                }

                if (meter.isRecording) {
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer,
                  ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                      ) {
                        Text(
                          text =
                            if (isFa) "در حال ضبط میکروفون: ${formatDurationMs(meter.elapsedMs)}"
                            else "Recording: ${formatDurationMs(meter.elapsedMs)}",
                          style = MaterialTheme.typography.titleMedium,
                        )
                        Button(onClick = { onStopRecording(selectedChapter.id) }) {
                          Icon(imageVector = Icons.Filled.Stop, contentDescription = null)
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(if (isFa) "توقف و ذخیره" else "Stop & Save")
                        }
                      }
                      LinearProgressIndicator(
                        progress = { meter.normalizedLevel },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                      )
                      if (meter.clippingDetected) {
                        Text(
                          text =
                            if (isFa) "هشدار: اوج صدا بیش از حد بالا است (Clipping)!"
                            else "Warning: Audio clipping detected!",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.error,
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // 4. Segment List Cards
          itemsIndexed(segments, key = { _, seg -> seg.segmentId }) { idx, seg ->
            val isPlayingThisSeg =
              playerState.isPlaying && playerState.activeSegmentId == seg.segmentId
            val hasFile = !seg.audioFilePath.isNullOrBlank()
            Surface(
              modifier = Modifier.fillMaxWidth().testTag("studio_segment_${seg.segmentId}"),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surface,
              border =
                BorderStroke(
                  width = if (seg.isOutdated || seg.isClippingWarning) 1.dp else 0.75.dp,
                  color =
                    when {
                      seg.isOutdated -> MaterialTheme.colorScheme.error
                      seg.isClippingWarning -> extra.goldAccent
                      hasFile -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                      else -> MaterialTheme.colorScheme.outlineVariant
                    },
                ),
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = if (isFa) "${(idx + 1).toLocalizedDigits(lang)}. ${seg.titleFa}" else "${idx + 1}. ${seg.titleEn}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                  )
                  Text(
                    text =
                      when {
                        seg.isOutdated -> if (isFa) "نیازمند بازضبط (متن تغییر کرده)" else "Outdated"
                        hasFile ->
                          "${seg.audioFormat?.name ?: "AUDIO"} • ${formatDurationMs(seg.durationMs)}"
                        else -> if (isFa) "بدون فایل صوتی" else "Not narrated"
                      },
                    style = MaterialTheme.typography.labelSmall,
                    color =
                      if (seg.isOutdated) MaterialTheme.colorScheme.error
                      else MaterialTheme.colorScheme.primary,
                  )
                }

                Text(
                  text = seg.narrationScriptFa,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (seg.isClippingWarning) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Filled.Warning,
                      contentDescription = null,
                      tint = extra.goldAccent,
                      modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text =
                        if (isFa) "اوج صدا (Clipping) در این ضبط ثبت شده است."
                        else "Peak clipping detected in this recording.",
                      style = MaterialTheme.typography.labelSmall,
                      color = extra.goldAccent,
                    )
                  }
                }

                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  OutlinedButton(
                    onClick = {
                      editingScriptSegment = seg
                      scriptEditText = seg.narrationScriptFa
                    },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isFa) "اصلاح تلفظ/متن" else "Edit Script")
                  }

                  Button(
                    onClick = {
                      if (meter.isRecording && meter.activeSegmentId == seg.segmentId) {
                        onStopRecording(selectedChapter.id)
                      } else {
                        val granted =
                          ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO,
                          ) == PackageManager.PERMISSION_GRANTED
                        if (granted) {
                          onStartRecording(selectedChapter, seg)
                        } else {
                          pendingRecordSegment = seg
                          audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                      }
                    },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(
                      imageVector =
                        if (meter.isRecording && meter.activeSegmentId == seg.segmentId) Icons.Filled.Stop
                        else Icons.Filled.Mic,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      if (meter.isRecording && meter.activeSegmentId == seg.segmentId) {
                        if (isFa) "توقف" else "Stop"
                      } else {
                        if (isFa) "ضبط" else "Record"
                      }
                    )
                  }

                  OutlinedButton(
                    onClick = {
                      pendingImportSegmentId = seg.segmentId
                      importAudioLauncher.launch(arrayOf("audio/*"))
                    },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(imageVector = Icons.Outlined.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isFa) "واردکردن فایل" else "Import")
                  }

                  OutlinedButton(
                    onClick = { onGenerateWithDeviceTts(selectedChapter, seg) },
                    enabled = uiState.devicePersianTtsAvailable,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Text(
                      if (uiState.devicePersianTtsAvailable) {
                        if (isFa) "تولید AI دستگاه" else "Device TTS"
                      } else {
                        if (isFa) "TTS فارسی ناموجود" else "No fa-IR TTS"
                      }
                    )
                  }

                  if (hasFile) {
                    OutlinedButton(
                      onClick = { onTogglePlaySegment(selectedChapter.id, idx) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                      Icon(
                        imageVector =
                          if (isPlayingThisSeg) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        if (isPlayingThisSeg) {
                          if (isFa) "توقف" else "Pause"
                        } else {
                          if (isFa) "بازبینی پخش" else "Review"
                        }
                      )
                    }
                  }
                }
              }
            }
          }

          // 5. Quality Review Checklist & Final Approval + Version History
          item {
            val checklist = production?.qualityChecklist
            val qualityItems =
              listOf(
                0 to (if (isFa) "دقت تلفظ واژگان و نام‌های فارسی" else "Persian vocabulary & name pronunciation accuracy"),
                1 to (if (isFa) "وضوح کلام و فهم‌پذیری کامل" else "Speech clarity & intelligibility"),
                2 to (if (isFa) "نبود نویز مزاحم پس‌زمینه" else "No distracting background noise"),
                3 to (if (isFa) "یکنواختی بلندی صدا در قطعات فصل" else "Volume consistency across segments"),
                4 to (if (isFa) "مکث‌های طبیعی و پایان درست جمله‌ها" else "Natural pauses & proper sentence endings"),
                5 to (if (isFa) "نبود افتادگی یا تکرار جمله" else "No missing or repeated sentences"),
                6 to (if (isFa) "سلامت موج صدا (بدون Clipping و اعوجاج)" else "No clipping or audio distortion"),
              )

            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(1.dp, extra.goldAccent.copy(alpha = 0.5f)),
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = if (isFa) "۴. ارزیابی کیفیت و تأیید نهایی فصل صوتی" else "4. Audio Quality Review & Approval",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.primary,
                )
                qualityItems.forEach { (bitIdx, label) ->
                  val checked =
                    when (bitIdx) {
                      0 -> checklist?.pronunciationChecked == true
                      1 -> checklist?.clarityChecked == true
                      2 -> checklist?.noiseChecked == true
                      3 -> checklist?.volumeConsistencyChecked == true
                      4 -> checklist?.pausesAndEndingsChecked == true
                      5 -> checklist?.completenessChecked == true
                      else -> checklist?.noClippingChecked == true
                    }
                  Row(
                    modifier =
                      Modifier.fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable { onToggleQualityCheckBit(selectedChapter.id, bitIdx) },
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Checkbox(
                      checked = checked,
                      onCheckedChange = { onToggleQualityCheckBit(selectedChapter.id, bitIdx) },
                    )
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                  }
                }

                Button(
                  onClick = { onApproveChapterAudio(selectedChapter) },
                  modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize().testTag("approve_chapter_audio_btn"),
                ) {
                  Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (isFa) "تأیید و بایگانی نسخه نهایی صوتی فصل" else "Approve & Archive Chapter Audio")
                }

                if (archives.isNotEmpty()) {
                  HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                  Text(
                    text = if (isFa) "نسخه‌های تأییدشده قبلی (محفوظ):" else "Preserved Approved Versions:",
                    style = MaterialTheme.typography.labelLarge,
                  )
                  archives.forEach { ver ->
                    Text(
                      text = "v${ver.versionNumber} • ${ver.formatLabel} • ${ver.narratorSummary}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                }
              }
            }
          }

          // 6. Export Formats & Organized Package Export (ART_OF_LIVING_AUDIO/)
          item {
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surfaceVariant,
              border = BorderStroke(0.75.dp, MaterialTheme.colorScheme.outline),
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = if (isFa) "۵. خروجی فایل‌ها (ART_OF_LIVING_AUDIO/)" else "5. Audio Export Package",
                  style = MaterialTheme.typography.titleMedium,
                )
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  AudioExportFormat.entries.forEach { fmt ->
                    FilterChip(
                      selected = uiState.selectedExportFormat == fmt,
                      onClick = { onSelectExportFormat(fmt) },
                      modifier = Modifier.minimumInteractiveComponentSize(),
                      label = { Text(if (isFa) fmt.labelFa else fmt.labelEn) },
                    )
                  }
                }
                if (!uiState.selectedExportFormat.nativeEncodeSupported) {
                  Text(
                    text =
                      if (isFa) "توجه: اندروید کدگذار بومی MP3 ندارد؛ در خروجی MP3 تنها فایل‌های MP3 واردشده منتقل می‌شوند. برای خروجی بومی فصل‌ها و نسخه کامل، M4A یا WAV را انتخاب کنید."
                      else "Note: Android lacks a built-in MP3 encoder; MP3 export transfers imported MP3s only. Use M4A or WAV for native chapter & combined encoding.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
                Button(
                  onClick = { exportTreeLauncher.launch(null) },
                  modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize().testTag("export_audiobook_btn"),
                ) {
                  Icon(imageVector = Icons.Outlined.FolderZip, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    if (isFa) "خروجی پوشه ART_OF_LIVING_AUDIO/ (CHAPTER_001 + AUDIOBOOK_COMPLETE)"
                    else "Export ART_OF_LIVING_AUDIO/ Folder"
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  editingScriptSegment?.let { target ->
    AlertDialog(
      onDismissRequest = { editingScriptSegment = null },
      title = {
        Text(
          if (isFa) "ویرایش متن گویندگی و تلفظ" else "Edit Narration Script",
          style = MaterialTheme.typography.titleLarge,
        )
      },
      text = {
        OutlinedTextField(
          value = scriptEditText,
          onValueChange = { scriptEditText = it },
          minLines = 4,
          label = {
            Text(if (isFa) "اعراب‌گذاری، مکث (،) یا تصحیح واژگان" else "Narration text & pauses")
          },
          modifier = Modifier.fillMaxWidth(),
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateSegmentScript(target.segmentId, scriptEditText)
            editingScriptSegment = null
          },
          modifier = Modifier.minimumInteractiveComponentSize(),
        ) {
          Text(if (isFa) "ذخیره و بررسی هم‌زمانی" else "Save & Sync")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { editingScriptSegment = null },
          modifier = Modifier.minimumInteractiveComponentSize(),
        ) {
          Text(if (isFa) "انصراف" else "Cancel")
        }
      },
    )
  }
}
