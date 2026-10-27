package gcd.testing.firstproject.collaborativetasks.data.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import gcd.testing.firstproject.collaborativetasks.domain.model.TaskNotificationContract
import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskNotificationPreferences

class FirebaseTaskAddedNotificationService(
    context: Context,
    private val preferences: TaskNotificationPreferences,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : TaskAddedNotificationService {
    private val appContext = context.applicationContext
    private var listenerRegistration: ListenerRegistration? = null

    @Volatile
    private var hasServerBaseline = false

    override fun start() {
        if (listenerRegistration != null) return
        createNotificationChannel()
        hasServerBaseline = false
        listenerRegistration = firestore.collection("tareas")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Task-added listener failed", error)
                    return@addSnapshotListener
                }
                if (snapshot == null || snapshot.metadata.isFromCache) return@addSnapshotListener

                if (!hasServerBaseline) {
                    hasServerBaseline = true
                    return@addSnapshotListener
                }

                snapshot.documentChanges
                    .filter { it.type == DocumentChange.Type.ADDED }
                    .forEach { change ->
                        if (!preferences.notificationsMuted()) {
                            postNewTaskNotification(
                                taskId = change.document.id,
                                title = change.document.getString("titulo") ?: "Tarea sin título",
                            )
                        }
                    }
            }
    }

    override fun stop() {
        listenerRegistration?.remove()
        listenerRegistration = null
        hasServerBaseline = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = appContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT),
        )
    }

    private fun postNewTaskNotification(taskId: String, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            appContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = appContext.getSystemService(NotificationManager::class.java)
        if (!manager.areNotificationsEnabled()) return

        val launchIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        if (launchIntent == null) {
            Log.e(TAG, "Unable to resolve the app launch activity for task $taskId")
            return
        }
        launchIntent.putExtra(TaskNotificationContract.OPEN_TASKS_EXTRA, true)
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val notificationId = taskId.hashCode() and Int.MAX_VALUE
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(appContext, CHANNEL_ID)
        } else {
            Notification.Builder(appContext)
        }
        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nueva tarea: $title")
            .setContentText("Tocá para ver la lista de tareas")
            .setStyle(Notification.BigTextStyle().bigText("Nueva tarea: $title"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .build()

        try {
            manager.notify(notificationId, notification)
        } catch (exception: SecurityException) {
            Log.e(TAG, "Notification permission is unavailable", exception)
        }
    }

    private companion object {
        const val TAG = "TaskNotifications"
        const val CHANNEL_ID = "new_tasks"
        const val CHANNEL_NAME = "Nuevas tareas"
    }
}
