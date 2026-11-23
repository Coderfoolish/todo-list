package com.example.todo_list.ui

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_list.R
import com.example.todo_list.data.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal sealed interface TaskFormUiState {
    data object Loading : TaskFormUiState
    data class LoadError(@get:StringRes val message: Int, val canRetry: Boolean) : TaskFormUiState
    data class Editing(
        val title: String,
        val description: String,
        val hasTitleError: Boolean = false,
        val isSaving: Boolean = false,
        @get:StringRes val saveError: Int? = null,
    ) : TaskFormUiState
    data object Saved : TaskFormUiState
}

internal val TaskFormUiState.blocksBack: Boolean
    get() = this == TaskFormUiState.Saved || (this is TaskFormUiState.Editing && isSaving)

internal class TaskFormViewModel(
    private val repository: TaskRepository,
    private val taskId: Long?,
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private val state = MutableStateFlow<TaskFormUiState>(
        if (taskId == null) draft() else TaskFormUiState.Loading,
    )
    val uiState = state.asStateFlow()

    init {
        if (taskId != null) loadTask(taskId)
    }

    fun retryLoad() {
        val error = state.value as? TaskFormUiState.LoadError ?: return
        if (error.canRetry && taskId != null) loadTask(taskId)
    }

    fun changeTitle(title: String) {
        val current = editableDraft() ?: return
        savedState[DraftTitle] = title
        state.value = current.copy(title = title, hasTitleError = false, saveError = null)
    }

    fun changeDescription(description: String) {
        val current = editableDraft() ?: return
        savedState[DraftDescription] = description
        state.value = current.copy(description = description, saveError = null)
    }

    fun save() {
        val current = editableDraft() ?: return
        val title = current.title.trim()
        if (title.isEmpty()) {
            state.value = current.copy(hasTitleError = true, saveError = null)
            return
        }

        state.value = current.copy(isSaving = true, saveError = null)
        viewModelScope.launch {
            state.value = try {
                val saved = if (taskId == null) {
                    repository.addTask(title, current.description)
                    true
                } else {
                    repository.updateTask(taskId, title, current.description)
                }
                if (saved) TaskFormUiState.Saved else current.copy(saveError = R.string.task_missing)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                current.copy(saveError = R.string.task_save_error)
            }
        }
    }

    private fun loadTask(id: Long) {
        state.value = TaskFormUiState.Loading
        viewModelScope.launch {
            state.value = try {
                val task = repository.findTask(id)
                if (task == null) {
                    TaskFormUiState.LoadError(R.string.task_missing, canRetry = false)
                } else {
                    draft(task.title, task.description)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                TaskFormUiState.LoadError(R.string.task_load_error, canRetry = true)
            }
        }
    }

    private fun draft(title: String = "", description: String = "") = TaskFormUiState.Editing(
        title = savedState[DraftTitle] ?: title,
        description = savedState[DraftDescription] ?: description,
    )

    private fun editableDraft(): TaskFormUiState.Editing? =
        (state.value as? TaskFormUiState.Editing)?.takeUnless { it.isSaving }

    private companion object {
        const val DraftTitle = "task_title"
        const val DraftDescription = "task_description"
    }
}
