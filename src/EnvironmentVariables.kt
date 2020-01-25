package dev.remylavergne

import dev.remylavergne.exceptions.MissingEnvironmentVariables

object EnvironmentVariables {
    // MongoDB
    lateinit var mongoUsername: String
    lateinit var mongoPassword: String
    lateinit var mongoHostname: String
    lateinit var mongoPort: String


    @Throws(MissingEnvironmentVariables::class)
    fun getEnvironmentVariables() {
        // MongoDB credentials
        this.mongoUsername = System.getenv(MONGO_USERNAME)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : MONGO_USERNAME")
        this.mongoPassword = System.getenv(MONGO_PASSWORD)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : MONGO_PASSWORD")
        this.mongoHostname = System.getenv(MONGO_HOSTNAME)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : MONGO_HOSTNAME")
        this.mongoPort = System.getenv(MONGO_PORT)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : MONGO_PORT")
    }
}