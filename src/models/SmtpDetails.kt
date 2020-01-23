package dev.remylavergne.models

import java.util.*

data class SmtpDetails(
    val id: String = UUID.randomUUID().toString(),
    var server: String,
    var port: Int,
    var login: String,
    var password: String
)