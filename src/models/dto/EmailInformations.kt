package dev.remylavergne.models.dto

data class EmailInformations(
    var receiverEmail: String = "",
    var mailTitle: String = "",
    var mailBody: String = ""
) {
    fun areValid() = receiverEmail.isNotEmpty() && mailTitle.isNotEmpty() && mailBody.isNotEmpty()
}
