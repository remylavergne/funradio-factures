package dev.remylavergne

import dev.remylavergne.models.Facture
import dev.remylavergne.models.dto.EmailInformations
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

/**
 * Register [Upload] routes.
 */
@KtorExperimentalLocationsAPI
fun Route.upload(uploadDir: File) {

    /**
     * Registers a POST route for [Upload]
     */
    post<Upload> {

        val multipart = call.receiveMultipart()
        var attachmentFile: File? = null
        val emailInformations = EmailInformations()

        // Processes each part of the multipart input content of the user
        multipart.forEachPart { part ->

            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "receiverEmail" -> emailInformations.receiverEmail = part.value
                        "mailTitle" -> emailInformations.mailTitle = part.value
                        "mailBody" -> emailInformations.mailBody = part.value
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
        if (attachmentFile != null && emailInformations.areValid()) {
            attachmentFile?.let { persistInformations(it, emailInformations) }
            call.respond(HttpStatusCode.PreconditionFailed, "File well upload")
        } else {
            call.respond(HttpStatusCode.OK, "File upload error")
        }
    }
}


/**
 * Make final object to persist all informations locally
 * @param fileUploaded the file uploaded by end user
 * @param informations object who hold mandatories informations to use the service
 */
private fun persistInformations(fileUploaded: File, informations: EmailInformations) {
    try {
        Database.persist(
            Facture(
                receiverEmail = informations.receiverEmail,
                mailTitle = informations.mailTitle,
                mailBody = informations.mailBody,
                fileName = fileUploaded.name,
                fileAdded = System.currentTimeMillis()
            )
        )
    } catch (e: Exception) {
        println("Error to persist facture in mongodb database...")
    }
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