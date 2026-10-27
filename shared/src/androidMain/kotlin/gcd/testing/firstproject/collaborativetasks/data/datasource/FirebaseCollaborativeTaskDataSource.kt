package gcd.testing.firstproject.collaborativetasks.data.datasource

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import gcd.testing.firstproject.collaborativetasks.data.mapper.TaskDocumentMapper
import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseCollaborativeTaskDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    realtimeDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(),
) : CollaborativeTaskDataSource {
    private val tasksCollection = firestore.collection("tareas")
    private val completedCounter: DatabaseReference = realtimeDatabase.getReference("stats/completadas")

    override fun observeTasks(): Flow<List<CollaborativeTask>> = callbackFlow {
        val registration = tasksCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            try {
                val tasks = snapshot.documents.map { document ->
                    TaskDocumentMapper.from(
                        documentId = document.id,
                        fields = mapOf(
                            "titulo" to document.getString("titulo"),
                            "completada" to document.getBoolean("completada"),
                        ),
                    )
                }
                trySend(tasks)
            } catch (exception: Exception) {
                close(exception)
            }
        }
        awaitClose { registration.remove() }
    }

    override fun observeCompletedCount(): Flow<Int> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = (snapshot.value as? Number)?.toInt()?.coerceAtLeast(0) ?: 0
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        completedCounter.addValueEventListener(listener)
        awaitClose { completedCounter.removeEventListener(listener) }
    }

    override suspend fun setTaskCompletion(
        taskId: String,
        isCompleted: Boolean,
    ): CompletionChange = firestore.runTransaction { transaction ->
        val taskReference = tasksCollection.document(taskId)
        val snapshot = transaction.get(taskReference)
        check(snapshot.exists()) { "Task $taskId does not exist" }
        val previousCompleted = snapshot.getBoolean("completada")
            ?: throw IllegalArgumentException("Task $taskId has no valid completada field")

        if (previousCompleted == isCompleted) {
            CompletionChange.Unchanged
        } else {
            transaction.update(taskReference, "completada", isCompleted)
            CompletionChange.Updated(previousCompleted)
        }
    }.awaitResult()

    override suspend fun adjustCompletedCount(delta: Int) {
        if (delta == 0) return

        suspendCancellableCoroutine { continuation ->
            completedCounter.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentCount = (currentData.value as? Number)?.toInt() ?: 0
                    currentData.value = (currentCount + delta).coerceAtLeast(0)
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?,
                ) {
                    if (!continuation.isActive) return
                    when {
                        error != null -> continuation.resumeWithException(error.toException())
                        committed -> continuation.resume(Unit)
                        else -> continuation.resumeWithException(
                            IllegalStateException("RTDB completed-count transaction was not committed"),
                        )
                    }
                }
            })
        }
    }

    override suspend fun restoreTaskCompletion(
        taskId: String,
        expectedCurrent: Boolean,
        previousCompleted: Boolean,
    ): Boolean = firestore.runTransaction { transaction ->
        val taskReference = tasksCollection.document(taskId)
        val snapshot = transaction.get(taskReference)
        if (!snapshot.exists() || snapshot.getBoolean("completada") != expectedCurrent) {
            false
        } else {
            transaction.update(taskReference, "completada", previousCompleted)
            true
        }
    }.awaitResult()
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        if (continuation.isActive) continuation.resume(result)
    }
    addOnFailureListener { exception ->
        if (continuation.isActive) continuation.resumeWithException(exception)
    }
    addOnCanceledListener {
        if (continuation.isActive) continuation.cancel()
    }
}
