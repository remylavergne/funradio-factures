package dev.remylavergne

import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

object Email {

    init {
        val email = EmailBuilder.startingBlank()
            .from("Rémy Lavergne", "")
            .to("Rémy Lavergne", "")
            // .bcc("Mr Sweetnose <snose@candyshop.org>")
            // .withReplyTo("lollypop", "lolly.pop@othermail.com")
            .withSubject("First test")
            .withPlainText("Please view this email in a modern email client!")
            // .withAttachment("invitation", pdfByteArray, "application/pdf")
            // .withHeader("X-Priority", 5)
            //.withReturnReceiptTo()
            .buildEmail();

        email

        val mailer = MailerBuilder
            .withSMTPServer(
                EnvironmentVariables.smtpServer,
                EnvironmentVariables.smtpPort.toInt(),
                EnvironmentVariables.smtpUser,
                EnvironmentVariables.smtpPassword
            )
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .withSessionTimeout(10 * 1000)
            .clearEmailAddressCriteria()
            .withDebugLogging(true)
            .async()
            .buildMailer()

        mailer
    }

    fun createEmail() {

    }
}