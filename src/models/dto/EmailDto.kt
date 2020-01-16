package dev.remylavergne.models.dto

data class EmailInformations(
    var receiverEmail: String = "",
    var mailTitle: String = "",
    var mailBody: String = "",
    var repeat: Long = 10_000,
    var delayMillis: Long = 10_000
) {
    /** Minimum informations */
    fun areValid() = receiverEmail.isNotEmpty() && mailTitle.isNotEmpty() && mailBody.isNotEmpty()
}
