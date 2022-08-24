package db

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import model.AccountInfo
import model.Statistics
import server.userInteractor.UserState

interface DbInterface {
    suspend fun createNewAccount(env: MessageHandlerEnvironment): AccountInfo
    suspend fun getAccountInfo(id: Long): AccountInfo?
    suspend fun changeAccountState(id: Long, to: UserState)
    suspend fun changeName(id: Long, to: String)
    suspend fun changeSurname(id: Long, to: String)
    suspend fun changePhoto(id: Long, to: String)
    suspend fun createNewStatistics(userId: Long): Long
    suspend fun changeAbout(id: Long, to: String)
    suspend fun addAppFeedback(fromId: Long, text: String): Long
}
