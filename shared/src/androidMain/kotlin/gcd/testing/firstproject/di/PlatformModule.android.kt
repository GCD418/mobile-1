package gcd.testing.firstproject.di

import gcd.testing.firstproject.config.AppDatabase
import gcd.testing.firstproject.collaborativetasks.data.datasource.CollaborativeTaskDataSource
import gcd.testing.firstproject.collaborativetasks.data.datasource.FirebaseCollaborativeTaskDataSource
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
    single<CollaborativeTaskDataSource> { FirebaseCollaborativeTaskDataSource() }
    single<TaskLogger> { AndroidTaskLogger() }
    single<CollaborativeTaskRepository> { CollaborativeTaskRepositoryImpl(get(), get()) }
}