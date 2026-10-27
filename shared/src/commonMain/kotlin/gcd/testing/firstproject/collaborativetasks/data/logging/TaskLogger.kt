package gcd.testing.firstproject.collaborativetasks.data.logging

interface TaskLogger {
    fun error(message: String, throwable: Throwable)
}
