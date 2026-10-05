package gcd.testing.firstproject.dollar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.dollar.domain.usecase.AddDollarQuoteUseCase
import gcd.testing.firstproject.dollar.domain.usecase.GetDollarQuotesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DollarViewModel(
    private val getDollarQuotesUseCase: GetDollarQuotesUseCase,
    private val addDollarQuoteUseCase: AddDollarQuoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DollarState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DollarEffects>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: DollarEvents) {
        when (event) {
            is DollarEvents.LoadQuotes -> loadQuotes()
            is DollarEvents.OnAddQuoteClick -> {
                _state.update { it.copy(isAddDialogVisible = true) }
            }
            is DollarEvents.OnDismissDialog -> {
                _state.update {
                    it.copy(
                        isAddDialogVisible = false,
                        officialQuote = "",
                        parallelQuote = ""
                    )
                }
            }
            is DollarEvents.OnOfficialQuoteChange -> {
                _state.update { it.copy(officialQuote = event.value) }
            }
            is DollarEvents.OnParallelQuoteChange -> {
                _state.update { it.copy(parallelQuote = event.value) }
            }
            is DollarEvents.OnConfirmAddQuote -> addQuote()
        }
    }

    private fun loadQuotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val quotes = getDollarQuotesUseCase()
                _state.update { it.copy(quotes = quotes, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                viewModelScope.launch {
                    _effects.emit(DollarEffects.ShowError(e.message ?: "Error loading quotes"))
                }
            }
        }
    }

    private fun addQuote() {
        val currentState = _state.value
        val official = currentState.officialQuote
        val parallel = currentState.parallelQuote

        if (official.isBlank() || parallel.isBlank()) {
            viewModelScope.launch {
                _effects.emit(DollarEffects.ShowError("Both fields are required"))
            }
            return
        }

        viewModelScope.launch {
            try {
                addDollarQuoteUseCase(official, parallel)
                _state.update {
                    it.copy(
                        isAddDialogVisible = false,
                        officialQuote = "",
                        parallelQuote = ""
                    )
                }
                loadQuotes()
                _effects.emit(DollarEffects.ShowSuccess("Quote added successfully"))
            } catch (e: Exception) {
                _effects.emit(DollarEffects.ShowError(e.message ?: "Error adding quote"))
            }
        }
    }
}
