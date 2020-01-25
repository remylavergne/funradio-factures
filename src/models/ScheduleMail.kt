package dev.remylavergne.models

import dev.remylavergne.AttachmentRetriever
import dev.remylavergne.services.SchedulingService
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.simplejavamail.api.email.Email as SimpleJavaEmail


data class ScheduledEmail(
    val email: Email,
    val smtpServer: SmtpDetails
) {

    init {
        // If email had an attachment, retrieve it
        if (email.attachmentName.isNullOrEmpty()) {
            sendMailWithoutAttachment()
        } else {
            sendMailWithAttachment()
        }
    }

    // TODO: Don't need to duplicate code here...
    private fun sendMailWithAttachment() {
        // Get attachment file byte array
        email.attachmentName?.let { attachmentName ->
            val ar = AttachmentRetriever(SchedulingService.getUploadDir())
            val attachment = ar.get(attachmentName).readBytes()

            val mail = EmailBuilder.startingBlank()
                .from(email.sender.name, email.sender.email)
                .to(email.receiver.name, email.receiver.email)
                .withSubject(email.title)
                .withPlainText(email.body)
                .withAttachment("attachment", attachment, "application/pdf")
                .buildEmail()

            // Send
            sendMail(mail)

        } ?: throw Exception("Invalid parameters to send mail with attachment...")
    }

    private fun sendMailWithoutAttachment() {

        val mail = EmailBuilder.startingBlank()
            .from(email.sender.name, email.sender.email)
            .to(email.receiver.name, email.receiver.email)
            .withSubject(email.title)
            .withPlainText(email.body)
            .buildEmail()

        // Send
        sendMail(mail)

    }

    private fun sendMail(mail: SimpleJavaEmail) {

        val mailer = MailerBuilder
            .withSMTPServer(
                smtpServer.server,
                smtpServer.port,
                smtpServer.login,
                smtpServer.password
            )
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .withSessionTimeout(10 * 1000)
            .clearEmailAddressCriteria()
            .withDebugLogging(true)
            .async()
            .buildMailer()

        mailer.sendMail(mail)
    }
}