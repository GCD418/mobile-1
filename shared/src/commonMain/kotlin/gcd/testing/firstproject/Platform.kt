package gcd.testing.firstproject
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.apps
import dev.gitlive.firebase.initialize

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun isCollaborativeTaskFeatureEnabled(): Boolean
