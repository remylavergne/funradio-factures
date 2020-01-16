package dev.remylavergne.services

import dev.remylavergne.Database
import dev.remylavergne.models.Email
import dev.remylavergne.models.dto.SchedulerDto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SchedulingService {

    private val jobsRunningInstances: MutableList<Email> = mutableListOf()

    private fun startCoroutineTimer(delayMillis: Long, repeatMillis: Long, action: () -> Unit) =
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
        return this.jobsRunningInstances
    }

    /**
     *
     */

    private fun start(job: SchedulerDto) {
        // Create Email
        val emailById = Database.getEmailById(job.emailId)
        // Create Job
        val jobPrepared = startCoroutineTimer(emailById.delayMillis, emailById.repeat) {
            // Send the mail
            println("Simulated => Mail sent")
        }
        // Save current job
        emailById.addJob(jobPrepared)
        // Save instance email running
        this.jobsRunningInstances.add(emailById)
    }

    private fun stop(job: SchedulerDto) {
        // Find email running to stop
        val emailFound = this.jobsRunningInstances.find { email ->
            email.id == job.emailId
        }

        emailFound?.let {
            // Stop running
            it.deleteCurrentJob()
            // Delete instance job
            this.jobsRunningInstances.remove(it)
        } ?: throw Exception("The mail ${job.emailId} doesn't exist in jobs running instances")

    }
}