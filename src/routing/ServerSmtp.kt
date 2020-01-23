package dev.remylavergne.routing

import dev.remylavergne.Database
import dev.remylavergne.ServerSmtp
import dev.remylavergne.models.SmtpDetails
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
    get<ServerSmtp> {


    }

    post<ServerSmtp> {

        val smtpDetails = call.receive<SmtpDetails>()

        Database.persistSmtpServer(smtpDetails)

        call.respond(HttpStatusCode.OK, "SMTP Server saved.")
    }

    delete<ServerSmtp> {


    }
}

