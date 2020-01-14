package dev.remylavergne.routing

import dev.remylavergne.Scheduler
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import java.io.Serializable

@KtorExperimentalLocationsAPI
fun Route.scheduler() {

    post<Scheduler> {

        val registration = call.receive<State>()

        println()

        call.respond("Reached !")

    }

}

data class State(val state: Boolean): Serializable