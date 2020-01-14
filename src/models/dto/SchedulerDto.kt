package dev.remylavergne.models.dto

import java.io.Serializable

data class SchedulerDto(
    val start: Boolean,
    val factureIds: List<String>,
    val delayMillis: Long = 86400000, // 24 hours
    val repeatMillis: Long = 0 // unlimited
) : Serializable