package gcd.testing.firstproject.dollar.domain.usecase

import gcd.testing.firstproject.dollar.domain.model.DollarModel
import gcd.testing.firstproject.dollar.domain.repository.DollarRepository

class AddDollarQuoteUseCase(
    private val repository: DollarRepository
) {
    suspend operator fun invoke(official: String, parallel: String) {
        val dollar = DollarModel(official = official, parallel = parallel)
        repository.insert(dollar)
    }
}
