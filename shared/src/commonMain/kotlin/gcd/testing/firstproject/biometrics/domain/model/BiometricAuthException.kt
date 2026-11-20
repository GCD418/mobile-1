package gcd.testing.firstproject.biometrics.domain.model

class BiometricAuthException(
    val outcome: BiometricAuthOutcome,
    override val message: String = outcome.toReadableMessage(),
) : Exception(message)

fun BiometricAuthOutcome.toReadableMessage(): String = when (this) {
    BiometricAuthOutcome.Success -> "Autenticación biométrica exitosa."
    BiometricAuthOutcome.Cancelled -> "Autenticación biométrica cancelada por el usuario."
    BiometricAuthOutcome.NoBiometricsEnrolled ->
        "No hay datos biométricos registrados en el dispositivo. Configura tu rostro o huella en Ajustes."
    BiometricAuthOutcome.Lockout ->
        "Sensor biométrico bloqueado temporalmente por demasiados intentos fallidos. Intenta de nuevo en unos segundos."
    BiometricAuthOutcome.HardwareUnavailable ->
        "El hardware biométrico no está disponible en este dispositivo."
    is BiometricAuthOutcome.Error -> message.ifBlank { "Error durante la verificación biométrica." }
}
