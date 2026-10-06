package gcd.testing.firstproject.dollar.domain.usecase

import gcd.testing.firstproject.dollar.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow

class ObserveExchangeUseCase(
    val repository: ExchangeRepository
) {
    suspend fun invoke(): Flow<String?> {
        return repository.observe()
    }
}