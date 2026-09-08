package gcd.testing.firstproject.userinformation.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import gcd.testing.firstproject.userinformation.presentation.viewmodel.UserInformationEffects
import gcd.testing.firstproject.userinformation.presentation.viewmodel.UserInformationEvents
import gcd.testing.firstproject.userinformation.presentation.viewmodel.UserInformationViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserInformationScreen(viewModel: UserInformationViewModel = koinViewModel ()) {
    val state = viewModel.state.collectAsState() //Con esto podemos observar los posibles cambios

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effects ->
            when(effects) {
                UserInformationEffects.OnSuccess -> TODO()
                is UserInformationEffects.ShowToast -> TODO()
                UserInformationEffects.NavigateToBack -> TODO()
            }
        }
    }

    Column {
        TextField(value = state.value.alias, onValueChange = {
            viewModel.emitEvent(UserInformationEvents.OnAliasChange(it)) //Lambda notation
        })
        Button(onClick = {
            viewModel.emitEvent(UserInformationEvents.OnSubmit)
        }) {
            Text("Buscar")
        }
        state.value.email?.let {
            Text(it)
        }
    }
}
