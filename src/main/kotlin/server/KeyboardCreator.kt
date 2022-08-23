package server

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import server.userInteractor.UserCommand
import server.userInteractor.UserCommand.ABOUT
import server.userInteractor.UserCommand.ACCOUNT
import server.userInteractor.UserCommand.BACK
import server.userInteractor.UserCommand.FIND
import server.userInteractor.UserCommand.HELP
import server.userInteractor.UserCommand.NAME
import server.userInteractor.UserCommand.SURNAME
import server.userInteractor.UserState
import server.userInteractor.UserState.FILLING_ABOUT
import server.userInteractor.UserState.FILLING_ACCOUNT_INFO
import server.userInteractor.UserState.FILLING_NAME
import server.userInteractor.UserState.FILLING_SURNAME
import server.userInteractor.UserState.MAIN_MENU
import server.userInteractor.UserState.NOT_STARTED
import server.userInteractor.UserState.STARTED

fun String.toCommand(): String {
    return "/$this"

}

object KeyboardCreator {
    fun createKeyboard(state: UserState): KeyboardReplyMarkup {
        val listKeyboard = createListKeyboard(state)
        return createTelegramKeyboard(listKeyboard)
    }

    private fun createListKeyboard(state: UserState): List<List<UserCommand>> {
        return when (state) {
            MAIN_MENU -> listOf(listOf(ACCOUNT, HELP), listOf(FIND))
            FILLING_ACCOUNT_INFO -> listOf(listOf(NAME, SURNAME, ABOUT), listOf(BACK))
            FILLING_NAME -> listOf(listOf(BACK))
            FILLING_SURNAME -> listOf(listOf(BACK))
            FILLING_ABOUT -> listOf(listOf(BACK))
            STARTED -> listOf(listOf(BACK))
            else -> listOf()
        }
    }

    private fun createTelegramKeyboard(listKeyboard: List<List<UserCommand>>): KeyboardReplyMarkup {
        val fixedList = listKeyboard.map { commandList -> commandList.map { it.commandName.toCommand() } }
        return KeyboardReplyMarkup.createSimpleKeyboard(fixedList)
    }
}
