package server.userInteractor

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import model.AccountInfo
import db.MongoDbConnector
import server.KeyboardCreator
import server.userInteractor.Execution.JustChangeStateCommandExecution
import server.userInteractor.Execution.NonStateChangeCommandExecution
import server.userInteractor.Execution.StateChangeCommandExecution
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
import server.userInteractor.UserCommand.LEAVE_FEEDBACK
import server.userInteractor.UserCommand.NAME
import server.userInteractor.UserCommand.OK
import server.userInteractor.UserCommand.REFUSE
import server.userInteractor.UserCommand.START
import server.userInteractor.UserCommand.SURNAME
import server.userInteractor.UserState.ASKING_TO_RATE
import server.userInteractor.UserState.FILLING_ABOUT
import server.userInteractor.UserState.FILLING_ACCOUNT_INFO
import server.userInteractor.UserState.FILLING_NAME
import server.userInteractor.UserState.FILLING_SURNAME
import server.userInteractor.UserState.MAIN_MENU
import server.userInteractor.UserState.STARTED
import server.userInteractor.UserState.WRITING_APP_FEEDBACK
import utils.CustomLogger

/**
 * Class that represents action, called when user chose some variant from keyboard.
 * By the way you may see it as a Button class. As Execution logic is always linked with some button click
 */
sealed class Execution {
    protected lateinit var accountInfo: AccountInfo

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
            assert(this::accountInfo.isInitialized) { "obtainAccountInfo must be called before changing state." }
            MongoDbConnector.setAccountInfoState(accountInfo.accountInfoId, toState)
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
        protected lateinit var text: String

        fun obtainText(text: String) {
            this.text = text
        }

        final override suspend fun doInnerInnerJob(env: MessageHandlerEnvironment) {
            assert(this::text.isInitialized) { "obtainText must be called before handling text." }
            assert(this::accountInfo.isInitialized) { "obtainAccountInfo must be called before handling text." }
            handleTextInner(text)
        }

        abstract suspend fun handleTextInner(text: String)

        class TextNotObtainedException(message: String) : Exception(message)
    }

    class AccountInfoNotObtainedException(message: String) : Exception(message)
}
