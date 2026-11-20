package gcd.testing.firstproject.biometrics.domain.model

class UnauthorizedException(
    override val message: String = "Sesión revocada o expirada, inicia sesión nuevamente",
    val statusCode: Int = 401,
) : Exception(message)
