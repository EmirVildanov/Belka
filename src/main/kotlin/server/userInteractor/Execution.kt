package server.userInteractor

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import model.AccountInfo
import db.MongoDbConnector
import server.KeyboardCreator
import server.userInteractor.Execution.CommandExecution.NonStateChangeCommandExecution
import server.userInteractor.Execution.CommandExecution.StateChangeCommandExecution
import server.userInteractor.Execution.CommandExecution.JustChangeStateCommandExecution
import server.userInteractor.Execution.WithOnExecuteLogic
import server.userInteractor.Execution.WithPreExecuteLogic
import server.userInteractor.Execution.TextExecution
import server.userInteractor.UserCommand.ACCOUNT
import server.userInteractor.UserCommand.BACK
import server.userInteractor.UserCommand.FIND
import server.userInteractor.UserCommand.NAME
import server.userInteractor.UserCommand.OK
import server.userInteractor.UserState.FILLING_ACCOUNT_INFO
import server.userInteractor.UserState.FILLING_NAME
import server.userInteractor.UserState.FILLING_SURNAME
import server.userInteractor.UserState.MAIN_MENU
import server.userInteractor.UserState.STARTED


/**
 * Class that represents action, called when user chose some variant from keyboard.
 * By the way you may see it as a Button class. As Execution logic is always linked with some button click
 */
sealed class Execution {
    protected var accountInfo: AccountInfo? = null

    fun obtainAccountInfo(accountInfo: AccountInfo) {
        this.accountInfo = accountInfo
    }

    sealed class PreExecuteResult {
        object Ok : PreExecuteResult()
        class Error(val message: String) :
            PreExecuteResult()
    }

    /** @preExecuteCheck called before any work. */
    interface WithPreExecuteLogic {
        suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult
    }
    /** @onExecute called before changing state if it's present. */
    interface WithOnExecuteLogic {
        suspend fun onExecute(env: MessageHandlerEnvironment)
    }

    abstract class ChangeStateExecution(open val toState: UserState) : WithOnExecuteLogic, Execution() {
        private suspend fun changeState(env: MessageHandlerEnvironment, toState: UserState) {
            if (accountInfo == null) {
                throw AccountInfoNotObtainedException("obtainAccountInfo must be called before changing state.")
            }
            MongoDbConnector.changeAccountState(accountInfo!!.id, toState)
            env.sendMessage("Send you new keyboard text.", KeyboardCreator.createKeyboard(toState))
        }

        final override suspend fun onExecute(env: MessageHandlerEnvironment) {
            doInnerInnerJob(env)
            changeState(env, toState)
        }

        abstract suspend fun doInnerInnerJob(env: MessageHandlerEnvironment)
    }


    sealed class CommandExecution(
        open val command: UserCommand,
    ) : Execution() {
        // Command execution without state change. For example to print some user help info
        open class NonStateChangeCommandExecution(override val command: UserCommand) : CommandExecution(command)

        // Command execution that just change accountInfo state
        abstract class StateChangeCommandExecution(open val command: UserCommand, override val toState: UserState) :
            WithOnExecuteLogic, ChangeStateExecution(toState)

        open class JustChangeStateCommandExecution(override val command: UserCommand, override val toState: UserState) :
            StateChangeCommandExecution(command, toState) {
            override suspend fun doInnerInnerJob(env: MessageHandlerEnvironment) { }
        }
    }

    /**
     *  Server may receive some text from user:
     *      - Random text like name or age
     *      - Text that was serialized from data class as an option to choose
     *  TextExecution always changes state. Otherwise, why the fu*k you would like to get text from user?
     */
    abstract class TextExecution(override val toState: UserState) : ChangeStateExecution(toState) {
        protected var text: String? = null

        fun obtainText(text: String) {
            this.text = text
        }

        override suspend fun doInnerInnerJob(env: MessageHandlerEnvironment) {
            if (text == null) {
                throw TextNotObtainedException("obtainText must be called before handling text.")
            }
            if (accountInfo == null) {
                throw AccountInfoNotObtainedException("obtainAccountInfo must be called before handling text.")
            }
            handleTextInner(text!!)
        }

        abstract suspend fun handleTextInner(text: String)

        class TextNotObtainedException(message: String) : Exception(message)
    }

    class AccountInfoNotObtainedException(message: String) : Exception(message)
}

object StartExecution : JustChangeStateCommandExecution(UserCommand.START, MAIN_MENU)
class BackExecution(toState: UserState) : JustChangeStateCommandExecution(BACK, toState)
class OkExecution(toState: UserState) : JustChangeStateCommandExecution(OK, toState)

object HelpExecution : WithOnExecuteLogic, NonStateChangeCommandExecution(UserCommand.HELP) {
    private const val HELP_MESSAGE = "There is how this application works:\n" +
            "1.) You may create an application to share a ride with somebody\n" +
            "2.) You may check other's application and choose them\n" +
            "Please contact @<DevAcc> if you need any help."

    override suspend fun onExecute(env: MessageHandlerEnvironment) {
        env.sendMessage(HELP_MESSAGE)
    }
}

object FillAccountInfoExecution : JustChangeStateCommandExecution(ACCOUNT, FILLING_ACCOUNT_INFO)
object FillNameCommandExecution : JustChangeStateCommandExecution(NAME, FILLING_NAME)
object FillSurnameCommandExecution : JustChangeStateCommandExecution(NAME, FILLING_SURNAME)

object FillNameExecution : WithPreExecuteLogic, TextExecution(FILLING_ACCOUNT_INFO) {
    private const val NAME_MAX_LENGTH = 10;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.changeName(accountInfo!!.id, text)
        MongoDbConnector.changeAccountState(accountInfo!!.id, FILLING_ACCOUNT_INFO)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (text!!.length <= NAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $NAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FillSurnameNameExecution : WithPreExecuteLogic, WithOnExecuteLogic, TextExecution() {
    private const val SURNAME_MAX_LENGTH = 10;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.changeSurname(accountInfo!!.id, text)
        MongoDbConnector.changeAccountState(accountInfo!!.id, UserState.FILLING_ACCOUNT_INFO)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (text!!.length <= SURNAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $SURNAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }

    override suspend fun onExecute(env: MessageHandlerEnvironment) {
        handleTextInner(text!!)
    }
}

object FindExecution : WithPreExecuteLogic, WithOnExecuteLogic, StateChangeCommandExecution(FIND, UserState.STARTED) {
    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (accountInfo!!.name == null || accountInfo!!.about == null || accountInfo!!.about == null) {
            return PreExecuteResult.Error("You must fill fields [name], [surname], [about] in order to start working with applications.")
        }
        return PreExecuteResult.Ok
    }

    override suspend fun onExecute(env: MessageHandlerEnvironment) {
        MongoDbConnector.changeAccountState(accountInfo!!.id, STARTED)
    }
}

private const val RATE_MIN_VALUE = 1
private const val RATE_MAX_VALUE = 10
private const val TEXT_FEEDBACK_SYMBOLS_BOUND = 150

fun checkRateInBounds(rate: Int): Boolean {
    return rate in RATE_MIN_VALUE..RATE_MAX_VALUE;
}

fun checkTextInBounds(text: String): Boolean {
    return text.length <= TEXT_FEEDBACK_SYMBOLS_BOUND;
}