package com.example.todo_list.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun findById(id: Long): Task?

    @Insert
    suspend fun insert(task: Task): Long

    @Query("UPDATE tasks SET title = :title, description = :description WHERE id = :id")
    suspend fun updateDetails(id: Long, title: String, description: String): Int

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setCompleted(id: Long, isCompleted: Boolean): Int

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long): Int
}
