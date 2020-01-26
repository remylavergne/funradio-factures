package dev.remylavergne.models

import dev.remylavergne.models.dto.SmtpDetailsDto
import java.util.*

data class SmtpDetails(
    val id: String = UUID.randomUUID().toString(),
    var server: String,
    var port: Int,
    var login: String,
    var password: String
) {
    fun generateUUID(): SmtpDetails {
        return SmtpDetails(server = this.server, port = this.port, login = this.login, password = this.password)
    }

    fun toDto(): SmtpDetailsDto {
        return SmtpDetailsDto(id = this.id, server = this.server, port = this.port, login = this.login)
    }
}