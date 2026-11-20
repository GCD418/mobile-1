package gcd.testing.firstproject.di

import android.app.Application
import android.content.Context
import eu.anifantakis.lib.ksafe.KSafe
import gcd.testing.firstproject.biometrics.data.security.BiometricPromptAuthenticator
import gcd.testing.firstproject.biometrics.data.security.CurrentActivityHolder
import gcd.testing.firstproject.config.AppDatabase
import gcd.testing.firstproject.collaborativetasks.data.datasource.CollaborativeTaskDataSource
import gcd.testing.firstproject.collaborativetasks.data.datasource.FirebaseCollaborativeTaskDataSource
import gcd.testing.firstproject.collaborativetasks.data.preferences.SharedPreferencesTaskNotificationPreferences
import gcd.testing.firstproject.collaborativetasks.data.service.FirebaseTaskAddedNotificationService
import gcd.testing.firstproject.collaborativetasks.data.service.TaskAddedNotificationService
import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskNotificationPreferences
import gcd.testing.firstproject.collaborativetasks.data.logging.AndroidTaskLogger
import gcd.testing.firstproject.collaborativetasks.data.logging.TaskLogger
import gcd.testing.firstproject.collaborativetasks.data.repository.CollaborativeTaskRepositoryImpl
import gcd.testing.firstproject.collaborativetasks.domain.repository.CollaborativeTaskRepository
import gcd.testing.firstproject.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        getDatabaseBuilder(get()).build()
    }
    single { KSafe(get()) }
    single {
        val context: Context = get()
        (context.applicationContext as? Application)?.let { CurrentActivityHolder.register(it) }
        BiometricPromptAuthenticator(context.applicationContext)
    }
    single<CollaborativeTaskDataSource> { FirebaseCollaborativeTaskDataSource() }
    single<TaskLogger> { AndroidTaskLogger() }
    single<CollaborativeTaskRepository> { CollaborativeTaskRepositoryImpl(get(), get()) }
    single<TaskNotificationPreferences> { SharedPreferencesTaskNotificationPreferences(get()) }
    single<TaskAddedNotificationService> { FirebaseTaskAddedNotificationService(get(), get()) }
}