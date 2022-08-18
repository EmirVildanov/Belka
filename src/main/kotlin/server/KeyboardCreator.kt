package server

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import model.enum.UserState

object KeyboardCreator {
    private fun createKeyboard(state: UserState): List<List<String>> {
        return when (state) {
            else -> listOf(listOf("string"), listOf("string"))
        }
    }


    fun createTelegramKeyboard(keyboardButtonsList: List<List<String>>): KeyboardReplyMarkup {
        return KeyboardReplyMarkup.createSimpleKeyboard(keyboardButtonsList)
    }
}
