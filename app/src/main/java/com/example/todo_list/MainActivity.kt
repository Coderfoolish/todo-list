package com.example.todo_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todo_list.data.TaskRepository
import com.example.todo_list.ui.TodoNavHost
import com.example.todo_list.ui.theme.TodolistTheme

class MainActivity : ComponentActivity() {
    private val repository by lazy { TaskRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodolistTheme {
                TodoNavHost(repository)
            }
        }
    }
}
