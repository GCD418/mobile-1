package gcd.testing.firstproject.dollar.domain.repository

import gcd.testing.firstproject.dollar.domain.model.DollarModel

interface DollarRepository {
    suspend fun getList(): List<DollarModel>

    suspend fun insert(dollar: DollarModel)
}