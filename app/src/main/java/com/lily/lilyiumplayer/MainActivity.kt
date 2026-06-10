package com.lily.lilyiumplayer

import MiniPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lily.lilyiumplayer.pages.FavoritePage

import com.lily.lilyiumplayer.pages.HomePage
import com.lily.lilyiumplayer.pages.InsideAlbumPage
import com.lily.lilyiumplayer.pages.LoadingScreenPage
import com.lily.lilyiumplayer.pages.NoServerPage
import com.lily.lilyiumplayer.pages.NowPlayingPage
import com.lily.lilyiumplayer.pages.ProfilePage
import com.lily.lilyiumplayer.pages.SearchPage
import com.lily.lilyiumplayer.pages.SongPage
import com.lily.lilyiumplayer.player.AudioPlayerManager
import com.lily.lilyiumplayer.ui.components.AddServerDialog
import com.lily.lilyiumplayer.ui.components.TopBar
import com.lily.lilyiumplayer.ui.theme.LilyiumTheme
import com.lily.lilyiumplayer.viewModel.SessionAction
import com.lily.lilyiumplayer.viewModel.SessionState
import com.lily.lilyiumplayer.viewModel.SessionViewModel
import com.lilyiumplayer.ui.albums.AlbumsPage
import com.lilyiumplayer.ui.artist.ArtistDetailPage
import com.lilyiumplayer.ui.artist.ArtistPage

class MainActivity : ComponentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        AudioPlayerManager.init(this)

        enableEdgeToEdge()

        setContent {
            LilyiumTheme {
                AppNavigation(sessionViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AudioPlayerManager.release()
    }

    @Composable
    fun AppNavigation(viewModel: SessionViewModel) {
        val state by viewModel.state.collectAsState()
        val navController = rememberNavController()

        when {
            state.isLoading -> {}
//            !state.isLoggedIn -> LoginPage()
            !state.isLoggedIn -> {
                NoServerPage(
                    onAddServer = { viewModel.onAction(SessionAction.ShowAddServerDialog) }
                )
                if (state.showAddServerDialog) {
                    AddServerDialog(
                        isLoading = state.isAddingServer,
                        errorMessage = state.addServerError,
                        onDismiss = { viewModel.onAction(SessionAction.DismissAddServerDialog) },
                        onSubmit = { label, server, username, password ->
                            viewModel.onAction(
                                SessionAction.SubmitAddServer(label, server, username, password)
                            )
                        }
                    )
                }
            }
            else -> LilyiumApp(
                sessionState = state,
                onSessionAction = viewModel::onAction,
                navController = navController
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LilyiumApp(
        sessionState: SessionState,
        onSessionAction: (SessionAction) -> Unit,
        navController: NavHostController = rememberNavController()
    ) {
        val navController = rememberNavController()

//    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val currentTab = AppDestinations.bottomTabs.find { it.route == currentRoute }

        // TopBar only on these routes
        val topBarRoutes = setOf(
            AppDestinations.HOME.route,
            AppDestinations.ALBUMS.route,
            AppDestinations.SONGS.route,
            AppDestinations.ARTISTS.route,
//            AppDestinations.LIBRARY.route,
        )
        val showTopBar = currentRoute in topBarRoutes

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.bottomTabs.forEach {
                    val selected = it == currentTab

                    item(
                        label = { Text(it.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(it.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                if (selected) it.selectedIcon else it.icon,
                                contentDescription = it.label
                            )
                        }
                    )
                }
            }
        ) {
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {

                Scaffold(
                    bottomBar = {
                        if (AudioPlayerManager.hasSong && currentRoute != "now_playing") {
                            MiniPlayer(
                                onClick = {
                                    navController.navigate("now_playing")
                                },
                            )
//                            MiniPlayer(
//                                title = AudioPlayerManager.currentTitle ?: "",
//                                artist = AudioPlayerManager.currentArtist ?: "",
//                                coverUrl = AudioPlayerManager.currentCoverUrl,
//                                isPlaying = AudioPlayerManager.isPlaying,
//                                onPlayPause = {
//                                    AudioPlayerManager.togglePlayPause()
//                                },
//                                onClick = {
//                                    navController.navigate("now_playing")
//                                }
//                            )
                        }
                    }
                ) { padding ->

                    NavHost(
                        navController = navController,
                        startDestination = AppDestinations.HOME.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("favorites") {
                            FavoritePage(modifier = Modifier.fillMaxSize(), onBackClick = {(navController.navigateUp())})
                        }

                        composable(AppDestinations.HOME.route) {
                            HomePage(
                                Modifier.fillMaxSize(),
                                navController
                            )
                        }
                        composable(AppDestinations.ALBUMS.route) {
                            AlbumsPage(
                                onAlbumClick = { albumId ->
                                    navController.navigate("albumDetail/$albumId")
                                }
                            )
                        }
                        composable(AppDestinations.SONGS.route) {
                            SongPage()
                        }
                        composable(AppDestinations.ARTISTS.route) {
                            ArtistPage(
                                onArtistClick = { artistId ->
                                    navController.navigate("artistDetail/$artistId")
                                }
                            )
                        }
//                        composable(AppDestinations.LIBRARY.route) {
//                            Text("Library")
//                        }

                        composable(
                            "albumDetail/{albumId}")
                        { backStackEntry ->
                            val albumId = backStackEntry.arguments?.getString("albumId") ?: return@composable
                            InsideAlbumPage(
                                albumId = albumId,
                                onBackClick = { navController.navigateUp() }
                            )
                        }

                        // InsideAlbumPage since artist pages work the same way
                        composable(
                            route = "artistDetail/{artistId}",     // "artistDetail/{artistId}"
                        ) { backStackEntry ->
                            val artistId = backStackEntry.arguments?.getString("artistId") ?: return@composable
                            ArtistDetailPage(
                                artistId = artistId,
                                onAlbumClick = { albumId ->
                                    navController.navigate("albumDetail/$albumId")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("search") {
                            SearchPage(
                                onBackClick = { navController.navigateUp() },
                                navController = navController
                            )
                        }
                        composable("settings") { Text("Settings") }
                        composable("profile") { ProfilePage() }

                        composable("now_playing") {
                            NowPlayingPage(
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                    }
                    if (showTopBar) {
                        TopBar(
                            modifier = Modifier.align(Alignment.TopEnd),
                            session = sessionState,
                            onAction = onSessionAction,
                            onSearchClick = { navController.navigate("search") },
                            onBackClick = { navController.navigateUp() },
                            onSettingsClick = { navController.navigate("settings") },
                        )
                    }


                }
            }
        }
    }


    sealed class AppDestinations(
        val route: String,
        val label: String,
        val icon: ImageVector,
        val selectedIcon: ImageVector
    ) {

        object HOME : AppDestinations(
            "home",
            "Home",
            Icons.Outlined.Home,
            Icons.Filled.Home
        )

        object ALBUMS : AppDestinations(
            "albums",
            "Albums",
            Icons.Outlined.Album,
            Icons.Filled.Album
        )

        object SONGS : AppDestinations(
            "songs",
            "Songs",
            Icons.Outlined.MusicNote,
            Icons.Filled.MusicNote
        )

        object ARTISTS : AppDestinations(
            "artists",
            "Artist",
            Icons.Outlined.Groups,
            Icons.Filled.Groups
        )

//        object LIBRARY : AppDestinations(
//            "library",
//            "Library",
//            Icons.Outlined.LibraryMusic,
//            Icons.Filled.LibraryMusic
//        )

        companion object {
            val bottomTabs = listOf(HOME, ALBUMS, SONGS, ARTISTS)
//            val bottomTabs = listOf(HOME, ALBUMS, SONGS, ARTISTS, LIBRARY)
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        LilyiumTheme {
            Greeting("Android")
        }
    }
}

