package dev.remylavergne.services

import dev.remylavergne.Database
import dev.remylavergne.SchedulingEnum
import dev.remylavergne.SchedulingStatus
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
    fun schedule(jobs: List<SchedulerDto>): List<SchedulingStatus> {
        val schedulingStatus = mutableListOf<SchedulingStatus>()

        jobs.forEach { schedule ->
            val status: SchedulingStatus = if (schedule.start) {
                this.start(schedule)
            } else {
                this.stop(schedule)
            }

            schedulingStatus.add(status)
        }
        return schedulingStatus // TODO: Handle this, show which operation failed
    }

    fun getAllCurrentJobs(): List<Email> =
        this.jobsRunningInstances.values.filter { it.stopAt < System.currentTimeMillis() }

    /**
     * Start a new email scheduling
     * The mail is scheduled until a user end specific action
     * @param job minimums informations to scheduled an email
     */
    private fun start(job: SchedulerDto): SchedulingStatus {

        val emailById = getEmailByIdFromLocalStorage(job.emailId) ?: return SchedulingStatus(
            job.emailId,
            SchedulingEnum.ERROR_EMAIL_DOESNT_EXIST.message
        )

        if (emailById.smtpServerId.isNullOrEmpty()) {
            return SchedulingStatus(emailById.id, SchedulingEnum.NO_SMTP_SERVER.message)
        }

        val jobPrepared = createJob(emailById)

        saveJobInstance(jobPrepared, emailById)

        if (!updateEmailScheduleStatus(emailById, true)) {
            return SchedulingStatus(emailById.id, SchedulingEnum.UNABLE_TO_UPDATE_EMAIL_STATUS.message)
        }

        return SchedulingStatus(emailById.id, SchedulingEnum.SUCCESS_START.message)
    }

    private fun getEmailByIdFromLocalStorage(emailId: String) = Database.getEmailById(emailId)

    private fun createJob(email: Email): Job {
        return startCoroutineTimer(
            startAt = email.startAt,
            repeatEvery = email.repeatEvery,
            stopAt = email.stopAt
        ) {
            // Create a new email to schedule
            // ScheduledEmail(emailById)
            println("Mail sent") // TODO: Remove
        }
    }

    private fun saveJobInstance(job: Job, email: Email) {
        this.jobsRunningInstances[job] = email
    }

    private fun updateEmailScheduleStatus(email: Email, state: Boolean): Boolean {
        return try {
            Database.isEmailScheduled(email, state)
            true
        } catch (e: Exception) {
            // TODO: Log exception
            false
        }
    }

    /**
     * Stop a current job / email running
     * @param job minimums informations to stop a the current job link to email
     */
    @InternalCoroutinesApi
    private fun stop(job: SchedulerDto): SchedulingStatus {
        // Find email running to stop
        val emailsFound = this.jobsRunningInstances.filter { (_, email) ->
            email.id == job.emailId
        }

        if (emailsFound.isEmpty()) {
            return SchedulingStatus(job.emailId, SchedulingEnum.EMAIL_NOT_ACTUALLY_SCHEDULED.message)
        }

        // Stop all jobs founds (In real life, just one)
        emailsFound.forEach { (job, email) ->
            cancelJob(job)
            removeJobCanceled(job)
            // Persist email state
            updateEmailScheduleStatus(email, false)
        }

        return SchedulingStatus(job.emailId, SchedulingEnum.SUCCESS_STOP.message) // TODO: Make this
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