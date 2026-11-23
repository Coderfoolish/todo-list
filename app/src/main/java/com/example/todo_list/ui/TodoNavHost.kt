package com.example.todo_list.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.lifecycle.withResumed
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.todo_list.data.TaskRepository
import kotlinx.serialization.Serializable

@Serializable
internal data object TaskListRoute : NavKey

@Serializable
internal data class TaskFormRoute(val taskId: Long? = null) : NavKey

@Composable
internal fun TodoNavHost(repository: TaskRepository, modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(TaskListRoute)

    NavDisplay(
        backStack = backStack,
        modifier = modifier.fillMaxSize(),
        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<TaskListRoute> {
                val model = viewModel { TaskListViewModel(repository) }
                val state by model.uiState.collectAsStateWithLifecycle()
                TaskListScreen(
                    state = state,
                    onAdd = {
                        if (backStack.lastOrNull() == TaskListRoute) backStack.add(TaskFormRoute())
                    },
                    onEdit = { id ->
                        if (backStack.lastOrNull() == TaskListRoute) backStack.add(TaskFormRoute(id))
                    },
                    onRetry = model::retryLoad,
                    onCompletedChange = model::setCompleted,
                    onDelete = model::deleteTask,
                )
            }
            entry<TaskFormRoute> { route ->
                val model = viewModel {
                    TaskFormViewModel(repository, route.taskId, createSavedStateHandle())
                }
                val state by model.uiState.collectAsStateWithLifecycle()
                val isCurrent = backStack.lastOrNull() == route
                val close: () -> Unit = {
                    if (backStack.lastOrNull() == route) backStack.removeLastOrNull()
                }
                val cancel = { if (!model.uiState.value.blocksBack) close() }
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                val saved = state == TaskFormUiState.Saved

                BackHandler(enabled = isCurrent, onBack = cancel)
                LaunchedEffect(saved, lifecycle) {
                    if (saved) lifecycle.withResumed { close() }
                }

                TaskFormScreen(
                    state = state,
                    isEditing = route.taskId != null,
                    onTitleChange = model::changeTitle,
                    onDescriptionChange = model::changeDescription,
                    onSave = model::save,
                    onRetry = model::retryLoad,
                    onBack = cancel,
                )
            }
        },
    )
}
