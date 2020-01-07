package dev.remylavergne

object EnvironmentVariables {

    lateinit var senderEmail: String
    lateinit var receiverEmail: String
    lateinit var emailObject: String
    lateinit var emailBody: String

    @Throws(MissingEnvironmentVariables::class)
    fun getEnvironmentVariables() {
        this.senderEmail = System.getenv(SENDER_EMAIL)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SENDER_EMAIL")
        this.receiverEmail = System.getenv(RECEIVER_EMAIL)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : RECEIVER_EMAIL")
        this.emailObject = System.getenv(EMAIL_OBJECT)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : EMAIL_OBJECT")
        this.emailBody = System.getenv(EMAIL_BODY)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : EMAIL_BODY")
    }
}