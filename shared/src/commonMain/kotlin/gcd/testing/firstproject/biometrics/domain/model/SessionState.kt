package gcd.testing.firstproject.biometrics.domain.model

sealed interface SessionState {
    data object Unregistered : SessionState

    data class Locked(
        val isFirstUnlockPending: Boolean,
    ) : SessionState

    data class Unlocked(
        val accessToken: String,
        val authStageDescription: String,
        val refreshCount: Int,
    ) : SessionState
}
