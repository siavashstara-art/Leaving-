package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookCatalog
import com.example.data.BookRepository
import com.example.data.GalleryArtwork
import com.example.data.local.AudioBookmarkEntity
import com.example.data.local.AudioChapterConfigEntity
import com.example.data.local.AudioSegmentEntity
import com.example.data.local.AudioVersionArchiveEntity
import com.example.data.local.BookDatabase
import com.example.data.local.ListeningProgressEntity
import com.example.data.local.NoteEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.ReadingProgressEntity
import com.example.media.AudioEncoderExporter
import com.example.media.AudioPlaybackState
import com.example.media.AudiobookPlayerEngine
import com.example.media.AudiobookPlayerState
import com.example.media.AudiobookScriptEngine
import com.example.media.AuthorVoiceRecorder
import com.example.media.ChapterAudioPlayer
import com.example.media.DevicePersianTtsNarrator
import com.example.media.NarrationGenerationResult
import com.example.media.RecordingMeterState
import com.example.model.AccessibilitySettings
import com.example.model.AppLanguage
import com.example.model.AudioBookmarkUi
import com.example.model.AudioExportFormat
import com.example.model.AudioVersionSnapshot
import com.example.model.BookPart
import com.example.model.Chapter
import com.example.model.ChapterAudioProductionUi
import com.example.model.ContentCategory
import com.example.model.ListeningProgressUi
import com.example.model.ManuscriptApprovalStatus
import com.example.model.NarrationSegmentUi
import com.example.model.NarratorType
import com.example.model.ReaderBlock
import com.example.model.ThemeMode
import com.example.model.VoiceCharacteristic
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AudiobookFeedbackBanner(
  val messageFa: String,
  val messageEn: String,
  val isError: Boolean = false,
)

data class HonarZistanUiState(
  val parts: List<BookPart> = BookCatalog.parts,
  val chapters: List<Chapter> = BookCatalog.chapters,
  val galleryItems: List<GalleryArtwork> = emptyList(),
  val selectedCategory: ContentCategory? = null,
  val searchQuery: String = "",
  val bookmarkedChapterIds: Set<String> = emptySet(),
  val notes: List<NoteEntity> = emptyList(),
  val progressMap: Map<String, ReadingProgressEntity> = emptyMap(),
  val lastReadChapterId: String = "ch_01",
  val settings: AccessibilitySettings = AccessibilitySettings(),
  val audioState: AudioPlaybackState = AudioPlaybackState(),
  val focusModeActiveInReader: Boolean = false,
  // Audiobook & Narration System state
  val selectedStudioChapterId: String = "ch_01",
  val selectedListenerChapterId: String = "ch_01",
  val selectedExportFormat: AudioExportFormat = AudioExportFormat.M4A,
  val audioProductionMap: Map<String, ChapterAudioProductionUi> = emptyMap(),
  val segmentsByChapter: Map<String, List<NarrationSegmentUi>> = emptyMap(),
  val versionArchivesByChapter: Map<String, List<AudioVersionSnapshot>> = emptyMap(),
  val listeningProgressMap: Map<String, ListeningProgressUi> = emptyMap(),
  val audioBookmarks: List<AudioBookmarkUi> = emptyList(),
  val audiobookPlayerState: AudiobookPlayerState = AudiobookPlayerState(),
  val recordingMeter: RecordingMeterState = RecordingMeterState(),
  val devicePersianTtsAvailable: Boolean = false,
  val studioFeedback: AudiobookFeedbackBanner? = null,
) {
  val completedCount: Int
    get() = progressMap.values.count { it.isCompleted }

  val filteredChapters: List<Chapter>
    get() =
      if (selectedCategory == null) chapters
      else chapters.filter { it.category == selectedCategory }

  fun partFor(chapter: Chapter): BookPart? = parts.find { it.id == chapter.partId }

  val searchResults: List<Chapter>
    get() {
      val q = searchQuery.trim().lowercase()
      if (q.isEmpty()) return chapters
      return chapters.filter { chapter ->
        chapter.titleFa.lowercase().contains(q) ||
          chapter.titleEn.lowercase().contains(q) ||
          chapter.openingEpigraphFa.lowercase().contains(q) ||
          chapter.excerptFa.lowercase().contains(q) ||
          chapter.excerptEn.lowercase().contains(q) ||
          chapter.sections.any { sec ->
            sec.titleFa.lowercase().contains(q) || sec.titleEn.lowercase().contains(q)
          } ||
          chapter.blocks.any { block ->
            when (block) {
              is ReaderBlock.Prose ->
                block.textFa.lowercase().contains(q) || block.textEn.lowercase().contains(q)
              is ReaderBlock.Poetry ->
                block.translationEn.lowercase().contains(q) ||
                  block.coupletsFa.any {
                    it.first.lowercase().contains(q) || it.second.lowercase().contains(q)
                  }
              is ReaderBlock.QuranicVerse ->
                block.arabicText.lowercase().contains(q) ||
                  block.translationFa.lowercase().contains(q) ||
                  block.translationEn.lowercase().contains(q) ||
                  block.explanationFa.lowercase().contains(q) ||
                  block.reflectionFa.lowercase().contains(q)
              is ReaderBlock.Illustration ->
                block.captionFa.lowercase().contains(q) || block.captionEn.lowercase().contains(q)
            }
          }
      }
    }
}

private data class StudioUiControlBundle(
  val selectedStudioChapterId: String = "ch_01",
  val selectedListenerChapterId: String = "ch_01",
  val selectedExportFormat: AudioExportFormat = AudioExportFormat.M4A,
  val feedback: AudiobookFeedbackBanner? = null,
)

private data class AudiobookDbBundle(
  val configs: List<AudioChapterConfigEntity>,
  val segments: List<AudioSegmentEntity>,
  val archives: List<AudioVersionArchiveEntity>,
  val listening: List<ListeningProgressEntity>,
  val bookmarks: List<AudioBookmarkEntity>,
)

class HonarZistanViewModel(application: Application) : AndroidViewModel(application) {
  private val repository =
    BookRepository(
      dao = BookDatabase.getInstance(application).bookUserDao(),
      prefs = PreferencesRepository(application),
    )

  private val audioPlayer = ChapterAudioPlayer(application)
  private val devicePersianTts = DevicePersianTtsNarrator(application)
  private val voiceRecorder = AuthorVoiceRecorder()

  private val audiobookPlayer =
    AudiobookPlayerEngine(viewModelScope) { chapterId, segId, segIdx, posMs ->
      viewModelScope.launch {
        repository.saveListeningProgress(
          chapterId = chapterId,
          segmentId = segId,
          segmentIndex = segIdx,
          positionMs = posMs,
          speed = currentPlaybackSpeed,
        )
      }
    }

  private var currentPlaybackSpeed = 1.0f

  private val selectedCategoryFlow = MutableStateFlow<ContentCategory?>(null)
  private val searchQueryFlow = MutableStateFlow("")
  private val focusModeReaderFlow = MutableStateFlow(false)
  private val studioControlFlow = MutableStateFlow(StudioUiControlBundle())

  private val filterBundle =
    combine(selectedCategoryFlow, searchQueryFlow, focusModeReaderFlow) { cat, q, focus ->
      Triple(cat, q, focus)
    }

  private val audiobookDbFlow =
    combine(
      repository.audioConfigs,
      repository.audioSegments,
      repository.audioVersionArchives,
      repository.listeningProgress,
      repository.audioBookmarks,
    ) { configs, segments, archives, listening, bookmarks ->
      AudiobookDbBundle(configs, segments, archives, listening, bookmarks)
    }

  private val audioEngineBundle =
    combine(
      audiobookPlayer.state,
      voiceRecorder.meter,
      studioControlFlow,
    ) { playerState, meterState, controls ->
      Triple(playerState, meterState, controls)
    }

  val uiState: StateFlow<HonarZistanUiState> =
    combine(
        repository.settings,
        repository.bookmarks,
        repository.notes,
        repository.progress,
        combine(
          combine(audioPlayer.state, filterBundle) { audio, filter -> Pair(audio, filter) },
          combine(audiobookDbFlow, audioEngineBundle) { db, eng -> Pair(db, eng) },
        ) { legacy, ab -> Pair(legacy, ab) },
      ) { settings, bookmarks, notes, progressList, combined ->
        val (legacy, abPair) = combined
        val (audioState, filter) = legacy
        val (selectedCategory, searchQuery, focusActive) = filter
        val (dbBundle, engineBundle) = abPair
        val (playerState, meterState, controls) = engineBundle

        val progressMap = progressList.associateBy { it.chapterId }
        val lastChapter = progressList.firstOrNull()?.chapterId ?: "ch_01"

        val configByChapter = dbBundle.configs.associateBy { it.chapterId }
        val segmentsGrouped = dbBundle.segments.groupBy { it.chapterId }
        val productionMap =
          repository.chapters.associate { ch ->
            ch.id to
              BookRepository.mapProductionState(
                chapterId = ch.id,
                config = configByChapter[ch.id],
                segments = segmentsGrouped[ch.id].orEmpty(),
              )
          }

        val segmentUiMap =
          repository.chapters.associate { ch ->
            ch.id to
              segmentsGrouped[ch.id].orEmpty().map { ent -> BookRepository.mapSegmentUi(ch, ent) }
          }

        val archivesMap =
          dbBundle.archives
            .map { BookRepository.mapArchiveUi(it) }
            .groupBy { it.chapterId }

        val listeningMap =
          dbBundle.listening
            .map { BookRepository.mapListeningProgress(it) }
            .associateBy { it.chapterId }

        HonarZistanUiState(
          parts = repository.parts,
          chapters = repository.chapters,
          galleryItems = repository.galleryItems,
          selectedCategory = selectedCategory,
          searchQuery = searchQuery,
          bookmarkedChapterIds = bookmarks.map { it.chapterId }.toSet(),
          notes = notes,
          progressMap = progressMap,
          lastReadChapterId = lastChapter,
          settings = settings,
          audioState = audioState,
          focusModeActiveInReader = focusActive || settings.focusModeDefault,
          selectedStudioChapterId = controls.selectedStudioChapterId,
          selectedListenerChapterId = controls.selectedListenerChapterId,
          selectedExportFormat = controls.selectedExportFormat,
          audioProductionMap = productionMap,
          segmentsByChapter = segmentUiMap,
          versionArchivesByChapter = archivesMap,
          listeningProgressMap = listeningMap,
          audioBookmarks = dbBundle.bookmarks.map { BookRepository.mapAudioBookmark(it) },
          audiobookPlayerState = playerState,
          recordingMeter = meterState,
          devicePersianTtsAvailable = devicePersianTts.isPersianAvailable(),
          studioFeedback = controls.feedback,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue =
          HonarZistanUiState(
            parts = repository.parts,
            chapters = repository.chapters,
            galleryItems = repository.galleryItems,
          ),
      )

  fun selectCategory(category: ContentCategory?) {
    selectedCategoryFlow.value = category
  }

  fun updateSearchQuery(query: String) {
    searchQueryFlow.value = query
  }

  fun openChapter(chapterId: String) {
    val alreadyDone = uiState.value.progressMap[chapterId]?.isCompleted == true
    viewModelScope.launch { repository.recordChapterOpened(chapterId, alreadyDone) }
  }

  fun toggleChapterCompleted(chapterId: String) {
    val currentlyDone = uiState.value.progressMap[chapterId]?.isCompleted == true
    viewModelScope.launch { repository.markChapterRead(chapterId, !currentlyDone) }
  }

  fun toggleBookmark(chapterId: String) {
    val isBookmarked = uiState.value.bookmarkedChapterIds.contains(chapterId)
    viewModelScope.launch { repository.toggleBookmark(chapterId, isBookmarked) }
  }

  fun saveNote(chapterId: String, content: String, existingId: Long = 0L) {
    viewModelScope.launch { repository.saveNote(chapterId, content, existingId) }
  }

  fun deleteNote(noteId: Long) {
    viewModelScope.launch { repository.deleteNote(noteId) }
  }

  fun toggleFocusModeInReader() {
    focusModeReaderFlow.value = !uiState.value.focusModeActiveInReader
  }

  fun setThemeMode(mode: ThemeMode) {
    viewModelScope.launch { repository.setThemeMode(mode) }
  }

  fun setFontScale(scale: Float) {
    viewModelScope.launch { repository.setFontScale(scale) }
  }

  fun setLargeText(enabled: Boolean) {
    viewModelScope.launch { repository.setLargeText(enabled) }
  }

  fun setHighContrast(enabled: Boolean) {
    viewModelScope.launch { repository.setHighContrast(enabled) }
  }

  fun setReducedMotion(enabled: Boolean) {
    viewModelScope.launch { repository.setReducedMotion(enabled) }
  }

  fun setFocusModeDefault(enabled: Boolean) {
    focusModeReaderFlow.value = enabled
    viewModelScope.launch { repository.setFocusModeDefault(enabled) }
  }

  fun setLanguage(language: AppLanguage) {
    audioPlayer.refreshAvailability(language)
    viewModelScope.launch { repository.setLanguage(language) }
  }

  fun getTranscript(chapter: Chapter): String =
    repository.buildTranscript(chapter, uiState.value.settings.language)

  fun toggleAudioNarration(chapter: Chapter) {
    val lang = uiState.value.settings.language
    audioPlayer.togglePlay(chapter.id, repository.buildTranscript(chapter, lang), lang)
  }

  fun setAudioSpeed(speed: Float) = audioPlayer.setSpeed(speed)

  fun stopAudio() = audioPlayer.stop()

  // --- Audiobook Studio & Listener Controls ---

  fun selectStudioChapter(chapterId: String) {
    studioControlFlow.update { it.copy(selectedStudioChapterId = chapterId, feedback = null) }
    val chapter = repository.chapters.find { it.id == chapterId }
    if (chapter != null && chapter.approvalStatus == ManuscriptApprovalStatus.APPROVED) {
      viewModelScope.launch { repository.prepareChapterScriptSegments(chapter) }
    }
  }

  fun selectListenerChapter(chapterId: String) {
    studioControlFlow.update { it.copy(selectedListenerChapterId = chapterId) }
    val chapter = repository.chapters.find { it.id == chapterId }
    if (chapter != null && chapter.approvalStatus == ManuscriptApprovalStatus.APPROVED) {
      viewModelScope.launch { repository.prepareChapterScriptSegments(chapter) }
    }
  }

  fun prepareChapterScript(chapter: Chapter) {
    if (chapter.approvalStatus != ManuscriptApprovalStatus.APPROVED) {
      postFeedback(
        "فصل‌های پیش‌نویس یا ناتمام برای تولید کتاب صوتی نهایی قفل هستند.",
        "Unapproved or incomplete manuscripts cannot be converted into final audiobook chapters.",
        isError = true,
      )
      return
    }
    viewModelScope.launch {
      repository.prepareChapterScriptSegments(chapter)
      postFeedback(
        "متن گویندگی فصل آماده و به قطعات استاندارد تقسیم شد.",
        "Narration script normalized and divided into manageable segments.",
      )
    }
  }

  fun updateNarratorSelection(
    chapterId: String,
    narratorType: NarratorType,
    voiceCharacteristic: VoiceCharacteristic,
    authorVoiceAuthorized: Boolean,
  ) {
    viewModelScope.launch {
      repository.updateNarratorConfig(
        chapterId = chapterId,
        narratorType = narratorType,
        voiceCharacteristic = voiceCharacteristic,
        authorVoiceAuthorized = authorVoiceAuthorized,
      )
    }
  }

  fun selectExportFormat(format: AudioExportFormat) {
    studioControlFlow.update { it.copy(selectedExportFormat = format) }
  }

  fun updateSegmentScript(segmentId: String, scriptTextFa: String) {
    viewModelScope.launch {
      repository.updateSegmentScript(segmentId, scriptTextFa)
      postFeedback(
        "متن گویندگی ذخیره شد؛ در صورت تغییر پس از ضبط، قطعه به عنوان نیازمند بروزرسانی علامت‌گذاری می‌شود.",
        "Segment script updated; previously recorded segments are flagged outdated when text diverges.",
      )
    }
  }

  fun generateSegmentWithDeviceAi(chapter: Chapter, segment: NarrationSegmentUi) {
    if (chapter.approvalStatus != ManuscriptApprovalStatus.APPROVED) {
      postFeedback(
        "امکان تولید صوت برای فصل تأییدنشده وجود ندارد.",
        "Cannot narrate unapproved manuscript.",
        isError = true,
      )
      return
    }
    viewModelScope.launch {
      val outDir = File(getApplication<Application>().filesDir, "audiobook/${chapter.id}")
      when (
        val res =
          devicePersianTts.synthesizeSegment(
            segmentId = segment.segmentId,
            scriptTextFa = segment.narrationScriptFa,
            outputDir = outDir,
            preferredFormat = uiState.value.selectedExportFormat,
          )
      ) {
        is NarrationGenerationResult.Success -> {
          repository.attachGeneratedSegmentAudio(segment.segmentId, NarratorType.AI_PERSIAN, res)
          postFeedback(
            "فایل صوتی قطعه از موتور گفتار فارسی تولید شد.",
            "Segment audio synthesized via device Persian TTS.",
          )
        }
        is NarrationGenerationResult.Unavailable -> {
          postFeedback(res.reasonFa, res.reasonEn, isError = true)
        }
      }
    }
  }

  fun startRecordingSegment(chapter: Chapter, segment: NarrationSegmentUi) {
    if (chapter.approvalStatus != ManuscriptApprovalStatus.APPROVED) {
      postFeedback(
        "امکان ضبط نهایی برای فصل تأییدنشده وجود ندارد.",
        "Cannot record final audiobook audio for an unapproved chapter.",
        isError = true,
      )
      return
    }
    val prod = uiState.value.audioProductionMap[chapter.id]
    if (prod?.narratorType == NarratorType.AUTHOR_VOICE && !prod.authorVoiceAuthorized) {
      postFeedback(
        "لطفاً ابتدا مجوز استفاده از صدای مؤلف را در بخش انتخاب گوینده تأیید کنید.",
        "Please confirm author voice authorization before recording.",
        isError = true,
      )
      return
    }
    val outDir = File(getApplication<Application>().filesDir, "audiobook/${chapter.id}")
    val started = voiceRecorder.startRecording(segment.segmentId, outDir, viewModelScope)
    if (!started) {
      postFeedback(
        voiceRecorder.meter.value.errorMessageFa ?: "شروع ضبط میکروفون ناموفق بود.",
        "Could not start microphone recording.",
        isError = true,
      )
    }
  }

  fun stopRecordingSegment(chapterId: String) {
    viewModelScope.launch {
      val narrator =
        uiState.value.audioProductionMap[chapterId]?.narratorType ?: NarratorType.AUTHOR_VOICE
      when (
        val res = voiceRecorder.stopRecordingAndFinalize(uiState.value.selectedExportFormat)
      ) {
        is NarrationGenerationResult.Success -> {
          val activeSegId = uiState.value.recordingMeter.activeSegmentId ?: return@launch
          repository.attachGeneratedSegmentAudio(activeSegId, narrator, res)
          val msgFa =
            if (res.clippingDetected) {
              "ضبط ذخیره شد، اما اوج صدا بالا (Clipping) تشخیص داده شد؛ بازضبط با فاصله بیشتر از میکروفون توصیه می‌شود."
            } else {
              "ضبط قطعه با کیفیت مناسب ذخیره شد."
            }
          val msgEn =
            if (res.clippingDetected) {
              "Recording saved with peak clipping warning; consider re-recording slightly further from mic."
            } else {
              "Segment recording saved cleanly."
            }
          postFeedback(msgFa, msgEn, isError = res.clippingDetected)
        }
        is NarrationGenerationResult.Unavailable -> {
          postFeedback(res.reasonFa, res.reasonEn, isError = true)
        }
        null -> Unit
      }
    }
  }

  fun importAudioForSegment(
    chapterId: String,
    segmentId: String,
    uri: Uri,
  ) {
    viewModelScope.launch {
      val context = getApplication<Application>()
      val targetDir = File(context.filesDir, "audiobook/$chapterId")
      val imported =
        withContext(Dispatchers.IO) {
          AudioEncoderExporter.importUriToAppAudioDir(
            context = context,
            sourceUri = uri,
            targetDir = targetDir,
            targetBaseName = "${segmentId}_imp",
          )
        }
      if (imported == null) {
        postFeedback(
          "وارد کردن فایل صوتی ناموفق بود.",
          "Failed to import selected audio file.",
          isError = true,
        )
      } else {
        val narrator =
          uiState.value.audioProductionMap[chapterId]?.narratorType ?: NarratorType.HUMAN_NARRATOR
        repository.attachImportedSegmentAudio(segmentId, narrator, imported)
        postFeedback(
          "فایل صوتی (${imported.extension.uppercase()}) با موفقیت به قطعه متصل شد.",
          "Audio file (${imported.extension.uppercase()}) imported for this segment.",
        )
      }
    }
  }

  fun toggleQualityCheckBit(chapterId: String, bitIndex: Int) {
    viewModelScope.launch { repository.toggleQualityChecklistBit(chapterId, bitIndex) }
  }

  fun approveChapterAudio(chapter: Chapter) {
    viewModelScope.launch {
      val (ok, msgFa) =
        withContext(Dispatchers.IO) {
          repository.approveChapterAudio(chapter, getApplication<Application>().filesDir)
        }
      postFeedback(msgFa, msgFa, isError = !ok)
    }
  }

  fun exportAudiobookPackage(treeUri: Uri) {
    viewModelScope.launch {
      val context = getApplication<Application>()
      val format = uiState.value.selectedExportFormat
      val exportDir = File(context.filesDir, "audiobook/export_staging")
      exportDir.mkdirs()

      val exportResult =
        withContext(Dispatchers.IO) {
          val chapterFiles = mutableListOf<Pair<String, File>>()
          val m4aToCombine = mutableListOf<File>()
          val wavToCombine = mutableListOf<File>()
          var mp3NoteNeeded = false

          repository.chapters
            .filter { it.approvalStatus == ManuscriptApprovalStatus.APPROVED }
            .forEach { chapter ->
              val segs = uiState.value.segmentsByChapter[chapter.id].orEmpty()
              val files = segs.mapNotNull { it.audioFilePath?.let { p -> File(p) } }.filter { it.exists() }
              if (files.isNotEmpty()) {
                when (format) {
                  AudioExportFormat.WAV -> {
                    val wavFiles = files.filter { it.extension.equals("wav", true) }
                    if (wavFiles.isNotEmpty()) {
                      val out =
                        File(exportDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "wav"))
                      if (AudioEncoderExporter.combineWavFiles(wavFiles, out)) {
                        chapterFiles.add(out.name to out)
                        wavToCombine.add(out)
                      }
                    }
                  }
                  AudioExportFormat.M4A -> {
                    val m4aReady = mutableListOf<File>()
                    files.forEachIndexed { idx, f ->
                      if (f.extension.equals("m4a", true)) {
                        m4aReady.add(f)
                      } else if (f.extension.equals("wav", true)) {
                        val conv = File(exportDir, "${chapter.id}_tmp_$idx.m4a")
                        if (AudioEncoderExporter.encodeWavToM4a(f, conv)) m4aReady.add(conv)
                      }
                    }
                    if (m4aReady.isNotEmpty()) {
                      val out =
                        File(exportDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "m4a"))
                      if (AudioEncoderExporter.combineM4aFiles(m4aReady, out)) {
                        chapterFiles.add(out.name to out)
                        m4aToCombine.add(out)
                      }
                    }
                  }
                  AudioExportFormat.MP3 -> {
                    val mp3s = files.filter { it.extension.equals("mp3", true) }
                    if (mp3s.isNotEmpty()) {
                      val out =
                        File(exportDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "mp3"))
                      mp3s.first().copyTo(out, overwrite = true)
                      chapterFiles.add(out.name to out)
                    } else {
                      mp3NoteNeeded = true
                    }
                  }
                }
              }
            }

          if (m4aToCombine.isNotEmpty()) {
            val combined = File(exportDir, "AUDIOBOOK_COMPLETE.m4a")
            if (AudioEncoderExporter.combineM4aFiles(m4aToCombine, combined)) {
              chapterFiles.add(combined.name to combined)
            }
          } else if (wavToCombine.isNotEmpty()) {
            val combined = File(exportDir, "AUDIOBOOK_COMPLETE.wav")
            if (AudioEncoderExporter.combineWavFiles(wavToCombine, combined)) {
              chapterFiles.add(combined.name to combined)
            }
          }

          val exportedNames =
            if (chapterFiles.isNotEmpty()) {
              AudioEncoderExporter.exportFilesToSafTree(
                context = context,
                treeUri = treeUri,
                folderName = "ART_OF_LIVING_AUDIO",
                namedFiles = chapterFiles,
              )
            } else emptyList()

          Triple(exportedNames, mp3NoteNeeded, chapterFiles.size)
        }

      val (names, mp3Warn, stagedCount) = exportResult
      when {
        names.isNotEmpty() -> {
          postFeedback(
            "تعداد ${names.size} فایل در پوشه ART_OF_LIVING_AUDIO/ خروجی گرفته شد: ${names.joinToString("، ")}",
            "Exported ${names.size} files to ART_OF_LIVING_AUDIO/: ${names.joinToString(", ")}",
          )
        }
        mp3Warn -> {
          postFeedback(
            "اندروید کدگذار بومی MP3 ندارد و فایل MP3 واردشده‌ای یافت نشد؛ لطفاً فرمت M4A یا WAV را برای خروجی انتخاب کنید.",
            "Android has no native MP3 encoder and no imported MP3 exists; select M4A or WAV to export.",
            isError = true,
          )
        }
        stagedCount == 0 -> {
          postFeedback(
            "هنوز هیچ فصلی فایل صوتی آماده برای خروجی ندارد.",
            "No narrated chapter audio is ready to export yet.",
            isError = true,
          )
        }
      }
    }
  }

  // Listener Player actions
  fun playOrToggleSegment(chapterId: String, segmentIndex: Int, startPositionMs: Long = 0L) {
    val segs = uiState.value.segmentsByChapter[chapterId].orEmpty()
    audiobookPlayer.togglePlayPause(chapterId, segs, segmentIndex)
  }

  fun playSegmentFromPosition(chapterId: String, segmentIndex: Int, startPositionMs: Long) {
    val segs = uiState.value.segmentsByChapter[chapterId].orEmpty()
    audiobookPlayer.playSegment(chapterId, segs, segmentIndex, startPositionMs)
  }

  fun seekAudiobookTo(positionMs: Long) = audiobookPlayer.seekTo(positionMs)

  fun seekAudiobookBy(deltaMs: Long) = audiobookPlayer.seekBy(deltaMs)

  fun setAudiobookSpeed(speed: Float) {
    currentPlaybackSpeed = speed
    audiobookPlayer.setSpeed(speed)
  }

  fun setAudiobookVolume(volume: Float) = audiobookPlayer.setVolume(volume)

  fun addBookmarkAtCurrentPosition(chapterId: String, label: String) {
    val ps = uiState.value.audiobookPlayerState
    val segId = ps.activeSegmentId ?: "${chapterId}_seg_0"
    viewModelScope.launch {
      repository.addAudioBookmark(
        chapterId = chapterId,
        segmentId = segId,
        segmentIndex = ps.activeSegmentIndex,
        positionMs = ps.positionMs,
        label = label,
      )
      postFeedback("نشانک صوتی ثبت شد.", "Audio bookmark saved.")
    }
  }

  fun deleteAudioBookmark(id: Long) {
    viewModelScope.launch { repository.deleteAudioBookmark(id) }
  }

  fun clearStudioFeedback() {
    studioControlFlow.update { it.copy(feedback = null) }
  }

  private fun postFeedback(msgFa: String, msgEn: String, isError: Boolean = false) {
    studioControlFlow.update {
      it.copy(feedback = AudiobookFeedbackBanner(msgFa, msgEn, isError))
    }
  }

  override fun onCleared() {
    audioPlayer.release()
    devicePersianTts.release()
    voiceRecorder.cancelIfRunning()
    audiobookPlayer.release()
    super.onCleared()
  }
}
