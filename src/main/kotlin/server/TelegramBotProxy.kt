package server

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.logging.LogLevel.Error
import db.MongoDbConnector
import kotlinx.coroutines.runBlocking
import server.userInteractor.UserInteractor
import utils.CustomLogger
import utils.Utils

object TelegramBotProxy {
    private const val TELEGRAM_CONFIG_TOKEN_KEY_NAME = "apiToken"
    private const val TELEGRAM_CONFIG_FILE_NAME = "telegramconfig.properties"

    private lateinit var accessToken: String
    private lateinit var telegramBot: Bot

    fun init() {
        accessToken = Utils.getProperty(TELEGRAM_CONFIG_FILE_NAME, TELEGRAM_CONFIG_TOKEN_KEY_NAME)
        telegramBot = bot {
            token = accessToken
            logLevel = Error

            dispatch {
                message {
                    if (message.photo != null) {
                        UserInteractor.handlePhoto(this)
                    } else if (message.document != null) {
                        UserInteractor.handleDocument(this)
                    } else if (Utils.textIsCommand(message.text)) {
                        UserInteractor.handleCommand(this)
                    } else {
                        UserInteractor.handleText(this)
                    }
                }
            }
        }
    }

    fun sendRecoverMessage() {
        runBlocking {
            val accountsInfo = MongoDbConnector.getAllAccountInfo()
            accountsInfo.consumeEach {
                println(it.age)
            }
        }
    }

    fun startPolling() {
        telegramBot.startPolling()
        CustomLogger.logInfoMessage("Bot started polling")
    }

    fun stopPolling() {
        telegramBot.stopPolling()
    }
}