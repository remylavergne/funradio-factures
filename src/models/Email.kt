package dev.remylavergne.models

import java.util.*

data class Email(
    val id: String = UUID.randomUUID().toString(),
    var receiver: MailReceiver,
    var sender: MailSender,
    var title: String,
    var body: String,
    var attachmentName: String?,
    var createdAt: Long,
    var active: Boolean = false,
    var delayMillis: Long = 10000, // Every 10 seconds
    var repeatEvery: Long = 10000// Default: 10 seconds
)