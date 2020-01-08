package dev.remylavergne

import dev.remylavergne.exceptions.MissingEnvironmentVariables

object EnvironmentVariables {
    // MongoDB
    lateinit var mongoUsername: String
    lateinit var mongoPassword: String
    lateinit var mongoHostname: String
    lateinit var mongoPort: String
    // SMTP
    lateinit var smtpServer: String
    var smtpPort: String = DEFAULT_SMTP_PORT
    lateinit var smtpUser: String
    lateinit var smtpPassword: String


    @Throws(MissingEnvironmentVariables::class)
    fun getEnvironmentVariables() {
        // MongoDB credentials
        this.mongoUsername = System.getenv(MONGO_USERNAME)
        this.mongoPassword = System.getenv(MONGO_PASSWORD)
        this.mongoHostname = System.getenv(MONGO_HOSTNAME)
        this.mongoPort = System.getenv(MONGO_PORT)
        // SMTP configuration (most important) -> Mandatory
        this.smtpServer = System.getenv(SMTP_SERVER)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SMTP_SERVER")
        this.smtpPort = System.getenv(SMTP_PORT)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SMTP_PORT")
        this.smtpUser = System.getenv(SMTP_USER)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SMTP_USER")
        this.smtpPassword = System.getenv(SMTP_PASSWORD)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SMTP_PASSWORD")

    }
}