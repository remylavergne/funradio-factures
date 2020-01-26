package dev.remylavergne.routing

import dev.remylavergne.Database
import dev.remylavergne.SmtpLink
import dev.remylavergne.models.dto.SmtpLinkDto
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@KtorExperimentalLocationsAPI
fun Route.smtplink() {

    post<SmtpLink> {
        // TODO: Export in Service
        val smtpLink = call.receive<SmtpLinkDto>()

        try {
            Database.linkSMTPToEmail(smtpLink.smtpServerId, smtpLink.emailId)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                SmtpLinkResponse(
                    emailId = smtpLink.emailId,
                    smtpServerId = smtpLink.smtpServerId,
                    status = SmtpLinkResponseEnum.ERROR
                )
            )
        }

        call.respond(
            HttpStatusCode.OK, SmtpLinkResponse(
                emailId = smtpLink.emailId,
                smtpServerId = smtpLink.smtpServerId,
                status = SmtpLinkResponseEnum.SUCCESS
            )
        )
    }
}

data class SmtpLinkResponse(
    val emailId: String,
    val smtpServerId: String,
    val status: SmtpLinkResponseEnum
)

enum class SmtpLinkResponseEnum(val message: String) {
    SUCCESS("Success"),
    ERROR("Error. Unable to link SMTP to Email...")
}

