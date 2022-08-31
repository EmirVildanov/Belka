package db

import java.util.*
import model.AccountInfo
import model.AppFeedback
import model.Statistics
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import server.userInteractor.UserState

interface DbInterface {
    // insert
    suspend fun addNewAccount(chatId: Long?): AccountInfo
    suspend fun addNewStatistics(accountId: UUID): Statistics
    suspend fun addAppFeedback(fromAccountId: UUID, text: String): AppFeedback
    // get
    suspend fun getAccountInfo(accountId: UUID): AccountInfo?
    suspend fun getAccountInfo(chatId: Long?): AccountInfo?
    suspend fun getAllAccountInfo(): CoroutineFindPublisher<AccountInfo>
    // set
    suspend fun setAccountInfoState(accountId: UUID, to: UserState)
    suspend fun setAccountInfoName(accountId: UUID, to: String)
    suspend fun setAccountInfoSurname(accountId: UUID, to: String)
    suspend fun setAccountInfoPhoto(accountId: UUID, to: String)
    suspend fun setAccountInfoAbout(accountId: UUID, to: String)
}
