package dev.remylavergne

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import dev.remylavergne.models.Factures
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object Database {

    lateinit var client: MongoClient
    lateinit var database: MongoDatabase
    lateinit var collection: MongoCollection<Factures>

    private val uri =
        MongoClientURI("mongodb://${EnvironmentVariables.mongoUsername}:${EnvironmentVariables.mongoPassword}@${EnvironmentVariables.mongoHostname}:${EnvironmentVariables.mongoPort}")

    @Throws(Exception::class)
    fun initialization() {
        try {
            client = KMongo.createClient(uri = uri)
            database = client.getDatabase(DATABASE_NAME)
            collection = database.getCollection()
        } catch (e: Exception) {
            throw e
        }
    }


}