package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.IconRepository
import com.example.ui.IconPackViewModel
import com.example.ui.IconPackViewModelFactory
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database, DAO and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = IconRepository(database.iconDao())

        // Create ViewModel Factory and ViewModel
        val factory = IconPackViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[IconPackViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

