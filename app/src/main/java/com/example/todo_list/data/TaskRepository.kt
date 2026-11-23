package com.example.todo_list.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class TaskRepository internal constructor(private val dao: TaskDao) {
    constructor(context: Context) : this(TaskDatabase.getInstance(context).taskDao())

    fun observeTasks(): Flow<List<Task>> = dao.observeAll()

    suspend fun findTask(id: Long): Task? = dao.findById(id)

    /** Returns the ID assigned to the new task. */
    suspend fun addTask(title: String, description: String = ""): Long =
        dao.insert(Task(title = validatedTitle(title), description = description))

    /** Returns false if the task no longer exists. */
    suspend fun updateTask(id: Long, title: String, description: String): Boolean =
        dao.updateDetails(id, validatedTitle(title), description) > 0

    /** Returns false if the task no longer exists. */
    suspend fun setCompleted(id: Long, isCompleted: Boolean): Boolean =
        dao.setCompleted(id, isCompleted) > 0

    /** Returns false if the task no longer exists. */
    suspend fun deleteTask(id: Long): Boolean = dao.delete(id) > 0

    private fun validatedTitle(title: String): String {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "Task title must not be blank" }
        return trimmed
    }
}
