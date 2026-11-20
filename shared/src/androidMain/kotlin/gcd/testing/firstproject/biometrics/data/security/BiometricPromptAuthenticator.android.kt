package gcd.testing.firstproject.biometrics.data.security

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

actual class BiometricPromptAuthenticator(
    private val appContext: Context,
) {
    actual suspend fun authenticate(title: String, subtitle: String): BiometricAuthOutcome =
        withContext(Dispatchers.Main) {
            val activity = CurrentActivityHolder.getCurrentActivity()
                ?: return@withContext BiometricAuthOutcome.Error(
                    "No se encontró una FragmentActivity/AppCompatActivity activa para mostrar el prompt biométrico."
                )

            val biometricManager = BiometricManager.from(activity)
            val biometricAuthenticators =
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK

            val canAuthBiometric = biometricManager.canAuthenticate(biometricAuthenticators)

            val useDeviceCredentialFallback = when (canAuthBiometric) {
                BiometricManager.BIOMETRIC_SUCCESS -> false
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    return@withContext BiometricAuthOutcome.NoBiometricsEnrolled
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    val canUseDeviceCredential = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) ==
                        BiometricManager.BIOMETRIC_SUCCESS
                    if (!canUseDeviceCredential) {
                        return@withContext BiometricAuthOutcome.HardwareUnavailable
                    }
                    true
                }
                else -> false
            }

            suspendCancellableCoroutine { continuation ->
                val executor = ContextCompat.getMainExecutor(activity)
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        if (continuation.isActive) {
                            continuation.resume(BiometricAuthOutcome.Success)
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (!continuation.isActive) return
                        val outcome = when (errorCode) {
                            BiometricPrompt.ERROR_USER_CANCELED,
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                            BiometricPrompt.ERROR_CANCELED -> BiometricAuthOutcome.Cancelled

                            BiometricPrompt.ERROR_NO_BIOMETRICS,
                            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> BiometricAuthOutcome.NoBiometricsEnrolled

                            BiometricPrompt.ERROR_LOCKOUT,
                            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricAuthOutcome.Lockout

                            BiometricPrompt.ERROR_HW_NOT_PRESENT,
                            BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricAuthOutcome.HardwareUnavailable

                            else -> BiometricAuthOutcome.Error(errString.toString())
                        }
                        continuation.resume(outcome)
                    }
                }

                val biometricPrompt = BiometricPrompt(activity, executor, callback)
                val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)

                if (useDeviceCredentialFallback) {
                    promptInfoBuilder.setAllowedAuthenticators(
                        biometricAuthenticators or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                } else {
                    promptInfoBuilder
                        .setAllowedAuthenticators(biometricAuthenticators)
                        .setNegativeButtonText("Cancelar")
                }

                continuation.invokeOnCancellation {
                    biometricPrompt.cancelAuthentication()
                }

                try {
                    biometricPrompt.authenticate(promptInfoBuilder.build())
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(
                            BiometricAuthOutcome.Error(
                                e.message ?: "Error al iniciar BiometricPrompt."
                            )
                        )
                    }
                }
            }
        }

    actual fun openSecuritySettings() {
        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK,
                )
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }

        val context = CurrentActivityHolder.getCurrentActivity() ?: appContext
        try {
            enrollIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(enrollIntent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {
                val rootSettings = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(rootSettings)
            }
        }
    }
}
