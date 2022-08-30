package db

import java.util.*
import model.AccountInfo
import model.AppFeedback
import model.Statistics
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import server.userInteractor.UserState

interface DbInterface {
    // insert
    suspend fun addNewAccount(accountId: UUID, chatId: Long?): AccountInfo
    suspend fun addNewStatistics(accountId: UUID): Statistics
    suspend fun addAppFeedback(fromAccountId: UUID, text: String): AppFeedback
    // get
    suspend fun getAccountInfo(accountId: UUID): AccountInfo?
    suspend fun getAllAccountInfo(): CoroutineFindPublisher<AccountInfo>
    // set
    suspend fun setAccountState(accountId: UUID, to: UserState)
    suspend fun setName(accountId: UUID, to: String)
    suspend fun setSurname(accountId: UUID, to: String)
    suspend fun setPhoto(accountId: UUID, to: String)
    suspend fun setAbout(accountId: UUID, to: String)
}
