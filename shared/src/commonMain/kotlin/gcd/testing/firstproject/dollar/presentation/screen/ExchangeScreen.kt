package gcd.testing.firstproject.dollar.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import gcd.testing.firstproject.dollar.presentation.viewmodel.ExchangeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel = koinViewModel(),
    onOpenCollaborativeTasks: (() -> Unit)? = null,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val message = viewModel.message.collectAsState()
        Text(message.value)
        onOpenCollaborativeTasks?.let { openTasks ->
            Button(onClick = openTasks) {
                Text("Collaborative task list")
            }
        }
    }

}