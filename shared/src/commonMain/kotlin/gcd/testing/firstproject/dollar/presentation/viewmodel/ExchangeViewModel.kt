package gcd.testing.firstproject.dollar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.dollar.domain.usecase.ObserveExchangeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExchangeViewModel(
    val usecase: ObserveExchangeUseCase
): ViewModel() {
    private val _message = MutableStateFlow<String>("")
    val message = _message.asStateFlow()

    init {
        observeMessage()
    }

    private fun observeMessage() {
        viewModelScope.launch {
            usecase.invoke()
                .collect { message ->
                    _message.value = message?:"Test"
                }
        }
    }
}