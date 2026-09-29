package gcd.testing.firstproject.weather.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gcd.testing.firstproject.weather.presentation.viewmodel.WeatherEffects
import gcd.testing.firstproject.weather.presentation.viewmodel.WeatherEvents
import gcd.testing.firstproject.weather.presentation.viewmodel.WeatherViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is WeatherEffects.ShowError -> { /* handled by state.error */ }
                WeatherEffects.NavigateBack -> { /* TODO: wire navigation */ }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = state.latitude,
            onValueChange = { viewModel.emitEvent(WeatherEvents.OnLatitudeChange(it)) },
            label = { Text("Latitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = state.longitude,
            onValueChange = { viewModel.emitEvent(WeatherEvents.OnLongitudeChange(it)) },
            label = { Text("Longitude") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.emitEvent(WeatherEvents.OnSubmit) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Get Weather")
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(error)
        }

        state.weather?.let { weather ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Temperature: ${weather.temperature}°C")
            Text("Wind: ${weather.windSpeed} km/h, ${weather.windDirection}°")
            Text("Weather Code: ${weather.weatherCode}")
            Text("Time: ${weather.time}")
        }
    }
}
