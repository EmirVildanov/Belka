package server

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.logging.LogLevel
import db.DbEngine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.AccountInfo
import model.UserState
import utils.CustomLogger
import utils.getResourcesFile
import java.util.*

object Server {
    private const val TELEGRAM_CONFIG_TOKEN_KEY_NAME = "apiToken"
    private const val TELEGRAM_CONFIG_PROPERTIES_NAME = "telegramconfig.properties"

    private val logger = CustomLogger
    private lateinit var telegramBot: Bot

    private fun readProperties(): String {
        val prop = Properties()
        prop.load(getResourcesFile(TELEGRAM_CONFIG_PROPERTIES_NAME))
        return prop.getProperty(TELEGRAM_CONFIG_TOKEN_KEY_NAME)
    }

    fun start() {
        try {
            DbEngine.init()
            val accessToken = readProperties()
            telegramBot = bot {
                token = accessToken
                logLevel = LogLevel.Error

                dispatch {
                    command(UserCommand.START.commandName) {
                        CommandHandler.handleCommandStart(this)
                    }
                    command(UserCommand.ACCOUNT.commandName) {
                        CommandHandler.handleCommandAccount(this)
                    }
                    text {
                        bot.sendMessage(ChatId.fromId(message.chat.id), text = "I am sorry. I don't know what to say")
                    }
                }
            }
            telegramBot.startPolling()
            logger.logInfoMessage("Bot started polling")
        } catch (e: IllegalArgumentException) {
            logger.logExceptionMessage("Probably couldn't open properties file", e)
        } catch (e: Exception) {
            throw e
        }
    }
}