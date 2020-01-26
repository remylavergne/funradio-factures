package dev.remylavergne

import dev.remylavergne.routing.create
import dev.remylavergne.routing.scheduling
import dev.remylavergne.routing.smtp
import dev.remylavergne.routing.smtplink
import dev.remylavergne.services.SchedulingService
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.routing
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.io.errors.IOException
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Location for uploading files.
 */
@KtorExperimentalLocationsAPI
@Location("/create") // TODO: rename /attachments/create
class Create

@KtorExperimentalLocationsAPI
@Location("/scheduling")
class Scheduling

@KtorExperimentalLocationsAPI
@Location("/smtp")
class Smtp

@KtorExperimentalLocationsAPI
@Location("/smtp/link")
class SmtpLink

@InternalCoroutinesApi
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    EnvironmentVariables.getEnvironmentVariables()
    Database.initialization()

    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
    // Allows to use classes annotated with @Location to represent URLs.
    // They are typed, can be constructed to generate URLs, and can be used to register routes.
    install(Locations)
    // Automatic '304 Not Modified' Responses
    install(ConditionalHeaders)
    // Supports for Range, Accept-Range and Content-Range headers
    install(PartialContent)
    // This feature enables compression automatically when accepted by the client.
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    val uploadDir = createUploadDirectory(environment)

    SchedulingService.init(uploadDir)

    routing {
        trace { application.log.warn(it.buildText()) }
        create(uploadDir)
        scheduling()
        smtp()
        smtplink()
    }
}

/**
 * Create a directory for all files uploaded
 * Configuration located in application.conf
 */
private fun createUploadDirectory(environment: ApplicationEnvironment): File {
    val facturesConfig = environment.config.config("factures")

    val uploadDirPath: String = facturesConfig.property("upload.dir").getString()
    val uploadDir = File(uploadDirPath)

    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }

    return uploadDir
}