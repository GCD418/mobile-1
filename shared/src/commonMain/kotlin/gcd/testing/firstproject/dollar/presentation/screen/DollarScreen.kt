package gcd.testing.firstproject.dollar.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gcd.testing.firstproject.dollar.presentation.viewmodel.DollarEffects
import gcd.testing.firstproject.dollar.presentation.viewmodel.DollarEvents
import gcd.testing.firstproject.dollar.presentation.viewmodel.DollarViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DollarScreen(
    viewModel: DollarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DollarEvents.LoadQuotes)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DollarEffects.ShowError -> {
                    // TODO: Implement snackbar or toast
                }
                is DollarEffects.ShowSuccess -> {
                    // TODO: Implement snackbar or toast
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dollar Quotes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(DollarEvents.OnAddQuoteClick) }
            ) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (state.quotes.isEmpty()) {
                Text(
                    text = "No quotes available. Tap + to add one.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.quotes) { quote ->
                        DollarQuoteCard(
                            official = quote.official,
                            parallel = quote.parallel
                        )
                    }
                }
            }
        }
    }

    if (state.isAddDialogVisible) {
        AddQuoteDialog(
            officialQuote = state.officialQuote,
            parallelQuote = state.parallelQuote,
            onOfficialQuoteChange = { viewModel.onEvent(DollarEvents.OnOfficialQuoteChange(it)) },
            onParallelQuoteChange = { viewModel.onEvent(DollarEvents.OnParallelQuoteChange(it)) },
            onConfirm = { viewModel.onEvent(DollarEvents.OnConfirmAddQuote) },
            onDismiss = { viewModel.onEvent(DollarEvents.OnDismissDialog) }
        )
    }
}

@Composable
private fun DollarQuoteCard(
    official: String,
    parallel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Official: $official",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Parallel: $parallel",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun AddQuoteDialog(
    officialQuote: String,
    parallelQuote: String,
    onOfficialQuoteChange: (String) -> Unit,
    onParallelQuoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Dollar Quote") },
        text = {
            Column {
                OutlinedTextField(
                    value = officialQuote,
                    onValueChange = onOfficialQuoteChange,
                    label = { Text("Official Quote") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = parallelQuote,
                    onValueChange = onParallelQuoteChange,
                    label = { Text("Parallel Quote") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
