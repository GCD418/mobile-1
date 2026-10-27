package gcd.testing.firstproject.collaborativetasks.data.repository

import gcd.testing.firstproject.collaborativetasks.data.datasource.CollaborativeTaskDataSource
import gcd.testing.firstproject.collaborativetasks.data.datasource.CompletionChange
import gcd.testing.firstproject.collaborativetasks.data.logging.TaskLogger
import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask
import gcd.testing.firstproject.collaborativetasks.domain.repository.CollaborativeTaskRepository
import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskSynchronizationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CollaborativeTaskRepositoryImpl(
    private val dataSource: CollaborativeTaskDataSource,
    private val logger: TaskLogger,
) : CollaborativeTaskRepository {
    override fun observeTasks(): Flow<List<CollaborativeTask>> = dataSource.observeTasks()

    override fun observeCompletedCount(): Flow<Int> = dataSource.observeCompletedCount()

    override suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean) = withContext(NonCancellable) {
        val change = try {
            dataSource.setTaskCompletion(taskId, isCompleted)
        } catch (firestoreFailure: Exception) {
            logger.error("Firestore failed to update task $taskId", firestoreFailure)
            throw TaskSynchronizationException(firestoreFailure)
        }

        if (change == CompletionChange.Unchanged) return@withContext
        change as CompletionChange.Updated

        val delta = if (isCompleted) 1 else -1
        try {
            dataSource.adjustCompletedCount(delta)
        } catch (counterFailure: Exception) {
            logger.error("RTDB failed to update the completed counter for task $taskId", counterFailure)
            val compensationFailure = try {
                val restored = dataSource.restoreTaskCompletion(
                    taskId = taskId,
                    expectedCurrent = isCompleted,
                    previousCompleted = change.previousCompleted,
                )
                if (restored) {
                    null
                } else {
                    IllegalStateException("Firestore rollback skipped because the task changed concurrently")
                }
            } catch (rollbackFailure: Exception) {
                rollbackFailure
            }

            if (compensationFailure != null) {
                logger.error("Firestore compensation failed for task $taskId", compensationFailure)
            }
            throw TaskSynchronizationException(counterFailure, compensationFailure)
        }
    }
}
