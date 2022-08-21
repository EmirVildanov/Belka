package server.userInteractor

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import model.AccountInfo
import db.MongoDbConnector
import server.userInteractor.Execution.CommandExecution
import server.userInteractor.Execution.WithOnExecuteLogic
import server.userInteractor.Execution.WithPreExecuteLogic
import server.userInteractor.Execution.PreExecuteResult
import server.userInteractor.Execution.TextExecution
import server.userInteractor.UserInteractor.ExecutionSearchResult.NotFound
import server.userInteractor.UserInteractor.ExecutionSearchResult.Ok
import utils.CustomLogger

object UserInteractor {
    private const val MAX_PHOTO_SIZE_BYTES = 20 * 1024 * 1024

    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    sealed class ExecutionSearchResult<T> where T : Execution {
        class Ok<T : Execution>(val execution: T) : ExecutionSearchResult<T>()
        class NotFound<T : Execution> : ExecutionSearchResult<T>()
    }

    private fun getCommandExecution(
        currentState: UserState,
        command: UserCommand
    ): ExecutionSearchResult<CommandExecution> {
        val searchResult =
            currentState.allowedExecutions.filterIsInstance<CommandExecution>().find { it.command == command }
        searchResult?.let { return Ok(it) }
            ?: return NotFound()
    }

    /**
     * It's expected that every state that can except text can except only one textExecution
     */
    private fun getTextExecution(currentState: UserState): ExecutionSearchResult<TextExecution> {
        val allowedExecutions =
            currentState.allowedExecutions.filterIsInstance<TextExecution>()
        if (allowedExecutions.isEmpty()) {
            return NotFound()
        }
        return Ok(allowedExecutions.first())
    }

//    fun handleCommandAccount(env: CommandHandlerEnvironment) = scope.launch {
//        val keyboardButtonsList = listOf(listOf("/First", "/Second"), listOf("/Third"))
//        val keyboardReplyMarkup = KeyboardCreator.createTelegramKeyboard(keyboardButtonsList)
//        val result = env.bot.sendMessage(
//            chatId = ChatId.fromId(env.message.chat.id),
//            text = "Let's fill the account info!",
//            replyMarkup = keyboardReplyMarkup
//        )
//        result.fold({
//        }, {
//            env.bot.sendMessage(ChatId.fromId(env.message.chat.id), text = "Something went wrong :( $it")
//        })
//    }

    fun handleCommand(env: MessageHandlerEnvironment) = scope.launch {
        val commandName = env.message.text!!.drop(1)
        try {
            val command = UserCommand.valueOf(commandName.toUpperCasePreservingASCIIRules())
            var accountInfo = MongoDbConnector.getAccountInfo(env.getChatId().id)
            if (accountInfo == null) {
                accountInfo = MongoDbConnector.createNewAccount(env)
            }
            val currentState = accountInfo.state
            val executionResult = getCommandExecution(currentState, command)
            handleExecutionCycle(env, accountInfo, executionResult, "Such command is not accepted in your state.")
        } catch (e: IllegalArgumentException) {
            // thrown when trying to obtain valueOf UserCommand enum
            CustomLogger.logInfoMessage("User ${env.getUsername()} sent wrong command.")
            env.sendMessage("You sent a wrong command.")
        }
    }

    fun handleText(env: MessageHandlerEnvironment) = scope.launch {
        val accountInfo = MongoDbConnector.getAccountInfo(env.getChatId().id)
        val currentState = accountInfo!!.state
        val executionResult = getTextExecution(currentState)
        handleExecutionCycle(env, accountInfo, executionResult, "Such text is not accepted in your state.")
    }

    private suspend fun <T : Execution> handleExecutionCycle(
        env: MessageHandlerEnvironment,
        accountInfo: AccountInfo,
        executionResult: ExecutionSearchResult<T>,
        searchNotFoundMessage: String
    ) {
        if (executionResult is NotFound) {
            env.sendMessage(searchNotFoundMessage)
        } else {
            val execution = (executionResult as Ok).execution
            execution.obtainAccountInfo(accountInfo)
            if (execution is TextExecution) {
                val text = env.message.text
                if (text.isNullOrBlank()) {
                    env.bot.sendMessage(
                        env.getChatId(),
                        text = "You sent me empty message. I don't understand it."
                    )
                    return
                }
                execution.obtainText(text)
            }
            if (execution is WithPreExecuteLogic) {
                val preExecuteCheckResult = execution.preExecuteCheck(env)
                if (preExecuteCheckResult is PreExecuteResult.Error) {
                    env.sendMessage(preExecuteCheckResult.message)
                    return
                }
            }
            if (execution is WithOnExecuteLogic) {
                execution.onExecute(env)
            }
        }
    }

    fun handleDocument(env: MessageHandlerEnvironment) {
        env.bot.sendMessage(
            chatId = ChatId.fromId(env.message.chat.id),
            text = "Please send me photo not as a document (without compression)."
        )
    }

    fun handlePhoto(env: MessageHandlerEnvironment) {
        val fileId = env.message.photo!![0].fileId
        if (env.message.photo!![0].fileSize!! > MAX_PHOTO_SIZE_BYTES) {
            env.bot.sendMessage(env.getChatId(), "File size must be less than 20Mb.")
        }
    }
}

class WrongStateException(override val message: String?) : Exception()
