package dev.remylavergne.services

import dev.remylavergne.models.Email
import dev.remylavergne.models.dto.SchedulerDto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SchedulingService {

    private val jobsRunning: List<Email> = emptyList()

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) =
        GlobalScope.launch {
            delay(delayMillis)
            if (repeatMillis > 0) {
                while (true) {
                    action()
                    delay(repeatMillis)
                }
            } else {
                action()
            }
        }

    // TODO: Make this with Coroutines and wait for all jobs done
    fun schedule(jobs: List<SchedulerDto>) {
        jobs.forEach { schedule ->
            if (schedule.start) {
                this.start(schedule)
            } else {
                this.stop(schedule)
            }
        }
    }

    fun getAllCurrentJobs(): List<Email> {
        return this.jobsRunning
    }

    /**
     *
     */

    private fun start(job: SchedulerDto) {
        // Create Email
    }

    private fun stop(job: SchedulerDto) {

    }
}