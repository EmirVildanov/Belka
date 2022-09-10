package server

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.files.PhotoSize
import model.AccountInfo
import server.userInteractor.UserState

class MessageHandlerEnvironmentWrapper(private val env: MessageHandlerEnvironment) {
    fun getChatId(): ChatId.Id {
        return ChatId.fromId(env.message.chat.id)
    }

    fun getChatIdLong(): Long {
        return env.message.chat.id
    }

    fun getMessageText(): String? {
        return env.message.text
    }

    fun getMessagePhoto(): List<PhotoSize>? {
        return env.message.photo
    }

    fun sendPhoto(fileId: String) {
        env.bot.sendPhoto(this.getChatId(), TelegramFile.ByFileId(fileId))
    }

    fun getUsername(): String? {
        return env.message.chat.username
    }

    /**
     * Returns False in case of sending message error.
     */
    fun sendMessage(state: UserState, text: String) {
        val keyboard = KeyboardCreator.createKeyboard(state)
        val result = env.bot.sendMessage(
            chatId = getChatId(),
            text = text,
            replyMarkup = keyboard
        )
        result.fold({
        }, {
            env.bot.sendMessage(
                chatId = getChatId(),
                text = "Something went wrong :( $it",
                replyMarkup = keyboard
            )
            throw BadMessageSendException("Could not sent message $text.")
        })
    }

    fun sendUsername(state: UserState, username: String) {
        sendMessage(state, "@$username")
    }
}

class BadMessageSendException(message: String) : Exception(message)