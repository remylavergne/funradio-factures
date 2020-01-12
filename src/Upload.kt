package dev.remylavergne

import dev.remylavergne.models.Facture
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
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

    get<Upload> {
        call.respond(HttpStatusCode.OK, ResponseTest("Un message String", 42))
    }

    /**
     * Registers a POST route for [Upload]
     */
    post<Upload> {

        val multipart = call.receiveMultipart()
        var title = ""
        var attachmentFile: File? = null

        // Processes each part of the multipart input content of the user
        multipart.forEachPart { part ->
            if (part is PartData.FormItem) {
                if (part.name == "title") {
                    title = part.value
                }
            } else if (part is PartData.FileItem) {
                val ext = File(part.originalFileName ?: "no_name").extension
                val file = File(
                    uploadDir,
                    "upload-${System.currentTimeMillis()}-${title.hashCode()}.$ext"
                )

                part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                attachmentFile = file
            }

            part.dispose()
        }

        // Persist file
        attachmentFile?.let {
            persistInformations(it)
        } ?: call.respond(HttpStatusCode.OK, "File upload error")

        call.respond(HttpStatusCode.OK, "File well upload")
    }
}

private fun persistInformations(fileUploaded: File) {
    val facture: Facture = Facture(
        receiverEmail = "",
        senderEmail = "",
        mailTitle = "",
        mailBody = "",
        fileName = fileUploaded.name,
        fileAdded = System.currentTimeMillis()
    )

    // Persist
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