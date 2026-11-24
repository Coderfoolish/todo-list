package com.example.todo_list.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class TaskRepositoryTest {
    @Test
    fun editsKeepCompletionAndDeletedTasksDisappear() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java).build()
        try {
            withTimeout(5_000.milliseconds) {
                val repository = TaskRepository(database.taskDao())
                val id = repository.addTask("  Mua sách  ")
                assertEquals(Task(id, "Mua sách"), repository.observeTasks().first().single())

                assertTrue(repository.setCompleted(id, true))
                assertTrue(repository.updateTask(id, "Đọc sách", "Chương 1"))
                assertEquals(Task(id, "Đọc sách", "Chương 1", true), repository.findTask(id))

                assertTrue(repository.setCompleted(id, false))
                assertEquals(Task(id, "Đọc sách", "Chương 1"), repository.findTask(id))
                assertTrue(repository.deleteTask(id))
                assertNull(repository.findTask(id))
                assertTrue(repository.observeTasks().first().isEmpty())
                assertFalse(repository.deleteTask(id))
            }
        } finally {
            database.close()
        }
    }
}
