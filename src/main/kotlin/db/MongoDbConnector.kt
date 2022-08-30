package db

import java.util.UUID
import model.AccountInfo
import model.AppFeedback
import model.Statistics
import model.UserReview
import org.bson.BsonInvalidOperationException
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import org.litote.kmongo.size
import server.userInteractor.UserState

object MongoDbConnector : DbInterface {
    private const val BELKA_DB_NAME = "belka"
    private const val ACCOUNT_COLLECTION_NAME = "accountinfo"
    private const val STATISTICS_COLLECTION_NAME = "statistics"
    private const val REVIEW_COLLECTION_NAME = "review"
    private const val APP_FEEDBACK_COLLECTION_NAME = "appfeedback"

    private lateinit var client: CoroutineClient
    private lateinit var db: CoroutineDatabase
    private lateinit var accountInfoCollection: CoroutineCollection<AccountInfo>
    private lateinit var statisticsCollection: CoroutineCollection<Statistics>
    private lateinit var reviewCollection: CoroutineCollection<UserReview>
    private lateinit var appFeedbackCollection: CoroutineCollection<AppFeedback>

    fun init() {
        client = KMongo.createClient().coroutine
        db = client.getDatabase(BELKA_DB_NAME)
        accountInfoCollection = db.getCollection()
        statisticsCollection = db.getCollection()
        reviewCollection = db.getCollection()
        appFeedbackCollection = db.getCollection()
    }

    suspend fun test() {
//        accountInfoCollection.insertOne(AccountInfo.MOCK_ACCOUNT)
        val res =
            accountInfoCollection.updateOne(AccountInfo::accountInfoId eq "UUIDString", setValue(AccountInfo::surname, "ABBA"))
        println(res)
    }

    // insert
    override suspend fun addNewAccount(chatId: Long?): AccountInfo {
        val newStatistics = addNewStatistics()
        val accountInfo = AccountInfo.createNewAccount(
            chatId = chatId,
            statisticsId = addNewStatistics(accountId).statisticsId,
        )
        accountInfoCollection.insertOne(accountInfo)
        return accountInfo
    }

    override suspend fun addNewStatistics(accountId: UUID): Statistics {
        val newStatistics = Statistics.createNewStatistics(accountId)
        statisticsCollection.insertOne(newStatistics)
        return newStatistics
    }

    override suspend fun addAppFeedback(fromAccountId: UUID, feedback: String): AppFeedback {
        val newFeedback = AppFeedback.createNewAppFeedBack(fromAccountId, feedback)
        appFeedbackCollection.insertOne(newFeedback)
        return newFeedback
    }

    // get
    override suspend fun getAccountInfo(accountId: UUID): AccountInfo? {
        try {
            return accountInfoCollection.findOne(AccountInfo::accountInfoId eq accountId)
        } catch (e: BsonInvalidOperationException) {
            throw BadDbRequestException("Problem with matching account schema in db and in request.\n$e")
        } catch (e: IllegalArgumentException) {
            throw BadDbRequestException("Probably trying to get an entry with missing field.\n$e")
        }
    }

    override suspend fun getAllAccountInfo(): CoroutineFindPublisher<AccountInfo> {
        return accountInfoCollection.find(AccountInfo::chatId gte 0)
    }
    // set
//    Don't know if we can use it properly
//    suspend fun <T> updateAccountInfoField(id: Long, property: KProperty<T>, value: T) {
//        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(property, value))
//    }
    override suspend fun setAccountState(accountId: UUID, to: UserState) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::state, to))
    }

    override suspend fun setName(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::name, to))
    }

    override suspend fun setSurname(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::surname, to))
    }

    override suspend fun setAbout(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::about, to))
    }

    override suspend fun setPhoto(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::photoFileId, to))
    }


    fun stop() {
        client.close()
    }
}

class BadDbRequestException(override val message: String) : Exception()
