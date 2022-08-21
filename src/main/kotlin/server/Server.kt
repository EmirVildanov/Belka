package server

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.logging.LogLevel
import db.MongoDbConnector
import db.RedisConnector
import server.userInteractor.UserInteractor
import serverCommunication.ServerCommunicator
import utils.CustomLogger
import utils.Utils
import utils.Utils.textIsCommand
import kotlin.system.exitProcess

object Server {
    private const val TELEGRAM_CONFIG_TOKEN_KEY_NAME = "apiToken"
    private const val TELEGRAM_CONFIG_FILE_NAME = "telegramconfig.properties"

    private lateinit var telegramBot: Bot
    private lateinit var accessToken: String

    fun start() {
        try {
            init()
            startBotPolling()
        } catch (e: IllegalArgumentException) {
            CustomLogger.logExceptionMessage("Probably couldn't open properties file", e)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun init() {
        accessToken = Utils.getProperty(TELEGRAM_CONFIG_FILE_NAME, TELEGRAM_CONFIG_TOKEN_KEY_NAME)
//        startMongoDbServer()
        MongoDbConnector.init()
//        RedisConnector.init()
        NetworkInteractor.init()
    }

    private fun startBotPolling() {
        telegramBot = bot {
            token = accessToken
            logLevel = LogLevel.Error

            dispatch {
                message {
                    if (message.photo != null) {
                        UserInteractor.handlePhoto(this)
                    } else if (message.document != null) {
                        UserInteractor.handleDocument(this)
                    } else if (textIsCommand(message.text)) {
                        UserInteractor.handleCommand(this)
                    } else {
                        UserInteractor.handleText(this)
                    }
                }
            }
        }
        telegramBot.startPolling()
        CustomLogger.logInfoMessage("Bot started polling")
    }

    private fun startMongoDbServer() {
        ServerCommunicator.runMongoDb()
        while (!ServerCommunicator.isMongodbRunning()) {
            Thread.sleep(1000)
            continue
        }
    }

    fun stop() {
        telegramBot.stopPolling()
        CustomLogger.logInfoMessage("Bot stopped polling")
        MongoDbConnector.stop()
        RedisConnector.stop()
        NetworkInteractor.stop()
        exitProcess(0)
    }
}
