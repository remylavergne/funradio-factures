package dev.remylavergne.routing

import dev.remylavergne.Scheduling
import dev.remylavergne.models.dto.SchedulerDto
import dev.remylavergne.services.SchedulingService
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@KtorExperimentalLocationsAPI
fun Route.scheduling() {

    get<Scheduling> {

        // Get all current jobs
        val currentEmailsRunning = SchedulingService.getAllCurrentJobs()

        call.respond(currentEmailsRunning)
    }

    post<Scheduling> {

        val schedulerDto = call.receive<SchedulerDto>()

        SchedulingService.schedule(listOf(schedulerDto))

        call.respond("Jobs started !")
    }

}