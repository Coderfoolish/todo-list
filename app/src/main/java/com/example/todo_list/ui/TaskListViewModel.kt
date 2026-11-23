package com.example.todo_list.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.R
import com.example.todo_list.data.Task
import com.example.todo_list.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal sealed interface TaskListContent {
    data object Loading : TaskListContent
    data object Empty : TaskListContent
    data object LoadError : TaskListContent
    data class Tasks(val items: List<Task>) : TaskListContent
}

internal data class TaskListUiState(
    val content: TaskListContent = TaskListContent.Loading,
    val isUpdating: Boolean = false,
    @get:StringRes val actionError: Int? = null,
)

internal class TaskListViewModel(private val repository: TaskRepository) : ViewModel() {
    private val state = MutableStateFlow(TaskListUiState())
    val uiState = state.asStateFlow()

    init {
        observeTasks()
    }

    fun retryLoad() {
        if (state.value.content == TaskListContent.LoadError) observeTasks()
    }

    fun setCompleted(id: Long, completed: Boolean) {
        updateTask(R.string.task_status_error) { repository.setCompleted(id, completed) }
    }

    fun deleteTask(id: Long) {
        updateTask(R.string.task_delete_error) { repository.deleteTask(id) }
    }

    private fun observeTasks() {
        state.update { it.copy(content = TaskListContent.Loading, actionError = null) }
        viewModelScope.launch {
            try {
                repository.observeTasks().collect { tasks ->
                    val content = if (tasks.isEmpty()) TaskListContent.Empty else TaskListContent.Tasks(tasks)
                    state.update { it.copy(content = content) }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                state.update { it.copy(content = TaskListContent.LoadError) }
            }
        }
    }

    private fun updateTask(@StringRes failureMessage: Int, action: suspend () -> Boolean) {
        if (state.value.isUpdating) return
        state.update { it.copy(isUpdating = true, actionError = null) }

        viewModelScope.launch {
            val error = try {
                if (action()) null else R.string.task_missing
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                failureMessage
            }
            state.update { it.copy(isUpdating = false, actionError = error) }
        }
    }
}
