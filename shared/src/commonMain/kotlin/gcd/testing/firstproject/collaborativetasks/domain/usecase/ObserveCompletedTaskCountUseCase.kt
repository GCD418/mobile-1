package gcd.testing.firstproject.collaborativetasks.domain.usecase

import gcd.testing.firstproject.collaborativetasks.domain.repository.CollaborativeTaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveCompletedTaskCountUseCase(
    private val repository: CollaborativeTaskRepository,
) {
    operator fun invoke(): Flow<Int> = repository.observeCompletedCount()
}
