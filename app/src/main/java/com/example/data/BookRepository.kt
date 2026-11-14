package com.example.data

import com.example.R
import com.example.data.local.AudioBookmarkEntity
import com.example.data.local.AudioChapterConfigEntity
import com.example.data.local.AudioSegmentEntity
import com.example.data.local.AudioVersionArchiveEntity
import com.example.data.local.BookUserDao
import com.example.data.local.BookmarkEntity
import com.example.data.local.ListeningProgressEntity
import com.example.data.local.NoteEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.ReadingProgressEntity
import com.example.media.AudioEncoderExporter
import com.example.media.AudiobookScriptEngine
import com.example.media.ImportedAudioInfo
import com.example.media.NarrationGenerationResult
import com.example.model.AccessibilitySettings
import com.example.model.AppLanguage
import com.example.model.AudioBookmarkUi
import com.example.model.AudioChapterWorkflowStatus
import com.example.model.AudioExportFormat
import com.example.model.AudioQualityChecklist
import com.example.model.AudioVersionSnapshot
import com.example.model.BookPart
import com.example.model.Chapter
import com.example.model.ChapterAudioProductionUi
import com.example.model.ListeningProgressUi
import com.example.model.ManuscriptApprovalStatus
import com.example.model.NarrationSegmentUi
import com.example.model.NarratorType
import com.example.model.ReaderBlock
import com.example.model.ThemeMode
import com.example.model.VoiceCharacteristic
import java.io.File
import kotlinx.coroutines.flow.Flow

data class GalleryArtwork(
  val id: String,
  val drawableRes: Int,
  val titleFa: String,
  val titleEn: String,
  val captionFa: String,
  val captionEn: String,
  val chapterId: String,
)

class BookRepository(
  private val dao: BookUserDao,
  private val prefs: PreferencesRepository,
) {
  val parts: List<BookPart> = BookCatalog.parts
  val chapters: List<Chapter> = BookCatalog.chapters

  val galleryItems: List<GalleryArtwork> =
    listOf(
      GalleryArtwork(
        id = "art_hero",
        drawableRes = R.drawable.img_hero_honar_zistan,
        titleFa = "حیاطِ روشنِ حضور",
        titleEn = "Sunlit Courtyard of Presence",
        captionFa = "نگاره‌ی سرآغاز کتاب؛ حیاط آرام و سروستان در نور بامداد.",
        captionEn = "Book frontispiece: tranquil courtyard and cypress grove in morning light.",
        chapterId = "ch_01",
      ),
      GalleryArtwork(
        id = "art_poetry",
        drawableRes = R.drawable.img_poetry_garden,
        titleFa = "باغِ شبانه‌ی شعر",
        titleEn = "Night Garden of Poetry",
        captionFa = "نگاره‌ی دفتر شعر؛ ستارگان طلایی و شبِ آرامش.",
        captionEn = "Poetry plate: restrained gold stars above nocturnal garden.",
        chapterId = "ch_03",
      ),
      GalleryArtwork(
        id = "art_reflection",
        drawableRes = R.drawable.img_chapter_reflection,
        titleFa = "بامدادِ زیتون و انار",
        titleEn = "Morning of Olive & Pomegranate",
        captionFa = "نگاره‌ی تندرستی و سادگی؛ شاخه زیتون و فنجان صبحگاهی.",
        captionEn = "Wellbeing plate: olive branches and morning tea ritual.",
        chapterId = "ch_05",
      ),
      GalleryArtwork(
        id = "art_author",
        drawableRes = R.drawable.img_author_quill,
        titleFa = "قلم، دوات و یاس",
        titleEn = "Reed Pen, Inkwell & Jasmine",
        captionFa = "نگاره‌ی شناسنامه و قلمِ سیاوش علی‌میری.",
        captionEn = "Colophon emblem honoring author Siavash Ali-Miri.",
        chapterId = "ch_02",
      ),
    )

  val settings: Flow<AccessibilitySettings> = prefs.settingsFlow
  val bookmarks: Flow<List<BookmarkEntity>> = dao.observeBookmarks()
  val notes: Flow<List<NoteEntity>> = dao.observeNotes()
  val progress: Flow<List<ReadingProgressEntity>> = dao.observeProgress()
  val audioConfigs: Flow<List<AudioChapterConfigEntity>> = dao.observeAudioConfigs()
  val audioSegments: Flow<List<AudioSegmentEntity>> = dao.observeAllAudioSegments()
  val audioVersionArchives: Flow<List<AudioVersionArchiveEntity>> =
    dao.observeAudioVersionArchives()
  val listeningProgress: Flow<List<ListeningProgressEntity>> = dao.observeListeningProgress()
  val audioBookmarks: Flow<List<AudioBookmarkEntity>> = dao.observeAudioBookmarks()

  suspend fun toggleBookmark(chapterId: String, currentlyBookmarked: Boolean) {
    if (currentlyBookmarked) {
      dao.removeBookmark(chapterId)
    } else {
      dao.addBookmark(BookmarkEntity(chapterId = chapterId))
    }
  }

  suspend fun saveNote(chapterId: String, content: String, existingId: Long = 0L) {
    val trimmed = content.trim()
    if (trimmed.isNotEmpty()) {
      dao.upsertNote(
        NoteEntity(
          id = existingId,
          chapterId = chapterId,
          content = trimmed,
          updatedAt = System.currentTimeMillis(),
        )
      )
    }
  }

  suspend fun deleteNote(noteId: Long) = dao.deleteNote(noteId)

  suspend fun markChapterRead(chapterId: String, isCompleted: Boolean) {
    dao.upsertProgress(
      ReadingProgressEntity(
        chapterId = chapterId,
        isCompleted = isCompleted,
        scrollFraction = if (isCompleted) 1.0f else 0.35f,
        lastReadAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun recordChapterOpened(chapterId: String, alreadyCompleted: Boolean) {
    dao.upsertProgress(
      ReadingProgressEntity(
        chapterId = chapterId,
        isCompleted = alreadyCompleted,
        scrollFraction = if (alreadyCompleted) 1.0f else 0.45f,
        lastReadAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun setThemeMode(mode: ThemeMode) = prefs.updateThemeMode(mode)

  suspend fun setFontScale(scale: Float) = prefs.updateFontScale(scale)

  suspend fun setLargeText(enabled: Boolean) = prefs.updateLargeText(enabled)

  suspend fun setHighContrast(enabled: Boolean) = prefs.updateHighContrast(enabled)

  suspend fun setReducedMotion(enabled: Boolean) = prefs.updateReducedMotion(enabled)

  suspend fun setFocusModeDefault(enabled: Boolean) = prefs.updateFocusModeDefault(enabled)

  suspend fun setLanguage(language: AppLanguage) = prefs.updateLanguage(language)

  // --- Audiobook Production & Synchronization ---

  suspend fun prepareChapterScriptSegments(chapter: Chapter) {
    if (chapter.approvalStatus != ManuscriptApprovalStatus.APPROVED) return
    val existing = dao.getSegmentsForChapter(chapter.id).associateBy { it.segmentId }
    val drafts = AudiobookScriptEngine.segmentChapter(chapter)
    val entities =
      drafts.map { draft ->
        val prev = existing[draft.segmentId]
        if (prev == null) {
          AudioSegmentEntity(
            segmentId = draft.segmentId,
            chapterId = chapter.id,
            sectionId = draft.sectionId,
            segmentIndex = draft.index,
            titleFa = draft.titleFa,
            titleEn = draft.titleEn,
            editedScriptFa = draft.preparedScriptFa,
            currentSourceHash = draft.sourceHash,
          )
        } else {
          // Detect whether manuscript draft changed since audio was recorded
          val baseHash = AudiobookScriptEngine.sha256(prev.editedScriptFa)
          prev.copy(
            segmentIndex = draft.index,
            titleFa = draft.titleFa,
            titleEn = draft.titleEn,
            currentSourceHash = baseHash,
          )
        }
      }
    dao.upsertAudioSegments(entities)

    val cfg = dao.getAudioConfig(chapter.id) ?: AudioChapterConfigEntity(chapterId = chapter.id)
    val nextStatus =
      if (cfg.workflowStatus == AudioChapterWorkflowStatus.NOT_STARTED.name) {
        AudioChapterWorkflowStatus.SCRIPT_PREPARED.name
      } else {
        cfg.workflowStatus
      }
    dao.upsertAudioConfig(cfg.copy(workflowStatus = nextStatus, updatedAt = System.currentTimeMillis()))
  }

  suspend fun updateNarratorConfig(
    chapterId: String,
    narratorType: NarratorType,
    voiceCharacteristic: VoiceCharacteristic,
    authorVoiceAuthorized: Boolean,
  ) {
    val existing = dao.getAudioConfig(chapterId) ?: AudioChapterConfigEntity(chapterId = chapterId)
    dao.upsertAudioConfig(
      existing.copy(
        narratorType = narratorType.name,
        voiceCharacteristic = voiceCharacteristic.name,
        authorVoiceAuthorized = authorVoiceAuthorized,
        updatedAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun updateSegmentScript(segmentId: String, updatedScriptFa: String) {
    val normalized = AudiobookScriptEngine.normalizePersianNarrationPunctuation(updatedScriptFa)
    val newHash = AudiobookScriptEngine.sha256(normalized)
    val all = dao.getSegmentsForChapter(segmentId.substringBefore("_seg_"))
    val seg = all.find { it.segmentId == segmentId } ?: return
    val textChangedAfterRecording =
      !seg.audioFilePath.isNullOrBlank() &&
        seg.recordedSourceHash.isNotBlank() &&
        seg.recordedSourceHash != newHash

    dao.upsertAudioSegment(
      seg.copy(
        editedScriptFa = normalized,
        currentSourceHash = newHash,
        isApproved = if (textChangedAfterRecording) false else seg.isApproved,
        updatedAt = System.currentTimeMillis(),
      )
    )

    if (textChangedAfterRecording) {
      val cfg =
        dao.getAudioConfig(seg.chapterId) ?: AudioChapterConfigEntity(chapterId = seg.chapterId)
      if (cfg.approvedVersion > 0 || cfg.workflowStatus == AudioChapterWorkflowStatus.APPROVED.name) {
        dao.upsertAudioConfig(
          cfg.copy(
            workflowStatus = AudioChapterWorkflowStatus.OUTDATED.name,
            updatedAt = System.currentTimeMillis(),
          )
        )
      }
    }
  }

  suspend fun attachGeneratedSegmentAudio(
    segmentId: String,
    narratorType: NarratorType,
    result: NarrationGenerationResult.Success,
  ) {
    val chapterId = segmentId.substringBefore("_seg_")
    val seg =
      dao.getSegmentsForChapter(chapterId).find { it.segmentId == segmentId } ?: return
    val cfg = dao.getAudioConfig(chapterId) ?: AudioChapterConfigEntity(chapterId = chapterId)
    val nextVer = (cfg.approvedVersion + 1).coerceAtLeast(1)

    dao.upsertAudioSegment(
      seg.copy(
        narratorType = narratorType.name,
        recordedSourceHash = seg.currentSourceHash,
        audioFilePath = result.file.absolutePath,
        audioFormat = result.format.name,
        durationMs = result.durationMs,
        peakAmplitude = result.peakAmplitude,
        isClippingWarning = result.clippingDetected,
        isApproved = false,
        versionNumber = nextVer,
        updatedAt = System.currentTimeMillis(),
      )
    )

    val allUpdated = dao.getSegmentsForChapter(chapterId)
    val allHaveAudio = allUpdated.isNotEmpty() && allUpdated.all { !it.audioFilePath.isNullOrBlank() }
    val nextWorkflow =
      if (allHaveAudio) AudioChapterWorkflowStatus.IN_REVIEW.name
      else AudioChapterWorkflowStatus.IN_PRODUCTION.name
    dao.upsertAudioConfig(
      cfg.copy(workflowStatus = nextWorkflow, updatedAt = System.currentTimeMillis())
    )
  }

  suspend fun attachImportedSegmentAudio(
    segmentId: String,
    narratorType: NarratorType,
    info: ImportedAudioInfo,
  ) {
    val chapterId = segmentId.substringBefore("_seg_")
    val seg =
      dao.getSegmentsForChapter(chapterId).find { it.segmentId == segmentId } ?: return
    val format =
      when (info.extension.lowercase()) {
        "mp3" -> AudioExportFormat.MP3
        "wav" -> AudioExportFormat.WAV
        else -> AudioExportFormat.M4A
      }
    val cfg = dao.getAudioConfig(chapterId) ?: AudioChapterConfigEntity(chapterId = chapterId)
    val nextVer = (cfg.approvedVersion + 1).coerceAtLeast(1)

    dao.upsertAudioSegment(
      seg.copy(
        narratorType = narratorType.name,
        recordedSourceHash = seg.currentSourceHash,
        audioFilePath = info.filePath,
        audioFormat = format.name,
        durationMs = info.durationMs,
        peakAmplitude = 0,
        isClippingWarning = false,
        isApproved = false,
        versionNumber = nextVer,
        updatedAt = System.currentTimeMillis(),
      )
    )
    val allUpdated = dao.getSegmentsForChapter(chapterId)
    val allHaveAudio = allUpdated.isNotEmpty() && allUpdated.all { !it.audioFilePath.isNullOrBlank() }
    dao.upsertAudioConfig(
      cfg.copy(
        workflowStatus =
          if (allHaveAudio) AudioChapterWorkflowStatus.IN_REVIEW.name
          else AudioChapterWorkflowStatus.IN_PRODUCTION.name,
        updatedAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun toggleQualityChecklistBit(chapterId: String, bitIndex: Int) {
    val cfg = dao.getAudioConfig(chapterId) ?: AudioChapterConfigEntity(chapterId = chapterId)
    val updatedMask = cfg.checklistMask xor (1 shl bitIndex)
    dao.upsertAudioConfig(
      cfg.copy(checklistMask = updatedMask, updatedAt = System.currentTimeMillis())
    )
  }

  suspend fun approveChapterAudio(
    chapter: Chapter,
    appFilesDir: File,
  ): Pair<Boolean, String> {
    if (chapter.approvalStatus != ManuscriptApprovalStatus.APPROVED) {
      return false to "فصل تأییدنشده یا ناقص مجاز به تأیید صوتی نهایی نیست."
    }
    val cfg = dao.getAudioConfig(chapter.id) ?: return false to "ابتدا متن گویندگی را آماده کنید."
    val segments = dao.getSegmentsForChapter(chapter.id)
    if (segments.isEmpty() || segments.any { it.audioFilePath.isNullOrBlank() }) {
      return false to "همه بندهای فصل هنوز فایل صوتی ندارند."
    }
    if (segments.any { it.recordedSourceHash != it.currentSourceHash }) {
      return false to "برخی قطعات نسبت به متن ویرایش‌شده منقضی هستند؛ ابتدا بازضبط یا بازتولید کنید."
    }
    val checklist = decodeChecklistMask(cfg.checklistMask)
    if (!checklist.allPassed) {
      return false to "لطفاً همه موارد چک‌لیست بازبینی کیفیت صوتی را پیش از تأیید نهایی علامت بزنید."
    }

    val newVersion = cfg.approvedVersion + 1
    val archiveDir = File(appFilesDir, "audiobook/versions/${chapter.id}_v$newVersion")
    archiveDir.mkdirs()

    val sourceFiles = segments.mapNotNull { it.audioFilePath?.let { path -> File(path) } }
    val allWav = sourceFiles.all { it.extension.equals("wav", ignoreCase = true) }
    val allM4a = sourceFiles.all { it.extension.equals("m4a", ignoreCase = true) }
    val archiveMaster =
      when {
        allM4a -> {
          val out = File(archiveDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "m4a"))
          AudioEncoderExporter.combineM4aFiles(sourceFiles, out)
          out
        }
        allWav -> {
          val wavOut = File(archiveDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "wav"))
          AudioEncoderExporter.combineWavFiles(sourceFiles, wavOut)
          val m4aOut = File(archiveDir, AudiobookScriptEngine.chapterManifestFileName(chapter.number, "m4a"))
          if (AudioEncoderExporter.encodeWavToM4a(wavOut, m4aOut)) m4aOut else wavOut
        }
        else -> {
          // Copy each segment cleanly into version archive directory to preserve exact multi-format edition
          sourceFiles.forEachIndexed { i, f ->
            f.copyTo(File(archiveDir, "seg_${i + 1}.${f.extension}"), overwrite = true)
          }
          archiveDir
        }
      }

    // Mark segments approved while preserving versioned snapshot in archives
    val updatedSegments =
      segments.map { it.copy(isApproved = true, versionNumber = newVersion) }
    dao.upsertAudioSegments(updatedSegments)

    val chapterHash = AudiobookScriptEngine.computeChapterHash(chapter)
    dao.upsertAudioConfig(
      cfg.copy(
        workflowStatus = AudioChapterWorkflowStatus.APPROVED.name,
        approvedVersion = newVersion,
        approvedManuscriptHash = chapterHash,
        updatedAt = System.currentTimeMillis(),
      )
    )
    dao.insertAudioVersionArchive(
      AudioVersionArchiveEntity(
        chapterId = chapter.id,
        versionNumber = newVersion,
        manuscriptHash = chapterHash,
        archivePath = archiveMaster.absolutePath,
        formatLabel = if (archiveMaster.isDirectory) "MULTI" else archiveMaster.extension.uppercase(),
        narratorSummary = cfg.narratorType,
        approvedAt = System.currentTimeMillis(),
      )
    )
    return true to "نسخه صوتی v$newVersion فصل با موفقیت تأیید و بایگانی شد."
  }

  suspend fun saveListeningProgress(
    chapterId: String,
    segmentId: String,
    segmentIndex: Int,
    positionMs: Long,
    speed: Float,
  ) {
    dao.upsertListeningProgress(
      ListeningProgressEntity(
        chapterId = chapterId,
        lastSegmentId = segmentId,
        lastSegmentIndex = segmentIndex,
        positionMs = positionMs,
        playbackSpeed = speed,
        updatedAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun addAudioBookmark(
    chapterId: String,
    segmentId: String,
    segmentIndex: Int,
    positionMs: Long,
    label: String,
  ) {
    dao.insertAudioBookmark(
      AudioBookmarkEntity(
        chapterId = chapterId,
        segmentId = segmentId,
        segmentIndex = segmentIndex,
        positionMs = positionMs,
        label = label.ifBlank { "نشانک صوتی" },
        createdAt = System.currentTimeMillis(),
      )
    )
  }

  suspend fun deleteAudioBookmark(id: Long) = dao.deleteAudioBookmark(id)

  fun buildTranscript(chapter: Chapter, language: AppLanguage): String {
    val sb = StringBuilder()
    sb.appendLine(if (language == AppLanguage.PERSIAN) chapter.titleFa else chapter.titleEn)
    sb.appendLine()
    chapter.sections.forEach { section ->
      sb.appendLine(if (language == AppLanguage.PERSIAN) section.titleFa else section.titleEn)
      section.blocks.forEach { block ->
        when (block) {
          is ReaderBlock.Prose -> {
            sb.appendLine(if (language == AppLanguage.PERSIAN) block.textFa else block.textEn)
          }
          is ReaderBlock.Poetry -> {
            if (language == AppLanguage.PERSIAN) {
              block.coupletsFa.forEach { (first, second) -> sb.appendLine("$first — $second") }
            } else {
              sb.appendLine(block.translationEn)
            }
          }
          is ReaderBlock.QuranicVerse -> {
            sb.appendLine(block.arabicText)
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.translationFa else block.translationEn
            )
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.explanationFa else block.explanationEn
            )
            sb.appendLine(
              if (language == AppLanguage.PERSIAN) block.reflectionFa else block.reflectionEn
            )
          }
          is ReaderBlock.Illustration -> {
            sb.appendLine(
              "[${if (language == AppLanguage.PERSIAN) block.captionFa else block.captionEn}]"
            )
          }
        }
      }
      sb.appendLine()
    }
    return sb.toString().trim()
  }

  companion object {
    fun decodeChecklistMask(mask: Int): AudioQualityChecklist =
      AudioQualityChecklist(
        pronunciationChecked = (mask and (1 shl 0)) != 0,
        clarityChecked = (mask and (1 shl 1)) != 0,
        noiseChecked = (mask and (1 shl 2)) != 0,
        volumeConsistencyChecked = (mask and (1 shl 3)) != 0,
        pausesAndEndingsChecked = (mask and (1 shl 4)) != 0,
        completenessChecked = (mask and (1 shl 5)) != 0,
        noClippingChecked = (mask and (1 shl 6)) != 0,
      )

    fun mapProductionState(
      chapterId: String,
      config: AudioChapterConfigEntity?,
      segments: List<AudioSegmentEntity>,
    ): ChapterAudioProductionUi {
      val drafts = BookCatalog.chapters.find { it.id == chapterId }
      val outdatedCount =
        segments.count {
          !it.audioFilePath.isNullOrBlank() &&
            it.recordedSourceHash.isNotBlank() &&
            it.recordedSourceHash != it.currentSourceHash
        }
      val readyCount = segments.count { !it.audioFilePath.isNullOrBlank() }
      val narratorType =
        config?.narratorType?.let { runCatching { NarratorType.valueOf(it) }.getOrNull() }
          ?: NarratorType.AUTHOR_VOICE
      val voiceChar =
        config?.voiceCharacteristic?.let {
          runCatching { VoiceCharacteristic.valueOf(it) }.getOrNull()
        } ?: VoiceCharacteristic.WARM_FRIENDLY
      val workflow =
        when {
          outdatedCount > 0 -> AudioChapterWorkflowStatus.OUTDATED
          config != null ->
            runCatching { AudioChapterWorkflowStatus.valueOf(config.workflowStatus) }
              .getOrDefault(AudioChapterWorkflowStatus.NOT_STARTED)
          else -> AudioChapterWorkflowStatus.NOT_STARTED
        }
      return ChapterAudioProductionUi(
        chapterId = chapterId,
        narratorType = narratorType,
        voiceCharacteristic = voiceChar,
        authorVoiceAuthorized = config?.authorVoiceAuthorized ?: false,
        workflowStatus = workflow,
        approvedVersion = config?.approvedVersion ?: 0,
        qualityChecklist = decodeChecklistMask(config?.checklistMask ?: 0),
        outdatedSegmentCount = outdatedCount,
        readySegmentCount = readyCount,
        totalSegmentCount = segments.size.takeIf { it > 0 } ?: (drafts?.sections?.size ?: 0),
      )
    }

    fun mapSegmentUi(
      chapter: Chapter?,
      entity: AudioSegmentEntity,
    ): NarrationSegmentUi {
      val draft =
        chapter?.let { AudiobookScriptEngine.segmentChapter(it) }?.find { it.segmentId == entity.segmentId }
      val isOutdated =
        !entity.audioFilePath.isNullOrBlank() &&
          entity.recordedSourceHash.isNotBlank() &&
          entity.recordedSourceHash != entity.currentSourceHash
      val narrator =
        runCatching { NarratorType.valueOf(entity.narratorType) }.getOrDefault(NarratorType.AUTHOR_VOICE)
      val format =
        entity.audioFormat?.let { runCatching { AudioExportFormat.valueOf(it) }.getOrNull() }
      return NarrationSegmentUi(
        segmentId = entity.segmentId,
        chapterId = entity.chapterId,
        sectionId = entity.sectionId,
        index = entity.segmentIndex,
        titleFa = entity.titleFa,
        titleEn = entity.titleEn,
        manuscriptTextFa = draft?.manuscriptTextFa ?: entity.editedScriptFa,
        manuscriptTextEn = draft?.manuscriptTextEn ?: "",
        narrationScriptFa = entity.editedScriptFa,
        currentManuscriptHash = entity.currentSourceHash,
        recordedSourceHash = entity.recordedSourceHash,
        narratorType = narrator,
        audioFilePath = entity.audioFilePath,
        audioFormat = format,
        durationMs = entity.durationMs,
        peakAmplitude = entity.peakAmplitude,
        isClippingWarning = entity.isClippingWarning,
        isOutdated = isOutdated,
        isApproved = entity.isApproved,
        versionNumber = entity.versionNumber,
      )
    }

    fun mapArchiveUi(e: AudioVersionArchiveEntity): AudioVersionSnapshot =
      AudioVersionSnapshot(
        id = e.id,
        chapterId = e.chapterId,
        versionNumber = e.versionNumber,
        narratorSummary = e.narratorSummary,
        formatLabel = e.formatLabel,
        approvedAt = e.approvedAt,
        manuscriptHash = e.manuscriptHash,
      )

    fun mapListeningProgress(e: ListeningProgressEntity): ListeningProgressUi =
      ListeningProgressUi(
        chapterId = e.chapterId,
        lastSegmentId = e.lastSegmentId,
        lastSegmentIndex = e.lastSegmentIndex,
        positionMs = e.positionMs,
        playbackSpeed = e.playbackSpeed,
        updatedAt = e.updatedAt,
      )

    fun mapAudioBookmark(e: AudioBookmarkEntity): AudioBookmarkUi =
      AudioBookmarkUi(
        id = e.id,
        chapterId = e.chapterId,
        segmentId = e.segmentId,
        segmentIndex = e.segmentIndex,
        positionMs = e.positionMs,
        label = e.label,
        createdAt = e.createdAt,
      )
  }
}
