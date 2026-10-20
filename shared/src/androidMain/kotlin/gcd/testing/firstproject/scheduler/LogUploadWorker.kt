package gcd.testing.firstproject.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import gcd.testing.firstproject.userinformation.domain.usecase.FindAliasUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LogUploadWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val alias: FindAliasUseCase by inject()

    override suspend fun doWork(): Result {
        println("ejecutar instrucción para subir datos")
        val response = alias.invoke("calyr")
        return Result.success()
    }
}