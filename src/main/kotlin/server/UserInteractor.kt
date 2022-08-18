package server

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import mongodb.MongoDbConnector
import kotlinx.coroutines.*
import model.enum.UserState
import server.enum.UserCommand
import utils.CustomLogger
import utils.Utils

object UserInteractor {

    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private suspend fun getUserChange(command: UserCommand, userState: UserState): UserState? {
        return command.allowedStateChanges.find { it.first == userState }?.second
    }

    fun handleCommandAccount(env: CommandHandlerEnvironment) = scope.launch {
        val keyboardButtonsList = listOf(listOf("/First", "/Second"), listOf("/Third"))
        val keyboardReplyMarkup = KeyboardCreator.createTelegramKeyboard(keyboardButtonsList)
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

    fun handleCommand(env: MessageHandlerEnvironment) = scope.launch {
        val commandName = env.message.text!!.drop(1)
        try {
            val command = UserCommand.valueOf(commandName)
            val accountInfo = MongoDbConnector.getAccountInfo(getChatId(env).id)
            val currentState = accountInfo.state
            val newState = getUserChange(command, currentState)
            if (newState == null) {
                sendMessage(env, "This command is not allowed in your state.")
            }
            val commandPreExecuteCheckResult = command.preExecuteCheck(env)
            if (!commandPreExecuteCheckResult.first) {
                val checkMessage = commandPreExecuteCheckResult.second
                checkMessage?.also { sendMessage(env, it) } ?: throw Exception("Checked failed, but message is null")
            }
        } catch (e: IllegalArgumentException) {
            CustomLogger.logInfoMessage("User ${getUsername(env)} sent wrong command")
            sendMessage(env, "You sent a wrong command")
        }
    }

    fun handleText(env: MessageHandlerEnvironment) = scope.launch {
        val messageText = env.message.text
        if (messageText.isNullOrBlank()) {
            env.bot.sendMessage(
                getChatId(env),
                text = "You sent me empty message. I don't understand it."
            )
        }
        val accountInfo = MongoDbConnector.getAccountInfo(getChatId(env).id)
        val currentState = accountInfo.state
        if (!currentState.randomTextAllowed) {
            env.bot.sendMessage(
                getChatId(env),
                text = "I don't accept random text in current state."
            )
        }
    }

    fun handleDocument(env: MessageHandlerEnvironment) {
        env.bot.sendMessage(
            chatId = ChatId.fromId(env.message.chat.id),
            text = "Please send me photo not as a document (without compression)."
        )
    }

    fun handlePhoto(env: MessageHandlerEnvironment) {
        val fileId = env.message.photo!![0].fileId
        if (env.message.photo!![0].fileSize!! / (1024 * 1024) > 20) {
            env.bot.sendMessage(getChatId(env), "File size must be less than 20Mb.")
        }
    }

    private fun getUsername(env: MessageHandlerEnvironment): String? {
        return env.message.chat.username
    }

    private fun getChatId(env: MessageHandlerEnvironment): ChatId.Id {
        return ChatId.fromId(env.message.chat.id)
    }

    private fun sendPhoto(env: MessageHandlerEnvironment, fileId: String) {
        env.bot.sendPhoto(getChatId(env), TelegramFile.ByFileId(fileId))
    }

    private fun sendMessage(env: MessageHandlerEnvironment, message: String) {
        env.bot.sendMessage(getChatId(env), message)
    }

    private fun sendUsername(env: MessageHandlerEnvironment, username: String) {
        sendMessage(env, "@$username")
    }
}

class WrongStateException(override val message: String?) : Exception()
