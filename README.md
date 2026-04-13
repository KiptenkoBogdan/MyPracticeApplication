Project Overview

A simple practice TikTok-style Android app made with Jetpack Compose.

 File Descriptions

 Core
 - MainActivity.kt — Entry point that hosts the root composable.
 - MyApp.kt — Application class; initialises Hilt and configures Coil image loader with video-frame decoding.

 DI
 - di/AppModule.kt — Hilt module providing the DataStoreManager singleton.

 Models
 - model/Route.kt — Sealed class defining all navigation destinations.
 - model/UserDetails.kt — Data class for user account info (email, password, displayName, profilePictureUri).
 - model/VideoItem.kt — Data class representing a video (filename, creator, likeCount, isLiked, isFavourite).

 Utils
 - utils/DataStoreManager.kt — All DataStore read/write operations: auth, profile, likes, and favourites.

 Screens
 - view/HomeScreen.kt — Vertical-pager video feed; delegates state to HomeViewModel.
 - view/LoginScreen.kt — Email/password login form with animated error states and loading indicator.
 - view/SignUpScreen.kt — Registration form (username, email, password) with validation and error animations.
 - view/ProfileScreen.kt — Editable profile screen: profile picture, username, logout.
 - view/BookmarkScreen.kt — Grid of bookmarked videos; navigates into SavedVideoPlayerScreen.
 - view/SavedVideoPlayerScreen.kt — Full-screen player for saved videos; auto-exits if all videos are un-bookmarked.

 Components
 - view/components/VideoPlayer.kt — ExoPlayer composable; loads assets, manages lifecycle, loops playback.
 - view/components/VideoOverlay.kt — Like, bookmark, and share button overlay drawn on top of the video player.
 - view/BottomNavBar.kt — Reusable bottom navigation bar (Home / Bookmarks / Profile).

 ViewModels
 - viewmodel/HomeViewModel.kt — Loads video list from assets, manages likes/bookmarks, emits toast events.
 - viewmodel/LoginViewModel.kt — Validates credentials against DataStore; saves session on success.
 - viewmodel/SignUpViewModel.kt — Registers new accounts; prevents duplicate emails.
 - viewmodel/BookmarkViewModel.kt — Exposes current user's favourite video list reactively.
 - viewmodel/ProfileViewModel.kt — Reads/writes profile data (display name, photo URI) via DataStore.
 - viewmodel/SavedVideoPlayerViewModel.kt — State for saved-video player: like/bookmark interactions, toast events.
 - viewmodel/NavigationViewModel.kt — Observes login state; drives conditional navigation to Login vs main app.

 Navigation
 - viewmodel/BottomNavigation.kt — Root NavHost; wires up bottom bar, handles login-gated routing.
