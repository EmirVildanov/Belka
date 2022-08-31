package server.userInteractor

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.TelegramFile

// Should also consider it getAccountId()
fun MessageHandlerEnvironment.getChatId(): ChatId.Id {
    return ChatId.fromId(this.message.chat.id)
}

fun MessageHandlerEnvironment.getChatIdLong(): Long {
    return this.message.chat.id
}

fun MessageHandlerEnvironment.sendPhoto(fileId: String) {
    this.bot.sendPhoto(this.getChatId(), TelegramFile.ByFileId(fileId))
}

fun MessageHandlerEnvironment.getUsername(): String? {
    return this.message.chat.username
}

fun MessageHandlerEnvironment.sendMessage(message: String, keyboard: KeyboardReplyMarkup? = null) {
    this.bot.sendMessage(
        chatId = this.getChatId(),
        text = message,
        replyMarkup = keyboard
    )
}

fun MessageHandlerEnvironment.sendUsername(username: String) {
    this.sendMessage("@$username")
}