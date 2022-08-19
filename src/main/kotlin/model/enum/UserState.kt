package model.enum

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import mongodb.MongoDbConnector
import server.UserInteractor
import server.enum.UserCommand

class StateChangePreExecuteResult(val status: Boolean, val errorMessage: String?)

open class StateChange(
    val command: UserCommand,
    val to: UserState,
    val preExecuteCheck: ((env: MessageHandlerEnvironment) -> StateChangePreExecuteResult)? = null,
    val onExecuteChange: ((env: MessageHandlerEnvironment) -> Unit)? = null
)

//abstract class StateChangeWithPreExecuteLogic(override val command: UserCommand, override val to: UserState) :
//    StateChange(command, to) {
//
//    abstract val preExecuteCheck: (env: MessageHandlerEnvironment) -> StateChangePreExecuteResult
//}
//
//abstract class StateChangeWithOnExecuteLogic(override val command: UserCommand, override val to: UserState) :
//    StateChange(command, to) {
//
//    abstract val onExecuteChange: (env: MessageHandlerEnvironment) -> Unit
//}
//
//abstract class StateChangeWithPreOnExecuteLogic(override val command: UserCommand, override val to: UserState) :
//    StateChange(command, to) {
//
//    abstract val preExecuteCheck: (env: MessageHandlerEnvironment) -> StateChangePreExecuteResult
//    abstract val onExecuteChange: (env: MessageHandlerEnvironment) -> Unit
//}

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    FILLING_ACCOUNT_INFO,
//      text info
        FILLING_NAME,
        FILLING_SURNAME,
        FILLING_AGE,
//      enum info
//        FILLING_UNIVERSITY_IDK
    STARTED,
    CHOOSING_DIRECTION;

    val allowedStateChanges: List<StateChange>
        get() = when (this) {
            NOT_STARTED -> listOf(StateChange(UserCommand.START, MAIN_MENU))
            MAIN_MENU -> listOf(StateChange(UserCommand.ACCOUNT, FILLING_ACCOUNT_INFO))
            FILLING_ACCOUNT_INFO -> listOf(StateChange(UserCommand.NAME, FILLING_NAME, ::checkNameChange, ::fillingNameChange))
            else -> listOf()
        }

    val randomTextAllowed: Boolean
        get() = when (this) {
            FILLING_NAME -> true
            FILLING_SURNAME -> true
            FILLING_AGE -> true
            else -> false
        }

    companion object {
        const val NAME_MAX_LENGTH = 10;
    }
}

fun checkNameChange(env: MessageHandlerEnvironment): StateChangePreExecuteResult {
    val maxNameLength = UserState.NAME_MAX_LENGTH
    return StateChangePreExecuteResult(env.message.text!!.length <= maxNameLength, "User name must be not be greater than $maxNameLength")
}

suspend fun fillingNameChange(env: MessageHandlerEnvironment) {
    val accountInfo = MongoDbConnector.getAccountInfo(UserInteractor.getChatId(env).id)
    val currentState = accountInfo.state
    MongoDbConnector.changeName(accountInfo.id, TODO(WHAT&!&!&!&))
    MongoDbConnector.changeAccountState(accountInfo.id, UserState.FILLING_ACCOUNT_INFO)
}
