package gcd.testing.firstproject.dollar.presentation.viewmodel

sealed interface DollarEvents {
    data object LoadQuotes : DollarEvents
    data object OnAddQuoteClick : DollarEvents
    data object OnDismissDialog : DollarEvents
    data class OnOfficialQuoteChange(val value: String) : DollarEvents
    data class OnParallelQuoteChange(val value: String) : DollarEvents
    data object OnConfirmAddQuote : DollarEvents
}
