package gcd.testing.firstproject.dollar.data.repository

import gcd.testing.firstproject.dollar.data.datasource.DollarLocalDataSource
import gcd.testing.firstproject.dollar.domain.model.DollarModel
import gcd.testing.firstproject.dollar.domain.repository.DollarRepository

class DollarRepositoryImpl(
    val localDataSource: DollarLocalDataSource
): DollarRepository {
    override suspend fun getList(): List<DollarModel> {
        return localDataSource.getList()
    }

    override suspend fun insert(dollar: DollarModel) {
        localDataSource.insert(dollar)
    }
}