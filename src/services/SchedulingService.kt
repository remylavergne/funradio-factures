package dev.remylavergne.services

import dev.remylavergne.Database
import dev.remylavergne.models.Email
import dev.remylavergne.models.dto.SchedulerDto
import kotlinx.coroutines.*

object SchedulingService {

    private val jobsRunningInstances: MutableMap<Job, Email> = mutableMapOf()

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
    @InternalCoroutinesApi
    fun schedule(jobs: List<SchedulerDto>) {
        jobs.forEach { schedule ->
            if (schedule.start) {
                this.start(schedule)
            } else {
                this.stop(schedule)
            }
        }
    }

    fun getAllCurrentJobs(): Map<Job, Email> {
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
        // Save instance email running
        this.jobsRunningInstances[jobPrepared] = emailById
    }

    @InternalCoroutinesApi
    private fun stop(job: SchedulerDto) {
        // Find email running to stop
        val emailsFound = this.jobsRunningInstances.filter { (_, email) ->
            email.id == job.emailId
        }
        // Stop all jobs founds
        emailsFound.forEach { (job, email) ->
            cancelJob(job)
            removeJobCanceled(job)
        }
    }

    private fun cancelJob(job: Job) {
        job.cancel()
    }

    private fun removeJobCanceled(job: Job) {
        jobsRunningInstances.remove(job)
    }
}