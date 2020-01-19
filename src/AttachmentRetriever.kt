package dev.remylavergne

import java.io.File

class AttachmentRetriever(private val uploadDir: File) {

    @Throws(Exception::class)
    fun get(fileName: String): File {

        val attachment = File("${uploadDir.path}/$fileName")

        if (!attachment.exists()) {
            throw Exception("Attachment doesn't exist...")
        }

        return attachment
    }

}