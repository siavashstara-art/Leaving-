package com.example.model

enum class NarratorType(
  val titleFa: String,
  val titleEn: String,
  val descriptionFa: String,
  val descriptionEn: String,
) {
  AI_PERSIAN(
    titleFa = "گوینده هوشمند فارسی (AI)",
    titleEn = "AI Persian Narrator",
    descriptionFa = "تولید با موتور گفتار فارسی دستگاه یا سرویس ابری مجاز",
    descriptionEn = "Uses on-device Persian TTS or authorized cloud adapter",
  ),
  HUMAN_NARRATOR(
    titleFa = "گوینده انسانی / استودیویی",
    titleEn = "Human Narrator",
    descriptionFa = "وارد کردن فایل ضبط‌شده‌ی گوینده انسانی (MP3 / WAV / M4A)",
    descriptionEn = "Import human studio narration files (MP3 / WAV / M4A)",
  ),
  AUTHOR_VOICE(
    titleFa = "صدای مؤلف (سیاوش علی‌میری)",
    titleEn = "Author's Own Voice",
    descriptionFa = "ضبط مستقیم با میکروفون یا فایل مجاز صدای نویسنده",
    descriptionEn = "Direct microphone recording or authorized author file",
  ),
}

enum class VoiceCharacteristic(
  val titleFa: String,
  val titleEn: String,
) {
  WARM_FRIENDLY("گرم و صمیمی", "Warm & Friendly"),
  CALM_REFLECTIVE("آرام و تأملی", "Calm & Reflective"),
  LITERARY_EXPRESSIVE("ادبی و بیانی", "Literary & Expressive"),
  GENTLE_INTIMATE("ملایم و خودمانی", "Gentle & Intimate"),
  NEUTRAL_DOCUMENTARY("مستند و خنثی", "Neutral & Documentary"),
}

enum class AudioChapterWorkflowStatus(
  val titleFa: String,
  val titleEn: String,
) {
  NOT_STARTED("شروع‌نشده", "Not Started"),
  SCRIPT_PREPARED("متن گویندگی آماده", "Script Prepared"),
  IN_PRODUCTION("در حال تولید / ضبط", "In Production"),
  IN_REVIEW("در انتظار بازبینی کیفیت", "In Quality Review"),
  APPROVED("تأیید نهایی‌شده", "Approved Audio"),
  OUTDATED("نیازمند بروزرسانی (متن تغییر کرده)", "Outdated (Text Changed)"),
}

enum class AudioExportFormat(
  val extension: String,
  val mimeType: String,
  val labelFa: String,
  val labelEn: String,
  val nativeEncodeSupported: Boolean,
) {
  M4A("m4a", "audio/mp4", "M4A (AAC بومی و کم‌حجم)", "M4A (Native AAC)", true),
  WAV("wav", "audio/wav", "WAV (کیفیت خام بدون افت)", "WAV (Uncompressed PCM)", true),
  MP3("mp3", "audio/mpeg", "MP3 (فایل واردشده / ابری)", "MP3 (Imported / Cloud)", false),
}

data class AudioQualityChecklist(
  val pronunciationChecked: Boolean = false,
  val clarityChecked: Boolean = false,
  val noiseChecked: Boolean = false,
  val volumeConsistencyChecked: Boolean = false,
  val pausesAndEndingsChecked: Boolean = false,
  val completenessChecked: Boolean = false,
  val noClippingChecked: Boolean = false,
) {
  val allPassed: Boolean
    get() =
      pronunciationChecked &&
        clarityChecked &&
        noiseChecked &&
        volumeConsistencyChecked &&
        pausesAndEndingsChecked &&
        completenessChecked &&
        noClippingChecked
}

data class NarrationSegmentUi(
  val segmentId: String,
  val chapterId: String,
  val sectionId: String,
  val index: Int,
  val titleFa: String,
  val titleEn: String,
  val manuscriptTextFa: String,
  val manuscriptTextEn: String,
  val narrationScriptFa: String,
  val currentManuscriptHash: String,
  val recordedSourceHash: String,
  val narratorType: NarratorType,
  val audioFilePath: String?,
  val audioFormat: AudioExportFormat?,
  val durationMs: Long,
  val peakAmplitude: Int,
  val isClippingWarning: Boolean,
  val isOutdated: Boolean,
  val isApproved: Boolean,
  val versionNumber: Int,
)

data class ChapterAudioProductionUi(
  val chapterId: String,
  val narratorType: NarratorType = NarratorType.AUTHOR_VOICE,
  val voiceCharacteristic: VoiceCharacteristic = VoiceCharacteristic.WARM_FRIENDLY,
  val authorVoiceAuthorized: Boolean = false,
  val workflowStatus: AudioChapterWorkflowStatus = AudioChapterWorkflowStatus.NOT_STARTED,
  val approvedVersion: Int = 0,
  val qualityChecklist: AudioQualityChecklist = AudioQualityChecklist(),
  val outdatedSegmentCount: Int = 0,
  val readySegmentCount: Int = 0,
  val totalSegmentCount: Int = 0,
)

data class AudioVersionSnapshot(
  val id: Long,
  val chapterId: String,
  val versionNumber: Int,
  val narratorSummary: String,
  val formatLabel: String,
  val approvedAt: Long,
  val manuscriptHash: String,
)

data class AudioBookmarkUi(
  val id: Long,
  val chapterId: String,
  val segmentId: String,
  val segmentIndex: Int,
  val positionMs: Long,
  val label: String,
  val createdAt: Long,
)

data class ListeningProgressUi(
  val chapterId: String,
  val lastSegmentId: String,
  val lastSegmentIndex: Int,
  val positionMs: Long,
  val playbackSpeed: Float,
  val updatedAt: Long,
)
