package model.enum

import server.enum.UserCommand

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    FILLING_ACCOUNT_INFO,
//    enum info
    FILLING_NAME,
    FILLING_SURNAME,
    FILLING_AGE,

//    text info


    START_WORKING,
    CHOOSING_DIRECTION;

    val randomTextAllowed: Boolean
        get() = when (this) {
            FILLING_NAME -> true
            FILLING_SURNAME -> true
            FILLING_AGE -> true
            else -> false
        }
}
