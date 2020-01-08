package dev.remylavergne

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    EnvironmentVariables.getEnvironmentVariables()
    Database.initialization()
    
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {

            // TODO: Refactor this

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

            val mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "", "")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                // .withProxy("socksproxy.host.com", 1080, "proxy user", "proxy password")
                .withSessionTimeout(10 * 1000)
                .clearEmailAddressCriteria() // turns off email validation
                // .withProperty("mail.smtp.sendpartial", true)
                .withDebugLogging(true)
                .async()
                // not enough? what about this:
                .buildMailer()


            val test = mailer.sendMail(email)


            call.respondText(
                "HELLO WORLD! + email in environment variables : ${System.getenv("EMAIL_USER")}",
                contentType = ContentType.Text.Plain
            )
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

private fun databaseInitialization() {

}

