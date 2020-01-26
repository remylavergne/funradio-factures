package dev.remylavergne

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import dev.remylavergne.models.Email
import dev.remylavergne.models.SmtpDetails
import dev.remylavergne.models.dto.SmtpDetailsDto
import org.litote.kmongo.*

object Database {

    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase
    private lateinit var collection: MongoCollection<Email>
    private lateinit var smtpDetailsCollection: MongoCollection<SmtpDetails>

    private val uri =
        MongoClientURI("mongodb://${EnvironmentVariables.mongoUsername}:${EnvironmentVariables.mongoPassword}@${EnvironmentVariables.mongoHostname}:${EnvironmentVariables.mongoPort}")

    @Throws(Exception::class)
    fun initialization() {
        try {
            client = KMongo.createClient(uri = uri)
            database = client.getDatabase(DATABASE_NAME)
            collection = database.getCollection<Email>()
            smtpDetailsCollection = database.getCollection<SmtpDetails>()
        } catch (e: Exception) {
            throw e
        }
    }

    fun persist(email: Email) {
        this.collection.insertOne(email)
    }

    fun persistSmtpServer(smtpDetails: SmtpDetails) {
        this.smtpDetailsCollection.insertOne(smtpDetails.generateUUID())
    }

    fun getEmailById(id: String): Email? {
        return this.collection.findOne(Email::id eq id)
    }

    /**
     * Change the state of an email.
     * The email field "active" is used to know if it scheduled, or not.
     * @param emailById the current email needs to be update
     * @param state the new state (e.g. true == scheduled)
     */
    fun isEmailScheduled(emailById: Email, state: Boolean) {
        this.collection.updateOne(Email::id eq emailById.id, Email::active setTo state)
    }

    fun linkSMTPToEmail(smtpServerId: String, emailId: String) {
        this.collection.updateOne(Email::id eq emailId, Email::smtpServerId setTo smtpServerId)
    }

    fun getAllSmtpServers(): MutableList<SmtpDetailsDto> {
        val collections = mutableListOf<SmtpDetailsDto>()

        this.smtpDetailsCollection.find(SmtpDetails::class.java).forEach { smtpDetail ->
            collections.add(smtpDetail.toDto())
        }

        return collections
    }
}