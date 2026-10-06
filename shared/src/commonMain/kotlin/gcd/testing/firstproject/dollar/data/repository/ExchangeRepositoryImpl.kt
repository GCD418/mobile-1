package gcd.testing.firstproject.dollar.data.repository

import gcd.testing.firstproject.dollar.data.datasource.RealTimeDataBase
import gcd.testing.firstproject.dollar.domain.repository.ExchangeRepository
import kotlinx.coroutines.flow.Flow

class ExchangeRepositoryImpl(
    val realTimeDataBase: RealTimeDataBase
): ExchangeRepository {
    override suspend fun observe(): Flow<String?> {
        return realTimeDataBase.observeMessage()
    }
}