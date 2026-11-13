package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.BookCatalog
import com.example.model.AppLanguage
import com.example.model.ContentCategory
import com.example.ui.HonarZistanViewModel
import com.example.ui.screens.BookContentsScreen
import com.example.ui.screens.ChaptersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PoetryScreen
import com.example.ui.screens.QuranicScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsAuthorScreen
import com.example.ui.theme.HonarZistanTheme
import kotlinx.coroutines.launch

sealed class AppRoute(val route: String) {
  data object Home : AppRoute("home")
  data object BookContents : AppRoute("book_contents")
  data object Chapters : AppRoute("chapters")
  data object Poetry : AppRoute("poetry")
  data object Quranic : AppRoute("quranic")
  data object Health : AppRoute("health")
  data object Library : AppRoute("library")
  data object Search : AppRoute("search")
  data object SettingsAuthor : AppRoute("settings_author")
  data object Reader : AppRoute("reader/{chapterId}?section={sectionIndex}") {
    fun create(chapterId: String, sectionIndex: Int = 0): String =
      "reader/$chapterId?section=$sectionIndex"
  }
}

private data class MenuDestination(
  val route: String,
  val labelFa: String,
  val labelEn: String,
  val icon: ImageVector,
  val testTag: String,
)

// All 9 explicitly labeled main navigation items required by Task 13
private val MainNavigationItems =
  listOf(
    MenuDestination(AppRoute.Home.route, "خانه", "Home", Icons.Filled.Home, "menu_home"),
    MenuDestination(
      AppRoute.BookContents.route,
      "فهرست کتاب",
      "Book contents",
      Icons.AutoMirrored.Outlined.ListAlt,
      "menu_book_contents",
    ),
    MenuDestination(
      AppRoute.Chapters.route,
      "فصل‌ها",
      "Chapters",
      Icons.AutoMirrored.Filled.MenuBook,
      "menu_chapters",
    ),
    MenuDestination(
      AppRoute.Poetry.route,
      "شعر و ادب",
      "Poetry and literature",
      Icons.Outlined.AutoAwesome,
      "menu_poetry",
    ),
    MenuDestination(
      AppRoute.Quranic.route,
      "تأملات قرآنی",
      "Quranic reflections",
      Icons.AutoMirrored.Outlined.MenuBook,
      "menu_quranic",
    ),
    MenuDestination(
      AppRoute.Health.route,
      "سلامت و تندرستی",
      "Health and wellbeing",
      Icons.Outlined.FavoriteBorder,
      "menu_health",
    ),
    MenuDestination(
      AppRoute.Library.route,
      "ذخیره‌شده‌ها",
      "Saved items",
      Icons.Filled.Bookmark,
      "menu_saved",
    ),
    MenuDestination(AppRoute.Search.route, "جستجو", "Search", Icons.Outlined.Search, "menu_search"),
    MenuDestination(
      AppRoute.SettingsAuthor.route,
      "تنظیمات و نویسنده",
      "Settings",
      Icons.Outlined.Tune,
      "menu_settings",
    ),
  )

// Compact 4-item bottom bar for instant mobile reach
private val BottomQuickTabs =
  listOf(
    MenuDestination(AppRoute.Home.route, "خانه", "Home", Icons.Filled.Home, "nav_home"),
    MenuDestination(
      AppRoute.BookContents.route,
      "فهرست کتاب",
      "Contents",
      Icons.AutoMirrored.Outlined.ListAlt,
      "nav_contents",
    ),
    MenuDestination(
      AppRoute.Chapters.route,
      "فصل‌ها",
      "Chapters",
      Icons.AutoMirrored.Filled.MenuBook,
      "nav_chapters",
    ),
    MenuDestination(
      AppRoute.Library.route,
      "ذخیره‌شده‌ها",
      "Saved",
      Icons.Filled.Bookmark,
      "nav_library",
    ),
  )

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { HonarZistanApp() }
  }
}

@Composable
fun HonarZistanApp(viewModel: HonarZistanViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isFa = uiState.settings.language == AppLanguage.PERSIAN
  val layoutDirection = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination
  val isReaderRoute = currentDestination?.route?.startsWith("reader/") == true
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  fun navigateMenuRoute(route: String) {
    if (route == AppRoute.Chapters.route) {
      viewModel.selectCategory(null)
    } else if (route == AppRoute.Health.route) {
      viewModel.selectCategory(ContentCategory.HEALTH_WELLBEING)
    }
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  fun openChapterReader(chapterId: String, sectionIndex: Int = 0) {
    viewModel.openChapter(chapterId)
    navController.navigate(AppRoute.Reader.create(chapterId, sectionIndex)) {
      launchSingleTop = true
    }
  }

  val drawerContent: @Composable (Boolean) -> Unit = { closeOnClick ->
    Column(
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)
    ) {
      Text(
        text = if (isFa) BookCatalog.BOOK_TITLE_FA else BookCatalog.BOOK_TITLE_EN,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      )
      Text(
        text =
          if (isFa) "${BookCatalog.BOOK_SUBTITLE_FA} • ${BookCatalog.AUTHOR_FA}"
          else "${BookCatalog.BOOK_SUBTITLE_EN} • ${BookCatalog.AUTHOR_EN}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
      )
      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

      MainNavigationItems.forEach { item ->
        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
        NavigationDrawerItem(
          selected = selected,
          onClick = {
            navigateMenuRoute(item.route)
            if (closeOnClick) scope.launch { drawerState.close() }
          },
          modifier = Modifier.minimumInteractiveComponentSize().testTag(item.testTag),
          icon = { Icon(imageVector = item.icon, contentDescription = null) },
          label = { Text(text = if (isFa) item.labelFa else item.labelEn) },
        )
      }
    }
  }

  HonarZistanTheme(
    themeMode = uiState.settings.themeMode,
    fontScale = uiState.settings.fontScale,
    highContrast = uiState.settings.highContrastEnabled,
  ) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val permanentDrawer = maxWidth >= 840.dp

        val mainScaffold: @Composable () -> Unit = {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
              if (!isReaderRoute) {
                Surface(
                  modifier = Modifier.fillMaxWidth(),
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    if (!permanentDrawer) {
                      TextButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier =
                          Modifier.minimumInteractiveComponentSize().testTag("open_main_menu_btn"),
                      ) {
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isFa) "منوی کتاب" else "Menu")
                      }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text =
                        if (isFa) "${BookCatalog.BOOK_TITLE_FA} — ${BookCatalog.AUTHOR_FA}"
                        else "${BookCatalog.BOOK_TITLE_EN} — ${BookCatalog.AUTHOR_EN}",
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.weight(1f),
                    )
                    TextButton(
                      onClick = { navigateMenuRoute(AppRoute.Search.route) },
                      modifier =
                        Modifier.minimumInteractiveComponentSize().testTag("topbar_search_btn"),
                    ) {
                      Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(if (isFa) "جستجو" else "Search")
                    }
                  }
                }
              }
            },
            bottomBar = {
              if (!permanentDrawer && !isReaderRoute) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                  BottomQuickTabs.forEach { tab ->
                    val selected =
                      currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                      selected = selected,
                      onClick = { navigateMenuRoute(tab.route) },
                      modifier = Modifier.testTag(tab.testTag),
                      icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                      label = { Text(text = if (isFa) tab.labelFa else tab.labelEn, maxLines = 1) },
                    )
                  }
                }
              }
            },
          ) { innerPadding ->
            val reducedMotion = uiState.settings.reducedMotionEnabled
            NavHost(
              navController = navController,
              startDestination = AppRoute.Home.route,
              modifier = Modifier.fillMaxSize().padding(innerPadding),
              enterTransition = {
                if (reducedMotion) EnterTransition.None else fadeIn(tween(150))
              },
              exitTransition = {
                if (reducedMotion) ExitTransition.None else fadeOut(tween(120))
              },
            ) {
              composable(AppRoute.Home.route) {
                HomeScreen(
                  uiState = uiState,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                  onOpenBookContents = { navigateMenuRoute(AppRoute.BookContents.route) },
                  onOpenCategory = { category ->
                    when (category) {
                      ContentCategory.POETRY -> navigateMenuRoute(AppRoute.Poetry.route)
                      ContentCategory.QURANIC_REFLECTION -> navigateMenuRoute(AppRoute.Quranic.route)
                      ContentCategory.HEALTH_WELLBEING -> navigateMenuRoute(AppRoute.Health.route)
                      ContentCategory.LIVING_ART -> {
                        viewModel.selectCategory(category)
                        navController.navigate(AppRoute.Chapters.route) { launchSingleTop = true }
                      }
                    }
                  },
                  onOpenAllChapters = { navigateMenuRoute(AppRoute.Chapters.route) },
                  onOpenAuthorSection = { navigateMenuRoute(AppRoute.SettingsAuthor.route) },
                )
              }

              composable(AppRoute.BookContents.route) {
                BookContentsScreen(
                  uiState = uiState,
                  onOpenChapterSection = ::openChapterReader,
                )
              }

              composable(AppRoute.Chapters.route) {
                ChaptersScreen(
                  uiState = uiState,
                  onSelectCategory = viewModel::selectCategory,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                  onToggleBookmark = viewModel::toggleBookmark,
                )
              }

              composable(AppRoute.Health.route) {
                ChaptersScreen(
                  uiState =
                    uiState.copy(selectedCategory = ContentCategory.HEALTH_WELLBEING),
                  onSelectCategory = { cat ->
                    viewModel.selectCategory(cat)
                    if (cat != ContentCategory.HEALTH_WELLBEING) {
                      navController.navigate(AppRoute.Chapters.route) { launchSingleTop = true }
                    }
                  },
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                  onToggleBookmark = viewModel::toggleBookmark,
                )
              }

              composable(AppRoute.Poetry.route) {
                PoetryScreen(
                  uiState = uiState,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                )
              }

              composable(AppRoute.Quranic.route) {
                QuranicScreen(
                  uiState = uiState,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                )
              }

              composable(AppRoute.Search.route) {
                SearchScreen(
                  uiState = uiState,
                  onQueryChange = viewModel::updateSearchQuery,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                )
              }

              composable(AppRoute.Library.route) {
                LibraryScreen(
                  uiState = uiState,
                  onOpenChapter = { id -> openChapterReader(id, 0) },
                  onToggleBookmark = viewModel::toggleBookmark,
                  onSaveNote = viewModel::saveNote,
                  onDeleteNote = viewModel::deleteNote,
                )
              }

              composable(AppRoute.SettingsAuthor.route) {
                SettingsAuthorScreen(
                  uiState = uiState,
                  onThemeModeChange = viewModel::setThemeMode,
                  onFontScaleChange = viewModel::setFontScale,
                  onLargeTextChange = viewModel::setLargeText,
                  onHighContrastChange = viewModel::setHighContrast,
                  onReducedMotionChange = viewModel::setReducedMotion,
                  onFocusModeDefaultChange = viewModel::setFocusModeDefault,
                  onLanguageChange = viewModel::setLanguage,
                )
              }

              composable(
                route = AppRoute.Reader.route,
                arguments =
                  listOf(
                    navArgument("chapterId") { type = NavType.StringType },
                    navArgument("sectionIndex") {
                      type = NavType.IntType
                      defaultValue = 0
                    },
                  ),
              ) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: "ch_01"
                val sectionIndex = backStackEntry.arguments?.getInt("sectionIndex") ?: 0
                val chapter =
                  uiState.chapters.find { it.id == chapterId } ?: uiState.chapters.first()
                ReaderScreen(
                  chapter = chapter,
                  initialSectionIndex = sectionIndex,
                  uiState = uiState,
                  transcript = viewModel.getTranscript(chapter),
                  onBack = {
                    viewModel.stopAudio()
                    navController.popBackStack()
                  },
                  onOpenBookContents = {
                    viewModel.stopAudio()
                    navigateMenuRoute(AppRoute.BookContents.route)
                  },
                  onToggleBookmark = { viewModel.toggleBookmark(chapter.id) },
                  onToggleCompleted = { viewModel.toggleChapterCompleted(chapter.id) },
                  onSaveNote = { content -> viewModel.saveNote(chapter.id, content) },
                  onToggleFocusMode = viewModel::toggleFocusModeInReader,
                  onFontScaleChange = viewModel::setFontScale,
                  onToggleAudio = { viewModel.toggleAudioNarration(chapter) },
                  onChangeAudioSpeed = viewModel::setAudioSpeed,
                  onNavigateChapter = { targetId ->
                    viewModel.stopAudio()
                    viewModel.openChapter(targetId)
                    navController.navigate(AppRoute.Reader.create(targetId, 0)) {
                      popUpTo(AppRoute.Reader.route) { inclusive = true }
                    }
                  },
                )
              }
            }
          }
        }

        if (permanentDrawer && !isReaderRoute) {
          PermanentNavigationDrawer(
            drawerContent = {
              PermanentDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                drawerContent(false)
              }
            },
            content = mainScaffold,
          )
        } else {
          ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isReaderRoute,
            drawerContent = {
              ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                drawerContent(true)
              }
            },
            content = mainScaffold,
          )
        }
      }
    }
  }
}
