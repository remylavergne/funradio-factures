package dev.remylavergne.models

import java.util.*

data class MailSender(
    override val id: String = UUID.randomUUID().toString(),
    override var name: String,
    override var email: String
) : MinimumMailInformations