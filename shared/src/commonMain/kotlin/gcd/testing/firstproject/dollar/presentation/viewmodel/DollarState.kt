package gcd.testing.firstproject.dollar.presentation.viewmodel

import gcd.testing.firstproject.dollar.domain.model.DollarModel

data class DollarState(
    val quotes: List<DollarModel> = emptyList(),
    val isLoading: Boolean = false,
    val officialQuote: String = "",
    val parallelQuote: String = "",
    val isAddDialogVisible: Boolean = false
)
