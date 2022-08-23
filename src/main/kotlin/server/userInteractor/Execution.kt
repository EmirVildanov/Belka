package server.userInteractor

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import model.AccountInfo
import db.MongoDbConnector
import server.KeyboardCreator
import server.userInteractor.Execution.JustChangeStateCommandExecution
import server.userInteractor.Execution.NonStateChangeCommandExecution
import server.userInteractor.Execution.WithOnExecuteLogic
import server.userInteractor.Execution.WithPreExecuteLogic
import server.userInteractor.Execution.TextExecution
import server.userInteractor.FillAboutTextExecution.ABOUT_MAX_LENGTH
import server.userInteractor.FillNameTextExecution.NAME_MAX_LENGTH
import server.userInteractor.FillSurnameNameTextExecution.SURNAME_MAX_LENGTH
import server.userInteractor.UserCommand.ABOUT
import server.userInteractor.UserCommand.ACCOUNT
import server.userInteractor.UserCommand.BACK
import server.userInteractor.UserCommand.FIND
import server.userInteractor.UserCommand.NAME
import server.userInteractor.UserCommand.OK
import server.userInteractor.UserCommand.START
import server.userInteractor.UserCommand.SURNAME
import server.userInteractor.UserState.FILLING_ABOUT
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

    abstract class StateChangeExecution(open val keyboardCommentText: String, open val toState: UserState) :
        WithOnExecuteLogic, Execution() {
        private suspend fun changeState(env: MessageHandlerEnvironment, toState: UserState) {
            if (accountInfo == null) {
                throw AccountInfoNotObtainedException("obtainAccountInfo must be called before changing state.")
            }
            MongoDbConnector.changeAccountState(accountInfo!!.id, toState)
            val keyboardReplyMarkup = KeyboardCreator.createKeyboard(toState)
            val result = env.bot.sendMessage(
                text = keyboardCommentText,
                chatId = ChatId.fromId(env.message.chat.id),
                replyMarkup = keyboardReplyMarkup
            )
            result.fold({
            }, {
                env.bot.sendMessage(ChatId.fromId(env.message.chat.id), text = "Something went wrong :( $it")
            })
        }

        final override suspend fun onExecute(env: MessageHandlerEnvironment) {
            doInnerInnerJob(env)
            changeState(env, toState)
        }

        abstract suspend fun doInnerInnerJob(env: MessageHandlerEnvironment)
    }

    interface ICommandExecution {
        val command: UserCommand
    }

    // Command execution without state change. For example to print some user help info
    open class NonStateChangeCommandExecution(override val command: UserCommand) : ICommandExecution, Execution()

    // Command execution that just change accountInfo state
    abstract class StateChangeCommandExecution(
        override val command: UserCommand,
        override val keyboardCommentText: String,
        override val toState: UserState
    ) :
        ICommandExecution, StateChangeExecution(keyboardCommentText, toState)

    open class JustChangeStateCommandExecution(
        override val command: UserCommand,
        override val keyboardCommentText: String,
        override val toState: UserState
    ) :
        ICommandExecution, StateChangeCommandExecution(command, keyboardCommentText, toState) {
        final override suspend fun doInnerInnerJob(env: MessageHandlerEnvironment) {}
    }

    /**
     *  Server may receive some text from user:
     *      - Random text like name or age
     *      - Text that was serialized from data class as an option to choose
     *  TextExecution always changes state. Otherwise, why the fu*k you would like to get text from user?
     */
    abstract class TextExecution(override val keyboardCommentText: String, override val toState: UserState) :
        StateChangeExecution(keyboardCommentText, toState) {
        protected var text: String? = null

        fun obtainText(text: String) {
            this.text = text
        }

        final override suspend fun doInnerInnerJob(env: MessageHandlerEnvironment) {
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

object StartExecution : JustChangeStateCommandExecution(START, "Okay, let's start!", MAIN_MENU)
class BackExecution(toState: UserState) : JustChangeStateCommandExecution(BACK, "Back.", toState)
class OkExecution(toState: UserState) : JustChangeStateCommandExecution(OK, "Ok.", toState)

object HelpExecution : WithOnExecuteLogic, NonStateChangeCommandExecution(UserCommand.HELP) {
    private const val HELP_MESSAGE = "There is how this application works:\n" +
            "1.) You may create an application to share a ride with somebody\n" +
            "2.) You may check other's application and choose them\n" +
            "Please contact @<DevAcc> if you need any help."

    override suspend fun onExecute(env: MessageHandlerEnvironment) {
        env.sendMessage(HELP_MESSAGE)
    }
}

object FillAccountInfoExecution :
    JustChangeStateCommandExecution(ACCOUNT, "Let's fill accountInfo.", FILLING_ACCOUNT_INFO)

object FillNameCommandExecution : JustChangeStateCommandExecution(
    NAME,
    "Please send me your name. It must not be greater than $NAME_MAX_LENGTH letters.",
    FILLING_NAME
)

object FillSurnameCommandExecution : JustChangeStateCommandExecution(
    SURNAME,
    "Please send me your surname. It must not be greater than $SURNAME_MAX_LENGTH letters.",
    FILLING_SURNAME
)

object FillAboutCommandExecution : JustChangeStateCommandExecution(
    ABOUT,
    "Please fill about section. It must not be greater than $ABOUT_MAX_LENGTH symbols.",
    FILLING_ABOUT
)

object FillNameTextExecution : WithPreExecuteLogic, TextExecution("Name is changed.", FILLING_ACCOUNT_INFO) {
    const val NAME_MAX_LENGTH = 13;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.changeName(accountInfo!!.id, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (text!!.length > NAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $NAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FillSurnameNameTextExecution : WithPreExecuteLogic, TextExecution("Surname is changed.", FILLING_ACCOUNT_INFO) {
    const val SURNAME_MAX_LENGTH = 13;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.changeSurname(accountInfo!!.id, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (text!!.length > SURNAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $SURNAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FillAboutTextExecution : WithPreExecuteLogic, TextExecution("About is changed.", FILLING_ACCOUNT_INFO) {
    const val ABOUT_MAX_LENGTH = 150;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.changeAbout(accountInfo!!.id, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (text!!.length > ABOUT_MAX_LENGTH) {
            return PreExecuteResult.Error("About must be not be greater than $ABOUT_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FindExecution : WithPreExecuteLogic, JustChangeStateCommandExecution(FIND, "Let's find some ride!", STARTED) {
    override suspend fun preExecuteCheck(env: MessageHandlerEnvironment): PreExecuteResult {
        if (accountInfo!!.name == null || accountInfo!!.about == null) {
            return PreExecuteResult.Error(
                "You must fill fields [name], [about] in order to start working with applications."
            )
        }
        return PreExecuteResult.Ok
    }
}

private const val RATE_MIN_VALUE = 1
private const val RATE_MAX_VALUE = 10
private const val TEXT_FEEDBACK_SYMBOLS_BOUND = 150

private fun checkRateInBounds(rate: Int): Boolean {
    return rate in RATE_MIN_VALUE..RATE_MAX_VALUE;
}

private fun checkTextInBounds(text: String): Boolean {
    return text.length <= TEXT_FEEDBACK_SYMBOLS_BOUND;
}