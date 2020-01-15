package dev.remylavergne.models.dto

data class EmailInformations(
    var receiverEmail: String = "",
    var mailTitle: String = "",
    var mailBody: String = "",
    var repeat: Long = 0,
    var delayMillis: Long = 10000
) {
    fun areValid() = receiverEmail.isNotEmpty() && mailTitle.isNotEmpty() && mailBody.isNotEmpty()
}
