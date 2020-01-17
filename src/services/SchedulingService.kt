package dev.remylavergne.services

import dev.remylavergne.Database
import dev.remylavergne.models.Email
import dev.remylavergne.models.ScheduleEmail
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

    /**
     * Receive a list of actions to start, or stop email scheduling
     * @param jobs email list to process
     */
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

    fun getAllCurrentJobs(): MutableCollection<Email> = this.jobsRunningInstances.values

    /**
     * Start a new email scheduling
     * The mail is scheduled until a user end specific action
     * @param job minimums informations to scheduled an email
     */
    private fun start(job: SchedulerDto) {
        // Create Email
        val emailById = Database.getEmailById(job.emailId)
        // Create Job
        val jobPrepared = startCoroutineTimer(emailById.delayMillis, emailById.repeatEvery) {
            // Create a new email to schedule
            ScheduleEmail(emailById)
        }
        // Save instance email running
        this.jobsRunningInstances[jobPrepared] = emailById
        // Persist email state
        Database.isEmailScheduled(emailById, true)
    }

    /**
     * Stop a current job / email running
     * @param job minimums informations to stop a the current job link to email
     */
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
            // Persist email state
            Database.isEmailScheduled(email, false)
        }
    }

    /**
     * Cancel a specific running job
     * @param job job to stop
     */
    private fun cancelJob(job: Job) {
        job.cancel()
    }

    /**
     * Remove a specific job, after that was cancel
     * @param job job to remove from running jobs instance
     * @see cancelJob method to cancel a job, before to remove it
     */
    private fun removeJobCanceled(job: Job) {
        jobsRunningInstances.remove(job)
    }
}