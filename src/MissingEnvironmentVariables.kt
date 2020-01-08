package dev.remylavergne

class MissingEnvironmentVariables(private val msg: String) : Exception() {

    override val message: String?
        get() = msg
}