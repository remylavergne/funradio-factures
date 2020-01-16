package dev.remylavergne.models

import java.util.*

data class Email(
    val id: String = UUID.randomUUID().toString(),
    var receiverEmail: String,
    var mailTitle: String? = null,
    var mailBody: String,
    var fileName: String?,
    var createdAt: Long,
    var active: Boolean = true,
    var delayMillis: Long = 10000, // Every 10 seconds
    var repeat: Long = 10000// Default: 10 seconds
)