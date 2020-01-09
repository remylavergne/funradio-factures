package dev.remylavergne

import org.simplejavamail.api.mailer.Mailer

interface EmailToSend {
    val email: Email
    val mailer: Mailer
}