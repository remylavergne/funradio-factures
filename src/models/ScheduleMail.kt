package dev.remylavergne.models

import dev.remylavergne.EnvironmentVariables
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder


data class ScheduleEmail(
    val email: Email
) {

    private val mail = EmailBuilder.startingBlank()
        .from(email.sender.name, email.sender.email)
        .to(email.receiver.name, email.receiver.email)
        // .bcc("Mr Sweetnose <snose@candyshop.org>")
        // .withReplyTo("lollypop", "lolly.pop@othermail.com")
        .withSubject(email.title)
        .withPlainText(email.body)
        // .withAttachment("invitation", pdfByteArray, "application/pdf")
        // .withHeader("X-Priority", 5)
        //.withReturnReceiptTo()
        .buildEmail()

    private val mailer = MailerBuilder
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

    init {
        mailer.sendMail(mail)
    }
}