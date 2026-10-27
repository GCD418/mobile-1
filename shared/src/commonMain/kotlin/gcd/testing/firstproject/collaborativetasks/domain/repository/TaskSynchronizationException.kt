package gcd.testing.firstproject.collaborativetasks.domain.repository

class TaskSynchronizationException(
    cause: Throwable,
    val compensationFailure: Throwable? = null,
) : Exception("Unable to synchronize task completion across Firestore and RTDB", cause)
