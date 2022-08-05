package server.enum

import model.enum.UserState

data class StateMovement(val from: UserState, val to: UserState)

enum class UserCommand(val commandName: String) {
    START("start"),
    ACCOUNT("account");

    val allowedStateMovement: StateMovement
        get() = when (this) {
            START -> StateMovement(UserState.NOT_STARTED, UserState.MAIN_MENU)
            ACCOUNT -> StateMovement(UserState.MAIN_MENU, UserState.FILLING_ACCOUNT_INFO)
        }
}