package gcd.testing.firstproject.biometrics.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome
import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.model.toReadableMessage
import gcd.testing.firstproject.biometrics.presentation.lifecycle.SessionLifecycleObserver
import gcd.testing.firstproject.biometrics.presentation.viewmodel.BiometricSessionEvent
import gcd.testing.firstproject.biometrics.presentation.viewmodel.BiometricSessionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BiometricSessionScreen(
    viewModel: BiometricSessionViewModel = koinViewModel(),
    onOpenExchange: (() -> Unit)? = null,
    onOpenCollaborativeTasks: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsState()

    // Bloqueo automático al pasar la app a segundo plano (ON_STOP)
    SessionLifecycleObserver(
        onAppBackgrounded = {
            viewModel.emitEvent(BiometricSessionEvent.OnAppBackgrounded)
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Seguridad Biométrica y Sesión (100%)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        SessionStatusHeaderCard(sessionState = state.sessionState)

        state.error?.let { errorMsg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(
                        onClick = { viewModel.emitEvent(BiometricSessionEvent.ClearError) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }

        if (state.isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                    )
                    Text(
                        text = state.loadingMessage ?: "Procesando...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        when (val session = state.sessionState) {
            SessionState.Unregistered -> {
                UnregisteredSection(
                    isLoading = state.isLoading,
                    onEnrollClick = { viewModel.emitEvent(BiometricSessionEvent.EnrollBiometrics) },
                )
            }

            is SessionState.Locked -> {
                LockedSection(
                    isFirstUnlockPending = session.isFirstUnlockPending,
                    isLoading = state.isLoading,
                    biometricOutcome = state.biometricOutcome,
                    onUnlockClick = { viewModel.emitEvent(BiometricSessionEvent.UnlockWithBiometrics) },
                    onOpenSecuritySettingsClick = {
                        viewModel.emitEvent(BiometricSessionEvent.OpenSecuritySettings)
                    },
                    onDismissOutcome = { viewModel.emitEvent(BiometricSessionEvent.ClearError) },
                    onResetClick = { viewModel.emitEvent(BiometricSessionEvent.ResetEnrollment) },
                )
            }

            is SessionState.Unlocked -> {
                UnlockedSection(
                    unlockedState = session,
                    isLoading = state.isLoading,
                    protectedApiResponse = state.protectedApiResponse,
                    simulateUnauthorizedNextCall = state.simulateUnauthorizedNextCall,
                    onToggleSimulateUnauthorized = {
                        viewModel.emitEvent(BiometricSessionEvent.ToggleSimulateUnauthorized(it))
                    },
                    onSimulateProtectedApiClick = {
                        viewModel.emitEvent(BiometricSessionEvent.SimulateProtectedApiRequest())
                    },
                    onLockClick = { viewModel.emitEvent(BiometricSessionEvent.LockSession) },
                    onResetClick = { viewModel.emitEvent(BiometricSessionEvent.ResetEnrollment) },
                )
            }
        }

        if (onOpenExchange != null || onOpenCollaborativeTasks != null) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                onOpenExchange?.let { openExchange ->
                    OutlinedButton(
                        onClick = openExchange,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Ir a Exchange")
                    }
                }
                onOpenCollaborativeTasks?.let { openTasks ->
                    OutlinedButton(
                        onClick = openTasks,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Tareas Colaborativas")
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionStatusHeaderCard(sessionState: SessionState) {
    val (stateLabel, stateDescription, containerColor, contentColor) = when (sessionState) {
        SessionState.Unregistered -> StatusBadgeStyle(
            label = "ESTADO: UNREGISTERED (No Enrolado)",
            description = "Sin enrollmentToken ni refreshToken en KSafe.",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        is SessionState.Locked -> StatusBadgeStyle(
            label = "ESTADO: LOCKED (Sesión Bloqueada)",
            description = if (sessionState.isFirstUnlockPending) {
                "enrollmentToken cifrado en KSafe | refreshToken = null | accessToken = null"
            } else {
                "enrollmentToken = null | refreshToken cifrado en KSafe | accessToken en RAM descartado"
            },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        )

        is SessionState.Unlocked -> StatusBadgeStyle(
            label = "ESTADO: UNLOCKED (Sesión Activa)",
            description = "refreshToken cifrado en KSafe | accessToken disponible en RAM (Refresh #${sessionState.refreshCount})",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stateLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = stateDescription,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor,
            )
        }
    }
}

private data class StatusBadgeStyle(
    val label: String,
    val description: String,
    val containerColor: androidx.compose.ui.graphics.Color,
    val contentColor: androidx.compose.ui.graphics.Color,
)

@Composable
private fun UnregisteredSection(
    isLoading: Boolean,
    onEnrollClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Paso Inicial: Enrolamiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Simula un login inicial para que el Backend emita un enrollmentToken de un solo uso vinculado al deviceId cifrado en KSafe.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onEnrollClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("1. Simular Login y Enrolar Biometría")
            }
        }
    }
}

@Composable
private fun LockedSection(
    isFirstUnlockPending: Boolean,
    isLoading: Boolean,
    biometricOutcome: BiometricAuthOutcome?,
    onUnlockClick: () -> Unit,
    onOpenSecuritySettingsClick: () -> Unit,
    onDismissOutcome: () -> Unit,
    onResetClick: () -> Unit,
) {
    val badgeText = if (isFirstUnlockPending) {
        "Próximo desbloqueo: INSTANCIA 1 (Solicitará Token Inicial al Backend)"
    } else {
        "Próximo desbloqueo: INSTANCIA 2+ (Solicitará Refresh Token al Backend)"
    }

    if (biometricOutcome != null && biometricOutcome != BiometricAuthOutcome.Success) {
        BiometricOutcomeFeedbackBanner(
            outcome = biometricOutcome,
            onOpenSecuritySettingsClick = onOpenSecuritySettingsClick,
            onDismiss = onDismissOutcome,
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isFirstUnlockPending) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isFirstUnlockPending) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
                )
            }

            Text(
                text = if (isFirstUnlockPending) {
                    "Se detectó un enrollmentToken de un solo uso y ningún refreshToken. Al autenticar con biometría (BIOMETRIC_STRONG / BIOMETRIC_WEAK 2D) se consumirá el enrollmentToken para obtener el par inicial de tokens."
                } else {
                    "Se detectó un refreshToken persistido en KSafe. Al autenticar con biometría (BIOMETRIC_STRONG / BIOMETRIC_WEAK 2D) se llamará al endpoint de rotación de Refresh Token."
                },
                style = MaterialTheme.typography.bodyMedium,
            )

            Button(
                onClick = onUnlockClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Desbloquear con Biometría / Rostro / Huella")
            }

            OutlinedButton(
                onClick = onResetClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Resetear Enrolamiento (Volver a cero)")
            }
        }
    }
}

@Composable
private fun BiometricOutcomeFeedbackBanner(
    outcome: BiometricAuthOutcome,
    onOpenSecuritySettingsClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isWarning = outcome is BiometricAuthOutcome.Cancelled ||
        outcome is BiometricAuthOutcome.NoBiometricsEnrolled
    val containerColor = if (isWarning) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isWarning) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    val title = when (outcome) {
        BiometricAuthOutcome.Cancelled -> "Verificación cancelada"
        BiometricAuthOutcome.NoBiometricsEnrolled -> "Sin biometría configurada en el dispositivo"
        BiometricAuthOutcome.Lockout -> "Sensor biométrico bloqueado temporalmente"
        BiometricAuthOutcome.HardwareUnavailable -> "Hardware biométrico no disponible"
        is BiometricAuthOutcome.Error -> "Error de autenticación biométrica"
        BiometricAuthOutcome.Success -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            )
            Text(
                text = outcome.toReadableMessage(),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
            )
            if (outcome is BiometricAuthOutcome.NoBiometricsEnrolled) {
                Button(
                    onClick = onOpenSecuritySettingsClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Abrir Ajustes de Seguridad de Android")
                }
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Entendido")
            }
        }
    }
}

@Composable
private fun UnlockedSection(
    unlockedState: SessionState.Unlocked,
    isLoading: Boolean,
    protectedApiResponse: String?,
    simulateUnauthorizedNextCall: Boolean,
    onToggleSimulateUnauthorized: (Boolean) -> Unit,
    onSimulateProtectedApiClick: () -> Unit,
    onLockClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Panel de Estado de Sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Origen de Autenticación:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = unlockedState.authStageDescription,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Access Token actual (Solo en RAM):",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = unlockedState.accessToken,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Prueba de Capa de Red / Endpoint Protegido",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Forzar HTTP 401 Unauthorized (Auto-Lock):",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = simulateUnauthorizedNextCall,
                    onCheckedChange = onToggleSimulateUnauthorized,
                    enabled = !isLoading,
                )
            }

            Button(
                onClick = onSimulateProtectedApiClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Simular Petición API Protegida")
            }

            protectedApiResponse?.let { apiResponse ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = apiResponse,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            HorizontalDivider()

            Button(
                onClick = onLockClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Bloquear Sesión (Probar siguiente instancia)")
            }

            OutlinedButton(
                onClick = onResetClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Resetear Enrolamiento (Volver a cero)")
            }
        }
    }
}
