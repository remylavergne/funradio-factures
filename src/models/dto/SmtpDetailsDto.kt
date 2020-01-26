package dev.remylavergne.models.dto

data class SmtpDetailsDto(
    val id: String,
    val server: String,
    val port: Int,
    val login: String
)