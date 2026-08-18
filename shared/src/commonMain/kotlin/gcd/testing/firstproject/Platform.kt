package gcd.testing.firstproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform