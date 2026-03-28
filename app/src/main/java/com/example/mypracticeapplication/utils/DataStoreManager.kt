package com.example.mypracticeapplication.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mypracticeapplication.model.UserDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val USER_DATASTORE = "user_data"

val Context.preferenceDataStore : DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE)

class DataStoreManager(val context: Context) {

    companion object{

        val EMAIL = stringPreferencesKey("EMAIL")
        val PASSWORD = stringPreferencesKey("PASSWORD")
        //val USERNAME = stringPreferencesKey("NAME")
    }

    suspend fun saveToDataStore(userDetails: UserDetails){
        context.preferenceDataStore.edit {
            it[EMAIL] = userDetails.email
            it[PASSWORD] = userDetails.password
            //it[USERNAME] = userDetails.userName
        }
    }

    fun getFromDataStore() = context.preferenceDataStore.data.map {
        UserDetails(
            email = it[EMAIL] ?: "",
            password = it[PASSWORD] ?: ""
            //userName = it[USERNAME] ?: ""
        )
    }

    suspend fun clearDataStore() = context.preferenceDataStore.edit {
        it.clear()
    }
}