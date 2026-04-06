package com.example.mypracticeapplication

import android.app.Application
import com.example.mypracticeapplication.utils.DataStoreManager

class MyApp : Application() {
    lateinit var dataStoreManager: DataStoreManager
        private set

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = DataStoreManager(this)
    }
}
