package gcd.testing.firstproject.signin.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gcd.testing.firstproject.signin.presentation.viewmodel.LoginEffects
import gcd.testing.firstproject.signin.presentation.viewmodel.LoginEvents
import gcd.testing.firstproject.signin.presentation.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SigninScreen( viewModel: LoginViewModel = koinViewModel ()) {
    val state = viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effects ->
            when(effects) {
                LoginEffects.NavigateToHome -> TODO()
                is LoginEffects.ShowToast -> {
                    println("ERROR ${effects.message}")
                }

                LoginEffects.SignUp -> TODO()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In")
        TextField(value = state.value.email, onValueChange = {
            viewModel.emitEvent(LoginEvents.OnEmailChanged(it))
        },
            modifier = Modifier.fillMaxWidth())
        TextField(value = state.value.password, onValueChange = {
            viewModel.emitEvent(LoginEvents.OnPasswordChanged(it))
        }) //La funcion lambda se puede sobreentender
        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            viewModel.emitEvent(LoginEvents.OnSubmit)
        }) {
            Text("Sign In")
        }

    }

}