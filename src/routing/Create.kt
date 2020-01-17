package dev.remylavergne.routing

import dev.remylavergne.Create
import dev.remylavergne.Database
import dev.remylavergne.models.Email
import dev.remylavergne.models.MailReceiver
import dev.remylavergne.models.MailSender
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.InputStream
import java.io.OutputStream

private const val RECEIVER_NAME = "receiverName"
private const val RECEIVER_EMAIL = "receiverEmail"
private const val SENDER_NAME = "senderName"
private const val SENDER_EMAIL = "senderEmail"
private const val MAIL_TITLE = "mailTitle"
private const val MAIL_BODY = "mailBody"
private const val REPEAT_EVERY = "repeatEvery"
private const val DELAY = "delayMillis"

/**
 * Register [Create] routes.
 */
@KtorExperimentalLocationsAPI
fun Route.create(uploadDir: File) {

    /**
     * Registers a POST route for [Create]
     */
    post<Create> {

        val multipart = call.receiveMultipart()
        var attachmentFile: File? = null
        val informations = mutableMapOf<String, String>()

        // Processes each part of the multipart input content of the user
        multipart.forEachPart { part ->

            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        RECEIVER_NAME -> informations[RECEIVER_NAME] = part.value
                        RECEIVER_EMAIL -> informations[RECEIVER_EMAIL] = part.value
                        SENDER_NAME -> informations[SENDER_NAME] = part.value
                        SENDER_EMAIL -> informations[SENDER_EMAIL] = part.value
                        MAIL_TITLE -> informations[MAIL_TITLE] = part.value
                        MAIL_BODY -> informations[MAIL_BODY] = part.value
                        REPEAT_EVERY -> informations[REPEAT_EVERY] = part.value
                        DELAY -> informations[DELAY] = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = File(part.originalFileName ?: "no_name").extension
                    val file = File(
                        uploadDir,
                        "${part.originalFileName}-${System.currentTimeMillis()}.$ext"
                    )

                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    attachmentFile = file
                }
            }

            part.dispose()
        }

        // Persist file
        if (requiredInformationsReceived(informations)) {
            val id = persistInformations(informations, attachmentFile)
            call.respond(HttpStatusCode.OK, "Email with id $id has been created.")
        } else {
            call.respond(HttpStatusCode.NotAcceptable, "File upload error")
        }
    }
}

private fun requiredInformationsReceived(informations: MutableMap<String, String>) = informations.size == 8


/**
 * Make final object to persist all informations locally
 * @param fileUploaded the file uploaded by end user
 * @param dto object who hold mandatories informations to use the service
 */
private fun persistInformations(informations: MutableMap<String, String>, fileUploaded: File? = null): String {
    var id = ""

    try {
        val receiver = MailReceiver(name = informations[RECEIVER_NAME]!!, email = informations[RECEIVER_EMAIL]!!)
        val sender = MailSender(name = informations[SENDER_NAME]!!, email = informations[SENDER_EMAIL]!!)
        val newEmail = Email(
            receiver = receiver,
            sender = sender,
            title = informations[MAIL_TITLE]!!,
            body = informations[MAIL_BODY]!!,
            attachmentName = fileUploaded?.name,
            createdAt = System.currentTimeMillis(),
            delayMillis = informations[DELAY]?.toLong()!!,
            repeatEvery = informations[REPEAT_EVERY]?.toLong()!!
        )

        Database.persist(newEmail)
        id = newEmail.id
    } catch (e: Exception) {
        println("Error to persist facture in mongodb database...")
    }

    return id
}


/**
 * Utility boilerplate method that suspending,
 * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
 *
 * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
 * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
 */
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}