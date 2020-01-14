package dev.remylavergne.routing

import dev.remylavergne.Scheduling
import dev.remylavergne.models.dto.SchedulerDto
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
fun Route.scheduling() {

    post<Scheduling> {

        val schedulerDto = call.receive<SchedulerDto>()

        if (schedulerDto.start) {
            // Start scheduling for each ids

        } else {
            // Stop Scheduling

        }

        call.respond("Reached !")

    }

}