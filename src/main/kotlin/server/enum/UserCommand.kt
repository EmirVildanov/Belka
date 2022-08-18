package server.enum

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import model.enum.UserState

enum class UserCommand(val commandName: String) {
    START("start"),
    ACCOUNT("account"),
    LOAD_PHOTO("load_photo"),
    OK("ok");

    // currentState to newState
    val allowedStateChanges: List<Pair<UserState, UserState>>
        get() = when (this) {
            START -> listOf(UserState.NOT_STARTED to UserState.MAIN_MENU)
            ACCOUNT -> listOf(UserState.MAIN_MENU to UserState.FILLING_ACCOUNT_INFO)
            else -> listOf()
        }

    // env -> isPassChecked to passNotCheckedMessage
    val preExecuteCheck: (env: MessageHandlerEnvironment) -> Pair<Boolean, String?>
        get() = when (this) {
            else -> { env: MessageHandlerEnvironment -> true to null }
        }
}
