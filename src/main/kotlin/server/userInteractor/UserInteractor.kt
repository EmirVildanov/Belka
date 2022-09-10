package server.userInteractor

import db.MongoDbConnector
import db.NoGetException
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import model.AccountInfo
import server.MessageHandlerEnvironmentWrapper
import server.userInteractor.Execution.ICommandExecution
import server.userInteractor.Execution.PreExecuteResult
import server.userInteractor.Execution.TextExecution
import server.userInteractor.Execution.WithOnExecuteLogic
import server.userInteractor.Execution.WithPreExecuteLogic
import server.userInteractor.UserInteractor.ExecutionSearchResult.NotFound
import server.userInteractor.UserInteractor.ExecutionSearchResult.Ok
import utils.CustomLogger

object UserInteractor {
    private const val MAX_PHOTO_SIZE_BYTES = 20 * 1024 * 1024

    private val job: Job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    sealed class ExecutionSearchResult {
        class Ok(val execution: Execution) : ExecutionSearchResult()
        object NotFound : ExecutionSearchResult()
    }

    private fun getCommandExecution(
        currentState: UserState,
        command: UserCommand
    ): ExecutionSearchResult {
        val searchResult =
            currentState.allowedExecutions.filterIsInstance<ICommandExecution>().find { it.command == command }
        searchResult?.let { return Ok(it as Execution) } ?: return NotFound
    }

    /**
     * It's expected that every state that can except text can except only one textExecution
     */
    private fun getTextExecution(currentState: UserState): ExecutionSearchResult {
        val allowedExecutions =
            currentState.allowedExecutions.filterIsInstance<TextExecution>()
        if (allowedExecutions.isEmpty()) {
            return NotFound
        }
        return Ok(allowedExecutions.first())
    }

    fun handleCommand(env: MessageHandlerEnvironmentWrapper) = scope.launch {
        val accountInfo: AccountInfo = try {
            MongoDbConnector.getAccountInfo(env.getChatIdLong())
        } catch (e: NoGetException) {
            MongoDbConnector.addNewAccount(env.getChatIdLong())
        }
        val currentState = accountInfo.state

        val commandName = env.getMessageText()!!.drop(1)
        try {
            val command = UserCommand.valueOf(commandName.toUpperCasePreservingASCIIRules())
            val executionResult = getCommandExecution(currentState, command)
            handleExecutionCycle(env, accountInfo, executionResult, "Such command is not accepted in your state.")
        } catch (e: IllegalArgumentException) {
            // thrown when trying to obtain valueOf UserCommand enum
            CustomLogger.logInfoMessage("User ${env.getUsername()} sent wrong command.")
            env.sendMessage(currentState, "You sent a wrong command.")
        }
    }

    fun handleText(env: MessageHandlerEnvironmentWrapper) = scope.launch {
        val accountInfo: AccountInfo = try {
            MongoDbConnector.getAccountInfo(env.getChatIdLong())
        } catch (e: NoGetException) {
            MongoDbConnector.addNewAccount(env.getChatIdLong())
        }
        val currentState = accountInfo.state
        val executionResult = getTextExecution(currentState)
        handleExecutionCycle(env, accountInfo, executionResult, "Such text is not accepted in your state.")
    }

    private suspend fun handleExecutionCycle(
        env: MessageHandlerEnvironmentWrapper,
        accountInfo: AccountInfo,
        executionResult: ExecutionSearchResult,
        searchNotFoundMessage: String
    ) {
        val state = accountInfo.state
        if (executionResult is NotFound) {
            env.sendMessage(state, searchNotFoundMessage)
        } else {
            val execution = (executionResult as Ok).execution
            execution.obtainAccountInfo(accountInfo)
            if (execution is TextExecution) {
                val text = env.getMessageText()
                if (text.isNullOrBlank()) {
                    env.sendMessage(
                        state,
                        "You sent me empty message. I don't understand it."
                    )
                    return
                }
                execution.obtainText(text)
            }
            if (execution is WithPreExecuteLogic) {
                val preExecuteCheckResult = execution.preExecuteCheck(env)
                if (preExecuteCheckResult is PreExecuteResult.Error) {
                    env.sendMessage(state, preExecuteCheckResult.message)
                    return
                }
            }
            if (execution is WithOnExecuteLogic) {
                execution.onExecute(env)
            }
        }
    }

    fun handleDocument(env: MessageHandlerEnvironmentWrapper) = scope.launch {
        val accountInfo = MongoDbConnector.getAccountInfo(env.getChatIdLong())
        env.sendMessage(
            accountInfo.state,
            "Please send me photo not as a document (without compression)."
        )
    }

    fun handlePhoto(env: MessageHandlerEnvironmentWrapper) = scope.launch {
        val photo = env.getMessagePhoto()?.let { it[0] } ?: throw ResourcesHandlingException("Photo fot handling wasn't provided.")
        val photoSize = photo.fileSize ?: throw ResourcesHandlingException("Can't determine photo size.")
        val fileId = photo.fileId
        val accountInfo = MongoDbConnector.getAccountInfo(env.getChatIdLong())
        if (photoSize > MAX_PHOTO_SIZE_BYTES) {
            env.sendMessage(accountInfo.state, "File size must be less than 20Mb.")
        }
        MongoDbConnector.setAccountInfoPhoto(accountInfo.accountInfoId, fileId)
    }

    fun stop() {
        scope.cancel("Shutting down the server.")
    }
}

class ResourcesHandlingException(override val message: String?) : Exception(message)
class WrongStateException(override val message: String?) : Exception(message)
