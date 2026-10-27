package gcd.testing.firstproject.collaborativetasks.data.datasource

import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask
import kotlinx.coroutines.flow.Flow

sealed interface CompletionChange {
    data object Unchanged : CompletionChange
    data class Updated(val previousCompleted: Boolean) : CompletionChange
}

interface CollaborativeTaskDataSource {
    fun observeTasks(): Flow<List<CollaborativeTask>>

    fun observeCompletedCount(): Flow<Int>

    suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean): CompletionChange

    suspend fun adjustCompletedCount(delta: Int)

    suspend fun restoreTaskCompletion(
        taskId: String,
        expectedCurrent: Boolean,
        previousCompleted: Boolean,
    ): Boolean
}
