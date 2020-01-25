package dev.remylavergne

data class SchedulingStatus(
    var emailId: String,
    var state: String // SchedulingEnum
)

enum class SchedulingEnum(val message: String) {
    SUCCESS_START("Start success"),
    SUCCESS_STOP("Stop success"),
    NO_SMTP_SERVER("No SMTP server provided for this email"),
    UNABLE_TO_UPDATE_EMAIL_STATUS("Email job status not updated. Can't start the job."),
    ERROR_EMAIL_DOESNT_EXIST("This email ID doesn't exist in current database."),
    EMAIL_NOT_ACTUALLY_SCHEDULED("This email doesn't exist in jobs instance")
}