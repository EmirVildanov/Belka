package server

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import db.DbEngine
import kotlinx.coroutines.*
import server.enum.UserCommand

object CommandHandler {

    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private suspend fun checkState(env: CommandHandlerEnvironment, command: UserCommand): Boolean {
        val userChatId = env.message.chat.id
        val accountInfo = DbEngine.getAccountInfo(userChatId)
        val userState = accountInfo.state
        if (command.allowedStateMovement.from != userState) {
            return false
        }
        return true
    }

    fun handleCommandAccount(env: CommandHandlerEnvironment) = scope.launch {
        if (!checkState(env, UserCommand.ACCOUNT)) {
            throw WrongStateException("Wrong state of user messaging. Smth wrong with the db.")
        }

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

    fun handleCommandStart(env: CommandHandlerEnvironment) {
        TODO("Not yet implemented")
    }
}

class WrongStateException(override val message: String?) : Exception()