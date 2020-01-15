package dev.remylavergne.models

import java.util.*

data class Facture(
    val id: String = UUID.randomUUID().toString(),
    var receiverEmail: String,
    var mailTitle: String? = null,
    var mailBody: String,
    var fileName: String,
    var fileAdded: Long,
    var active: Boolean = true
) {
}