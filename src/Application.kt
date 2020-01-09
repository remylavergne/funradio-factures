package dev.remylavergne

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    EnvironmentVariables.getEnvironmentVariables()
    Database.initialization()

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText(
                "HELLO WORLD! + email in environment variables : ${System.getenv("EMAIL_USER")}",
                contentType = ContentType.Text.Plain
            )
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
