package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookCatalog
import com.example.data.BookRepository
import com.example.data.GalleryArtwork
import com.example.data.local.BookDatabase
import com.example.data.local.NoteEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.ReadingProgressEntity
import com.example.media.AudioPlaybackState
import com.example.media.ChapterAudioPlayer
import com.example.model.AccessibilitySettings
import com.example.model.AppLanguage
import com.example.model.BookPart
import com.example.model.Chapter
import com.example.model.ContentCategory
import com.example.model.ReaderBlock
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

class HonarZistanViewModel(application: Application) : AndroidViewModel(application) {
  private val repository =
    BookRepository(
      dao = BookDatabase.getInstance(application).bookUserDao(),
      prefs = PreferencesRepository(application),
    )

  private val audioPlayer = ChapterAudioPlayer(application)

  private val selectedCategoryFlow = MutableStateFlow<ContentCategory?>(null)
  private val searchQueryFlow = MutableStateFlow("")
  private val focusModeReaderFlow = MutableStateFlow(false)

  private val filterBundle =
    combine(selectedCategoryFlow, searchQueryFlow, focusModeReaderFlow) { cat, q, focus ->
      Triple(cat, q, focus)
    }

  val uiState: StateFlow<HonarZistanUiState> =
    combine(
        repository.settings,
        repository.bookmarks,
        repository.notes,
        repository.progress,
        combine(audioPlayer.state, filterBundle) { audio, filter -> Pair(audio, filter) },
      ) { settings, bookmarks, notes, progressList, audioAndFilter ->
        val (audioState, filter) = audioAndFilter
        val (selectedCategory, searchQuery, focusActive) = filter
        val progressMap = progressList.associateBy { it.chapterId }
        val lastChapter = progressList.firstOrNull()?.chapterId ?: "ch_01"
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

  override fun onCleared() {
    audioPlayer.release()
    super.onCleared()
  }
}
