package dev.remylavergne.models.dto

import java.io.Serializable

data class SchedulerDto(
    val start: Boolean,
    val emailId: String
) : Serializable