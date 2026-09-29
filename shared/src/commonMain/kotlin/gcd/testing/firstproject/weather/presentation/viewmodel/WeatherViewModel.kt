package gcd.testing.firstproject.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.weather.domain.usecase.GetCurrentWeatherUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<WeatherEffects>()
    val effects = _effects.asSharedFlow()

    private fun emitEffect(effect: WeatherEffects) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    fun emitEvent(event: WeatherEvents) {
        when (event) {
            is WeatherEvents.OnLatitudeChange -> {
                _state.update { it.copy(latitude = event.value) }
            }
            is WeatherEvents.OnLongitudeChange -> {
                _state.update { it.copy(longitude = event.value) }
            }
            WeatherEvents.OnBack -> {
                emitEffect(WeatherEffects.NavigateBack)
            }
            WeatherEvents.OnSubmit -> {
                viewModelScope.launch {
                    val lat = _state.value.latitude.toFloatOrNull()
                    val lon = _state.value.longitude.toFloatOrNull()

                    if (lat == null || lon == null) {
                        _state.update { it.copy(error = "Invalid coordinates") }
                        emitEffect(WeatherEffects.ShowError("Invalid coordinates"))
                        return@launch
                    }

                    _state.update { it.copy(isLoading = true, error = null) }

                    getCurrentWeatherUseCase.invoke(lat, lon).fold(
                        onSuccess = { weather ->
                            _state.update { it.copy(isLoading = false, weather = weather) }
                        },
                        onFailure = { throwable ->
                            _state.update { it.copy(isLoading = false, error = throwable.message) }
                            emitEffect(WeatherEffects.ShowError(throwable.message ?: "Unknown error"))
                        },
                    )
                }
            }
        }
    }
}
