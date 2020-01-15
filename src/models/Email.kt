package dev.remylavergne.models

import kotlinx.coroutines.Job
import java.util.*

data class Email(
    val id: String = UUID.randomUUID().toString(),
    var receiverEmail: String,
    var mailTitle: String? = null,
    var mailBody: String,
    var fileName: String,
    var fileAdded: Long,
    var active: Boolean = true,
    var delayMillis: Long = 10000, // Every 10 seconds
    var repeat: Long = 0, // Default: Unlimited
    private var job: Job? = null
) {
    @Throws(Exception::class)
    fun addJob(job: Job) {
        if (this.job != null) {
            // Error -> Cancel previous Job
            throw Exception("A job already exist ! Please remove it before")
        }

        this.job = job
    }

    fun deleteJob(job: Job) {
        this.job = null
    }
}