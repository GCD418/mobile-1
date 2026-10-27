package gcd.testing.firstproject.collaborativetasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.collaborativetasks.domain.usecase.GetTaskNotificationsMutedUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.ObserveCollaborativeTasksUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.ObserveCompletedTaskCountUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.SetCollaborativeTaskCompletionUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.SetTaskNotificationsMutedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CollaborativeTasksViewModel(
    private val observeTasks: ObserveCollaborativeTasksUseCase,
    private val observeCompletedCount: ObserveCompletedTaskCountUseCase,
    private val setTaskCompletion: SetCollaborativeTaskCompletionUseCase,
    private val getNotificationsMuted: GetTaskNotificationsMutedUseCase,
    private val setNotificationsMuted: SetTaskNotificationsMutedUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(
        CollaborativeTasksState(notificationsMuted = getNotificationsMuted()),
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(observeTasks(), observeCompletedCount()) { tasks, count -> tasks to count }
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unable to load collaborative tasks.",
                        )
                    }
                }
                .collect { (tasks, count) ->
                    _state.update {
                        it.copy(
                            tasks = tasks,
                            completedCount = count,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
        }
    }

    fun emitEvent(event: CollaborativeTasksEvent) {
        when (event) {
            CollaborativeTasksEvent.ToggleNotificationMute -> {
                val muted = !_state.value.notificationsMuted
                setNotificationsMuted(muted)
                _state.update { it.copy(notificationsMuted = muted) }
            }
            is CollaborativeTasksEvent.CompletionChanged -> {
                updateTaskCompletion(event.taskId, event.isCompleted)
            }
        }
    }

    private fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        if (taskId in _state.value.pendingTaskIds) return
        _state.update {
            it.copy(
                pendingTaskIds = it.pendingTaskIds + taskId,
                error = null,
            )
        }

        viewModelScope.launch {
            try {
                setTaskCompletion(taskId, isCompleted)
            } catch (exception: Exception) {
                _state.update {
                    it.copy(error = exception.message ?: "Unable to update task completion.")
                }
            } finally {
                _state.update { it.copy(pendingTaskIds = it.pendingTaskIds - taskId) }
            }
        }
    }
}
