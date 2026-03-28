package com.example.mypracticeapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.mypracticeapplication.ui.theme.MyPracticeApplicationTheme
import com.example.mypracticeapplication.utils.DataStoreManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPracticeApplicationTheme {
                val dataStoreManager = DataStoreManager(LocalContext.current)
                BottomNavigator(dataStoreManager)
            }
        }
    }
}

