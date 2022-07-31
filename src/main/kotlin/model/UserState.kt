package model

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    FILLING_ACCOUNT_INFO,
    FILLING_NAME,
    FILLING_SURNAME,

    START_WORKING,
    CHOOSING_DIRECTION;

    val allowedStateCommands: List<UserState>
        get() = when (this) {
            NOT_STARTED -> listOf(MAIN_MENU)

            MAIN_MENU -> listOf(FILLING_ACCOUNT_INFO)
            FILLING_ACCOUNT_INFO -> listOf()
            FILLING_NAME -> listOf()
            FILLING_SURNAME -> listOf()

            START_WORKING -> listOf()
            CHOOSING_DIRECTION -> listOf()
        }
}