package dev.remylavergne

import dev.remylavergne.models.Facture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MailScheduler(
    val facture: Facture,
    val delayMillis: Long,
    val repeatMillis: Long
) {

    private val timer: Job = startCoroutineTimer(delayMillis = 0, repeatMillis = 10000) {
        // some actions
    }

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) = GlobalScope.launch {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

}