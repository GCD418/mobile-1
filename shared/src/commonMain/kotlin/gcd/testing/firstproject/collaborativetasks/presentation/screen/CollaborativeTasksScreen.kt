package gcd.testing.firstproject.collaborativetasks.presentation.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gcd.testing.firstproject.collaborativetasks.presentation.viewmodel.CollaborativeTasksEvent
import gcd.testing.firstproject.collaborativetasks.presentation.viewmodel.CollaborativeTasksViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollaborativeTasksScreen(viewModel: CollaborativeTasksViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val animatedCount by animateIntAsState(
        targetValue = state.completedCount,
        label = "completed-task-count",
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Text("Collaborative tasks", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Completed: $animatedCount", style = MaterialTheme.typography.titleMedium)
        Button(
            onClick = { viewModel.emitEvent(CollaborativeTasksEvent.ToggleNotificationMute) },
        ) {
            Text(if (state.notificationsMuted) "Activar notificaciones" else "Silenciar notificaciones")
        }
        Spacer(modifier = Modifier.height(16.dp))

        state.error?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.tasks.isEmpty()) {
            Text("No tasks yet.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = state.tasks, key = { task -> task.id }) { task ->
                    val isPending = task.id in state.pendingTaskIds
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                enabled = !isPending,
                                onCheckedChange = { isCompleted ->
                                    viewModel.emitEvent(
                                        CollaborativeTasksEvent.CompletionChanged(
                                            taskId = task.id,
                                            isCompleted = isCompleted,
                                        ),
                                    )
                                },
                            )
                            Text(task.title, modifier = Modifier.weight(1f))
                            if (isPending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
