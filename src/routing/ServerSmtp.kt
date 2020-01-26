package dev.remylavergne.routing

import dev.remylavergne.Database
import dev.remylavergne.Smtp
import dev.remylavergne.models.SmtpDetails
import dev.remylavergne.models.dto.SmtpDetailsDto
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
@KtorExperimentalLocationsAPI
fun Route.smtp() {

    /**
     * Get all SMTP servers saved in database
     */
    get<Smtp> {
        var allSmtpServers: MutableList<SmtpDetailsDto> = mutableListOf()

        try {
            allSmtpServers = Database.getAllSmtpServers()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, allSmtpServers)
        }

        call.respond(HttpStatusCode.OK, allSmtpServers)
    }

    post<Smtp> {

        val smtpDetails = call.receive<SmtpDetails>()

        try {
          Database.persistSmtpServer(smtpDetails)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "SMTP Server save failed...")
        }

        call.respond(HttpStatusCode.OK, "SMTP Server well saved.")
    }

    delete<Smtp> {


    }
}

