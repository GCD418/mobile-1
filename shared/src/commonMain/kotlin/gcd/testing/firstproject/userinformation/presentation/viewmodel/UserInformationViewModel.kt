package gcd.testing.firstproject.userinformation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.userinformation.domain.usecase.FindAliasUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserInformationViewModel(
    val findAliasUseCase: FindAliasUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UserInformationState>(UserInformationState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<UserInformationEffects>()
    val effects = _effects.asSharedFlow()

    private fun emitEffect(effect: UserInformationEffects) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    fun emitEvent(event: UserInformationEvents) {
        when(event) {
            is UserInformationEvents.OnAliasChange -> {
                //Business Logic
                _state.update { it.copy(alias = event.value) }
            }
            UserInformationEvents.OnBack -> {
                emitEffect(UserInformationEffects.NavigateToBack)
            }
            UserInformationEvents.OnSubmit -> {
                viewModelScope.launch {
                    findAliasUseCase.invoke(_state.value.alias).fold(
                        onSuccess = { userInfo ->
                            _state.update {
                                it.copy(email = userInfo.email)
                            }
                        },
                        onFailure = {

                        }
                    )
                }
            }
        }
    }
}