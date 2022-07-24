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
import utils.CustomLogger
import utils.getResourcesFile
import java.util.*

class Server {
    private val logger = CustomLogger

    private lateinit var telegramBot: Bot
    private lateinit var dbEngine: DbEngine

    private fun readProperties() : String {
        val prop = Properties()
        prop.load(getResourcesFile(TELEGRAM_CONFIG_PROPERTIES_NAME))
        val accessToken = prop.getProperty("apiToken")
        return accessToken
    }

    fun start() {
        try {
            dbEngine = DbEngine()
            val accessToken = readProperties()
            telegramBot = bot {
                token = accessToken
                logLevel = LogLevel.Error

                dispatch {
                    command("account") {
                        handleCommandAccount(this)
                    }
                    command("test") {
                        handleCommandTest(this)
                    }
                    text {
                        bot.sendMessage(ChatId.fromId(message.chat.id), text = text)
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

    private fun handleCommandTest(env: CommandHandlerEnvironment) {
        val testAccountInfo = AccountInfo(1, "Emir", "Vildanov", 20, UserState.NOT_STARTED)
        dbEngine.testInsert(testAccountInfo)

        val secondsToWait = 2

        env.bot.sendMessage(ChatId.fromId(env.message.chat.id), text = "Added account to db. Will sleep $secondsToWait sec")

        Thread.sleep(secondsToWait * 1000L)

        val responseAccountInfo = dbEngine.testGet(testAccountInfo.id)
        val accountName = responseAccountInfo?.name ?: "nothing"
        env.bot.sendMessage(ChatId.fromId(env.message.chat.id), text = "Got $accountName as response")
    }

    private fun handleCommandAccount(env: CommandHandlerEnvironment) {
        val keyboardButtonsList = listOf(listOf("/First", "/Second"), listOf("/Third"))
        val keyboardReplyMarkup = KeyboardReplyMarkup.createSimpleKeyboard(keyboardButtonsList)
        val result = env.bot.sendMessage(
            chatId = ChatId.fromId(env.message.chat.id),
            text = "Let's fill the account info!",
            replyMarkup = keyboardReplyMarkup
        )
        result.fold({

        }, {
            env.bot.sendMessage(ChatId.fromId(env.message.chat.id), text = "Something went wrong :( $it")
        })
    }

    companion object {
        private val TELEGRAM_CONFIG_PROPERTIES_NAME = "telegramconfig.properties"
    }
}