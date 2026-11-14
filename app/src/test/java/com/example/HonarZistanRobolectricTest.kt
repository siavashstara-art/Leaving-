package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HonarZistanRobolectricTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun homeScreen_bookContents_reader_and_nineItemMenuWork() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("هنر زیستن")[0].assertIsDisplayed()

    // Open Book Contents (Book -> Part -> Chapter -> Section)
    composeTestRule.onNodeWithTag("nav_contents").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("book_contents_screen").assertIsDisplayed()

    // Open Reader from Home Continue Reading & verify honest audio + controls
    composeTestRule.onNodeWithTag("nav_home").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("continue_reading_btn").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule.onAllNodesWithTag("reader_screen").fetchSemanticsNodes().isNotEmpty() &&
        composeTestRule.onNodeWithTag("reader_screen").isDisplayed()
    }
    composeTestRule.onNodeWithTag("reader_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reader_font_inc").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("audio_play_btn").performScrollTo().assertIsNotEnabled()
    composeTestRule.onNodeWithTag("transcript_toggle_btn").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("reader_bookmark_btn").performClick()
    composeTestRule.onNodeWithTag("focus_mode_toggle_btn").performClick()
    composeTestRule.waitForIdle()

    // Back and open persistent 9-item navigation menu -> Poetry, Quranic, Health
    composeTestRule.onNodeWithTag("reader_back_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("open_main_menu_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_home").assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_book_contents").assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_chapters").assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_poetry").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_quranic").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_health").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_saved").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_search").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("menu_settings").performScrollTo().assertIsDisplayed()

    // Visit Poetry screen
    composeTestRule.onNodeWithTag("menu_poetry").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("poetry_screen").assertIsDisplayed()

    // Open Quranic Reflections via menu & verify separated translation/explanation/reference
    composeTestRule.onNodeWithTag("open_main_menu_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_quranic").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("quranic_screen").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("توضیح واژگانی و مضمون")[0].assertIsDisplayed()

    // Open Health and Wellbeing via menu
    composeTestRule.onNodeWithTag("open_main_menu_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_health").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("chapters_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("chapter_card_ch_05").assertIsDisplayed()
  }

  @Test
  fun search_chapters_savedItems_and_settingsLtrSwitchWork() {
    composeTestRule.waitForIdle()

    // Search via labeled TopBar Search button
    composeTestRule.onNodeWithTag("topbar_search_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("search_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("search_input").performTextInput("آرامش")
    composeTestRule.waitForIdle()
    composeTestRule
      .onNodeWithTag("search_results_list")
      .performScrollToNode(hasTestTag("search_result_ch_04"))
    composeTestRule.onNodeWithTag("search_result_ch_04").assertIsDisplayed()

    // Chapters screen
    composeTestRule.onNodeWithTag("nav_chapters").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("chapters_screen").assertIsDisplayed()

    // Saved Items (Library)
    composeTestRule.onNodeWithTag("nav_library").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("library_screen").assertIsDisplayed()

    // Settings via persistent menu -> switch to English LTR
    composeTestRule.onNodeWithTag("open_main_menu_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_settings").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("settings_author_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lang_english_chip").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
        .onAllNodesWithText("Reading & Accessibility Settings")
        .fetchSemanticsNodes()
        .isNotEmpty()
    }
    composeTestRule.onNodeWithText("Reading & Accessibility Settings").assertIsDisplayed()
    composeTestRule.onNodeWithTag("settings_author_list").performScrollToIndex(3)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Siavash Ali-Miri").assertIsDisplayed()
  }

  @Test
  fun audiobookListener_and_narrationStudio_work() {
    composeTestRule.waitForIdle()

    // Open Audiobook Listener via menu
    composeTestRule.onNodeWithTag("open_main_menu_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("menu_audiobook").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("audiobook_listener_screen").assertIsDisplayed()
    composeTestRule
      .onNodeWithTag("audiobook_listener_list")
      .performScrollToNode(hasTestTag("listener_play_pause_btn"))
    composeTestRule.onNodeWithTag("listener_play_pause_btn").assertIsNotEnabled()

    // Scroll back to header and open Narration Studio from Listener button
    composeTestRule.onNodeWithTag("audiobook_listener_list").performScrollToIndex(0)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("open_studio_from_listener_btn").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("narration_studio_screen").assertIsDisplayed()

    // Scroll to script preparation for approved ch_01 and verify segments render
    composeTestRule
      .onNodeWithTag("narration_studio_list")
      .performScrollToNode(hasTestTag("prepare_script_btn"))
    composeTestRule.onNodeWithTag("prepare_script_btn").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule
        .onAllNodesWithTag("studio_segment_ch_01_seg_0")
        .fetchSemanticsNodes()
        .isNotEmpty() ||
        runCatching {
            composeTestRule
              .onNodeWithTag("narration_studio_list")
              .performScrollToNode(hasTestTag("studio_segment_ch_01_seg_0"))
          }
          .isSuccess
    }
    composeTestRule
      .onNodeWithTag("narration_studio_list")
      .performScrollToNode(hasTestTag("studio_segment_ch_01_seg_0"))
    composeTestRule.onNodeWithTag("studio_segment_ch_01_seg_0").assertIsDisplayed()

    // Select unapproved draft ch_07 and verify Editorial Lock Banner appears
    composeTestRule
      .onNodeWithTag("narration_studio_list")
      .performScrollToNode(hasTestTag("studio_chapter_row"))
    composeTestRule
      .onNodeWithTag("studio_chapter_row")
      .performScrollToNode(hasTestTag("studio_chapter_chip_ch_07"))
    composeTestRule.onNodeWithTag("studio_chapter_chip_ch_07").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
      .onNodeWithTag("narration_studio_list")
      .performScrollToNode(hasTestTag("unapproved_chapter_lock_banner"))
    composeTestRule.onNodeWithTag("unapproved_chapter_lock_banner").assertIsDisplayed()
  }
}
