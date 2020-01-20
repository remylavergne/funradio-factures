package dev.remylavergne

import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Test


class AttachmentRetrieverTest {

    private val uploadDir = ".factures-attachments"
    private val existingFile = "file-example_PDF_1MB.pdf-1578949130642.pdf"
    private val nonValidFile = "unknownFile.pdf"

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    } // turn relaxUnitFun on for all mocks

    @Test
    fun retrieveExistingFile() {

       // val attachmentRetriever = AttachmentRetriever(uploadDir)
       // val fileRetrieve = attachmentRetriever.get(existingFile)

    }
}