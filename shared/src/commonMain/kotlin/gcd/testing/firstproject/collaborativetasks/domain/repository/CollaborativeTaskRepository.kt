package gcd.testing.firstproject.collaborativetasks.domain.repository

import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask
import kotlinx.coroutines.flow.Flow

interface CollaborativeTaskRepository {
    fun observeTasks(): Flow<List<CollaborativeTask>>

    fun observeCompletedCount(): Flow<Int>

    suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean)
}
