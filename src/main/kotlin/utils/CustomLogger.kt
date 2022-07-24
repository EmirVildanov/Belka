package utils

import mu.KotlinLogging

object CustomLogger {
    private val logger = KotlinLogging.logger {}

    fun logInfoMessage(message: String?) {
        logger.info { message }
    }

    fun logExceptionMessage(message: String?, exception: java.lang.Exception) {
        logger.error(exception) { message }
    }
}