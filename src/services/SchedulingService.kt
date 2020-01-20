package dev.remylavergne.services

import dev.remylavergne.Database
import dev.remylavergne.models.Email
import dev.remylavergne.models.dto.SchedulerDto
import kotlinx.coroutines.*
import java.io.File

object SchedulingService {

    private val ONE_MINUTES = 60_000
    private lateinit var uploadDir: File
    private val jobsRunningInstances: MutableMap<Job, Email> = mutableMapOf()

    fun init(uploadDir: File) {
        this.uploadDir = uploadDir
    }

    fun getUploadDir() = this.uploadDir

    private fun startCoroutineTimer(startAt: Long, repeatEvery: Long, stopAt: Long, action: () -> Unit) =
        GlobalScope.launch {

            while (System.currentTimeMillis() < stopAt) {

                val currentTime = System.currentTimeMillis()

                if (currentTime >= startAt) {
                    action()
                    delay(repeatEvery)
                } else {
                    // 5 minutes delay before next iteration
                    delay(ONE_MINUTES * 5L)
                }
            }
            cancel()
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
    
    fun getAllCurrentJobs(): List<Email> =
        this.jobsRunningInstances.values.filter { it.stopAt < System.currentTimeMillis() }

    /**
     * Start a new email scheduling
     * The mail is scheduled until a user end specific action
     * @param job minimums informations to scheduled an email
     */
    private fun start(job: SchedulerDto) {
        // Create Email
        val emailById = Database.getEmailById(job.emailId)
        // Create Job
        val jobPrepared = startCoroutineTimer(
            startAt = emailById.startAt,
            repeatEvery = emailById.repeatEvery,
            stopAt = emailById.stopAt
        ) {
            // Create a new email to schedule
            // ScheduledEmail(emailById)
            println("Mail sent")
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