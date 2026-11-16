package gcd.testing.firstproject.di

import gcd.testing.firstproject.biometrics.domain.usecase.EnrollBiometricsUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.LockSessionUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ObserveSessionStateUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ResetEnrollmentUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.UnlockWithBiometricsUseCase
import gcd.testing.firstproject.dollar.domain.usecase.AddDollarQuoteUseCase
import gcd.testing.firstproject.dollar.domain.usecase.GetDollarQuotesUseCase
import gcd.testing.firstproject.dollar.domain.usecase.ObserveExchangeUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.GetTaskNotificationsMutedUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.ObserveCollaborativeTasksUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.ObserveCompletedTaskCountUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.SetCollaborativeTaskCompletionUseCase
import gcd.testing.firstproject.collaborativetasks.domain.usecase.SetTaskNotificationsMutedUseCase
import gcd.testing.firstproject.userinformation.domain.usecase.FindAliasUseCase
import gcd.testing.firstproject.weather.domain.usecase.GetCurrentWeatherUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::FindAliasUseCase)
    singleOf(::GetCurrentWeatherUseCase)
    singleOf(::GetDollarQuotesUseCase)
    singleOf(::AddDollarQuoteUseCase)
    singleOf(::ObserveExchangeUseCase)
    singleOf(::ObserveCollaborativeTasksUseCase)
    singleOf(::ObserveCompletedTaskCountUseCase)
    singleOf(::SetCollaborativeTaskCompletionUseCase)
    singleOf(::GetTaskNotificationsMutedUseCase)
    singleOf(::SetTaskNotificationsMutedUseCase)
    singleOf(::ObserveSessionStateUseCase)
    singleOf(::EnrollBiometricsUseCase)
    singleOf(::UnlockWithBiometricsUseCase)
    singleOf(::LockSessionUseCase)
    singleOf(::ResetEnrollmentUseCase)
}