package gcd.testing.firstproject.dollar.domain.usecase

import gcd.testing.firstproject.dollar.domain.model.DollarModel
import gcd.testing.firstproject.dollar.domain.repository.DollarRepository

class GetDollarQuotesUseCase(
    private val repository: DollarRepository
) {
    suspend operator fun invoke(): List<DollarModel> {
        return repository.getList()
    }
}
