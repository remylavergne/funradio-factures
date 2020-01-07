package dev.remylavergne

object EnvironmentVariables {

    lateinit var senderEmail: String
    lateinit var receiverEmail: String
    lateinit var emailObject: String
    lateinit var emailBody: String

    lateinit var smtpServer: String
    var smtpPort: String = DEFAULT_SMTP_PORT
    lateinit var smtpUser: String
    lateinit var smtpPassword: String


    @Throws(MissingEnvironmentVariables::class)
    fun getEnvironmentVariables() {
        // TODO: Delete this and set via Frontend or API
        this.senderEmail = System.getenv(SENDER_EMAIL)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : SENDER_EMAIL")
        this.receiverEmail = System.getenv(RECEIVER_EMAIL)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : RECEIVER_EMAIL")
        this.emailObject = System.getenv(EMAIL_OBJECT)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : EMAIL_OBJECT")
        this.emailBody = System.getenv(EMAIL_BODY)
            ?: throw MissingEnvironmentVariables("Environment variables are missing : EMAIL_BODY")

        // SMTP configuration (most important)
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