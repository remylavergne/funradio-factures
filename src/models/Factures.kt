package dev.remylavergne.models

import java.util.*

data class Factures(
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