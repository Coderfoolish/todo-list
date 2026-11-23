package com.example.todo_list.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todo_list.R
import com.example.todo_list.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskListScreen(
    state: TaskListUiState,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onRetry: () -> Unit,
    onCompletedChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var taskToDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    val items = (state.content as? TaskListContent.Tasks)?.items.orEmpty()
    val taskToDelete = items.firstOrNull { it.id == taskToDeleteId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_list_title)) },
                actions = {
                    FilledTonalButton(onClick = onAdd, modifier = Modifier.padding(end = 12.dp)) {
                        Text(stringResource(R.string.task_add))
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.isUpdating) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.actionError?.let { TaskError(it, Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }

            when (val content = state.content) {
                TaskListContent.Loading -> TaskProgress(R.string.tasks_loading, Modifier.weight(1f))
                TaskListContent.Empty -> TaskMessage(R.string.tasks_empty, Modifier.weight(1f))
                TaskListContent.LoadError -> TaskMessage(
                    message = R.string.tasks_load_error,
                    modifier = Modifier.weight(1f),
                    onRetry = onRetry,
                )
                is TaskListContent.Tasks -> LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(content.items, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            actionsEnabled = !state.isUpdating,
                            onEdit = { onEdit(task.id) },
                            onCompletedChange = { onCompletedChange(task.id, it) },
                            onDelete = { taskToDeleteId = task.id },
                        )
                    }
                }
            }
        }
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDeleteId = null },
            title = { Text(stringResource(R.string.task_delete_title)) },
            text = { Text(stringResource(R.string.task_delete_confirmation, taskToDelete.title)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDeleteId = null
                        onDelete(taskToDelete.id)
                    },
                ) { Text(stringResource(R.string.task_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { taskToDeleteId = null }) { Text(stringResource(R.string.task_cancel)) }
            },
        )
    }
}

@Composable
private fun TaskRow(
    task: Task,
    actionsEnabled: Boolean,
    onEdit: () -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    val completionLabel = stringResource(R.string.task_completion_label, task.title)
    val deleteLabel = stringResource(R.string.task_delete_label, task.title)
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCompletedChange,
                enabled = actionsEnabled,
                modifier = Modifier.semantics { contentDescription = completionLabel },
            )
            Column(Modifier.weight(1f).clickable(onClick = onEdit).padding(vertical = 12.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            TextButton(
                onClick = onDelete,
                enabled = actionsEnabled,
                modifier = Modifier.semantics { contentDescription = deleteLabel },
            ) { Text(stringResource(R.string.task_delete)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskFormScreen(
    state: TaskFormUiState,
    isEditing: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val heading = if (isEditing) R.string.task_edit_title else R.string.task_add_title
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(heading)) },
                actions = {
                    TextButton(onClick = onBack, enabled = !state.blocksBack) {
                        Text(stringResource(R.string.task_cancel))
                    }
                },
            )
        },
    ) { padding ->
        val content = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding()
        when (state) {
            TaskFormUiState.Loading -> TaskProgress(R.string.task_loading, content)
            is TaskFormUiState.LoadError -> TaskMessage(
                message = state.message,
                modifier = content,
                onRetry = onRetry.takeIf { state.canRetry },
            )
            is TaskFormUiState.Editing -> Column(
                modifier = content.verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    isError = state.hasTitleError,
                    supportingText = if (state.hasTitleError) {
                        { TaskError(R.string.task_title_required) }
                    } else null,
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    label = { Text(stringResource(R.string.task_description)) },
                    minLines = 3,
                )
                state.saveError?.let { TaskError(it) }
                Button(onClick = onSave, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(if (state.isSaving) R.string.task_saving else R.string.task_save))
                }
                if (state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            TaskFormUiState.Saved -> TaskProgress(R.string.task_saved, content)
        }
    }
}

@Composable
private fun TaskError(@StringRes message: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(message),
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
    )
}

@Composable
private fun TaskProgress(@StringRes message: Int, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator()
            Text(stringResource(message))
        }
    }
}

@Composable
private fun TaskMessage(
    @StringRes message: Int,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(message), textAlign = TextAlign.Center)
            if (onRetry != null) {
                Button(onClick = onRetry) { Text(stringResource(R.string.task_retry)) }
            }
        }
    }
}
