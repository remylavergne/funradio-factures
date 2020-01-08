package dev.remylavergne.models.entities

import java.util.*

data class EmailDescription(
    val id: String = UUID.randomUUID().toString(),
    var receiverEmail: String,
    var senderEmail: String,
    var mailTitle: String? = null,
    var mailBody: String,
    var fileName: String,
    var fileAdded: Long,
    var filePath: String,
    var active: Boolean = true
) {
}