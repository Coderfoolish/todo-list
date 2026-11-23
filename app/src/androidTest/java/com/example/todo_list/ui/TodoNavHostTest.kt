package com.example.todo_list.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todo_list.R
import com.example.todo_list.data.Task
import com.example.todo_list.data.TaskDao
import com.example.todo_list.data.TaskDatabase
import com.example.todo_list.data.TaskRepository
import com.example.todo_list.ui.theme.TodolistTheme
import java.io.IOException
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoNavHostTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private var database: TaskDatabase? = null

    @After
    fun closeDatabase() {
        database?.close()
    }

    @Test
    fun retriesFailedLoadAndReturnsToListOnlyAfterSuccessfulSave() {
        val room = Room.inMemoryDatabaseBuilder(compose.activity, TaskDatabase::class.java).build()
        database = room
        val repository = TaskRepository(FailOnceTaskDao(room.taskDao()))
        compose.setContent { TodolistTheme { TodoNavHost(repository) } }

        awaitText(label(R.string.tasks_load_error))
        compose.onNodeWithText(label(R.string.task_retry)).performClick()
        awaitText(label(R.string.tasks_empty))

        compose.onNodeWithText(label(R.string.task_add)).performClick()
        compose.onNodeWithText(label(R.string.task_save)).performClick()
        awaitText(label(R.string.task_title_required))

        val title = "Mua sách"
        compose.onNodeWithText(label(R.string.task_title)).performTextInput(title)
        compose.onNodeWithText(label(R.string.task_save)).performClick()
        awaitText(label(R.string.task_save_error))
        compose.onNodeWithText(title).assertExists()
        compose.onNodeWithText(label(R.string.task_add_title)).assertExists()

        compose.onNodeWithText(label(R.string.task_save)).performClick()
        awaitText(label(R.string.task_list_title))
        awaitText(title)
        compose.onNodeWithText(label(R.string.task_save)).assertDoesNotExist()
    }

    private fun label(id: Int) = compose.activity.getString(id)

    private fun awaitText(text: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private class FailOnceTaskDao(private val delegate: TaskDao) : TaskDao by delegate {
        private var failLoad = true
        private var failInsert = true

        override fun observeAll() = flow {
            if (failLoad) {
                failLoad = false
                throw IOException("Test list load failure")
            }
            emitAll(delegate.observeAll())
        }

        override suspend fun insert(task: Task): Long {
            if (failInsert) {
                failInsert = false
                throw IOException("Test save failure")
            }
            return delegate.insert(task)
        }
    }
}
