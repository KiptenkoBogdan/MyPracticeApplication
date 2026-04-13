package com.example.mypracticeapplication.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mypracticeapplication.model.UserDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object {
        val EMAIL = stringPreferencesKey("EMAIL")
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val DISPLAY_NAME = stringPreferencesKey("DISPLAY_NAME")
        val PROFILE_PICTURE_URI = stringPreferencesKey("PROFILE_PICTURE_URI")
        val LIKE_COUNTS = stringPreferencesKey("LIKE_COUNTS")
        val ACCOUNTS = stringPreferencesKey("ACCOUNTS")

        fun favouritesKey(email: String) = stringPreferencesKey("FAVOURITES_$email")
        fun userLikesKey(email: String) = stringPreferencesKey("USER_LIKES_$email")
    }

    // --- Auth ---

    suspend fun saveToDataStore(userDetails: UserDetails) {
        context.preferenceDataStore.edit {
            it[EMAIL] = userDetails.email
            it[PASSWORD] = userDetails.password
            it[DISPLAY_NAME] = userDetails.displayName
            it[PROFILE_PICTURE_URI] = userDetails.profilePictureUri
        }
    }

    // --- Accounts (multi-user registry) ---

    fun getAccounts(): Flow<List<UserDetails>> =
        context.preferenceDataStore.data
            .map { it[ACCOUNTS] ?: "[]" }
            .distinctUntilChanged()
            .map { json ->
                try {
                    Json.decodeFromString<List<UserDetails>>(json)
                } catch (e: Exception) {
                    emptyList()
                }
            }

    suspend fun findAccount(email: String): UserDetails? {
        val prefs = context.preferenceDataStore.data.first()
        val list = try {
            Json.decodeFromString<List<UserDetails>>(prefs[ACCOUNTS] ?: "[]")
        } catch (e: Exception) {
            emptyList()
        }
        return list.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    suspend fun registerAccount(newUser: UserDetails): Boolean {
        var success = true
        context.preferenceDataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<UserDetails>>(prefs[ACCOUNTS] ?: "[]")
            } catch (e: Exception) {
                emptyList()
            }
            if (current.any { it.email.equals(newUser.email, ignoreCase = true) }) {
                success = false
                return@edit
            }
            prefs[ACCOUNTS] = Json.encodeToString(current + newUser)
        }
        return success
    }

    suspend fun updateAccount(email: String, transform: (UserDetails) -> UserDetails) {
        context.preferenceDataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<UserDetails>>(prefs[ACCOUNTS] ?: "[]")
            } catch (e: Exception) {
                return@edit
            }
            val updated = current.map {
                if (it.email.equals(email, ignoreCase = true)) transform(it) else it
            }
            prefs[ACCOUNTS] = Json.encodeToString(updated)
        }
    }

    fun getFromDataStore(): Flow<UserDetails> = context.preferenceDataStore.data.map {
        UserDetails(
            email = it[EMAIL] ?: "",
            password = it[PASSWORD] ?: "",
            displayName = it[DISPLAY_NAME] ?: "",
            profilePictureUri = it[PROFILE_PICTURE_URI] ?: ""
        )
    }.distinctUntilChanged()

    fun isLoggedIn(): Flow<Boolean> = context.preferenceDataStore.data
        .map { (it[EMAIL] ?: "").isNotBlank() }
        .distinctUntilChanged()

    fun getLoggedInEmail(): Flow<String> = context.preferenceDataStore.data
        .map { it[EMAIL] ?: "" }
        .distinctUntilChanged()

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.remove(EMAIL)
        it.remove(PASSWORD)
        it.remove(DISPLAY_NAME)
        it.remove(PROFILE_PICTURE_URI)
    }

    // --- Profile ---

    suspend fun saveDisplayName(name: String) {
        context.preferenceDataStore.edit { it[DISPLAY_NAME] = name }
    }

    suspend fun saveProfilePictureUri(uri: String) {
        context.preferenceDataStore.edit { it[PROFILE_PICTURE_URI] = uri }
    }

    // --- Favourites (per-user) ---

    fun getFavourites(email: String): Flow<List<String>> =
        context.preferenceDataStore.data
            .map { it[favouritesKey(email)] ?: "[]" }
            .distinctUntilChanged()
            .map { json ->
                try {
                    Json.decodeFromString<List<String>>(json)
                } catch (e: Exception) {
                    emptyList()
                }
            }

    suspend fun toggleFavourite(email: String, filename: String) {
        context.preferenceDataStore.edit { prefs ->
            val key = favouritesKey(email)
            val current = try {
                Json.decodeFromString<List<String>>(prefs[key] ?: "[]")
            } catch (e: Exception) {
                emptyList()
            }
            val updated = if (filename in current) current - filename else current + filename
            prefs[key] = Json.encodeToString(updated)
        }
    }

    // --- Like counts (global) ---

    fun getLikeCounts(): Flow<Map<String, Int>> =
        context.preferenceDataStore.data
            .map { it[LIKE_COUNTS] ?: "{}" }
            .distinctUntilChanged()
            .map { json ->
                try {
                    Json.decodeFromString<Map<String, Int>>(json)
                } catch (e: Exception) {
                    emptyMap()
                }
            }

    suspend fun initLikeCounts(videos: List<String>) {
        context.preferenceDataStore.edit { prefs ->
            val existing = try {
                Json.decodeFromString<Map<String, Int>>(prefs[LIKE_COUNTS] ?: "{}")
            } catch (e: Exception) {
                emptyMap()
            }
            val updated = existing.toMutableMap()
            for (video in videos) {
                if (video !in updated) {
                    updated[video] = (100..30000).random()
                }
            }
            prefs[LIKE_COUNTS] = Json.encodeToString(updated)
        }
    }

    // --- User likes (per-user) ---

    fun getUserLikes(email: String): Flow<Set<String>> =
        context.preferenceDataStore.data
            .map { it[userLikesKey(email)] ?: "[]" }
            .distinctUntilChanged()
            .map { json ->
                try {
                    Json.decodeFromString<List<String>>(json).toSet()
                } catch (e: Exception) {
                    emptySet()
                }
            }

    suspend fun toggleLike(email: String, filename: String) {
        context.preferenceDataStore.edit { prefs ->
            // Toggle per-user like
            val likesKey = userLikesKey(email)
            val currentLikes = try {
                Json.decodeFromString<List<String>>(prefs[likesKey] ?: "[]").toMutableSet()
            } catch (e: Exception) {
                mutableSetOf()
            }

            val wasLiked = filename in currentLikes

            // Update global count
            val counts = try {
                Json.decodeFromString<Map<String, Int>>(prefs[LIKE_COUNTS] ?: "{}").toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }

            if (wasLiked) {
                currentLikes.remove(filename)
                counts[filename] = (counts[filename] ?: 1) - 1
            } else {
                currentLikes.add(filename)
                counts[filename] = (counts[filename] ?: 0) + 1
            }

            prefs[likesKey] = Json.encodeToString(currentLikes.toList())
            prefs[LIKE_COUNTS] = Json.encodeToString(counts)
        }
    }
}
