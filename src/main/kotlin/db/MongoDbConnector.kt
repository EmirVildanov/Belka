package db

import com.mongodb.MongoClientSettings
import java.util.*
import model.AccountInfo
import model.AppFeedback
import model.Statistics
import model.UserReview
import org.bson.BsonInvalidOperationException
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import server.userInteractor.UserState

object MongoDbConnector : DbInterface {
    private const val BELKA_DB_NAME = "belka"

    private lateinit var client: CoroutineClient
    private lateinit var db: CoroutineDatabase
    private lateinit var accountInfoCollection: CoroutineCollection<AccountInfo>
    private lateinit var statisticsCollection: CoroutineCollection<Statistics>
    private lateinit var reviewCollection: CoroutineCollection<UserReview>
    private lateinit var appFeedbackCollection: CoroutineCollection<AppFeedback>

    fun init() {
        /** Need this for MongoDbFormatter can encode and decode UUID. */
        val clientSettings = MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).build()
        client = KMongo.createClient(clientSettings).coroutine
        db = client.getDatabase(BELKA_DB_NAME)
        accountInfoCollection = db.getCollection()
        statisticsCollection = db.getCollection()
        reviewCollection = db.getCollection()
        appFeedbackCollection = db.getCollection()
    }

    suspend fun test() {
        accountInfoCollection.insertOne(
            AccountInfo.createNewAccount(
                1
            )
        )
//        val res =
//            accountInfoCollection.updateOne(
//                AccountInfo::accountInfoId eq UUID.fromString(""),
//                setValue(AccountInfo::surname, "ABBA")
//            )
//        println(res)
    }

    // insert
    override suspend fun addNewAccount(chatId: Long?): AccountInfo {
        val accountInfo = AccountInfo.createNewAccount(
            chatId = chatId,
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
    override suspend fun getAccountInfo(accountId: UUID): AccountInfo {
        try {
            return accountInfoCollection.findOne(AccountInfo::accountInfoId eq accountId)
                ?: throw NoGetException("Didn't find account.")
        } catch (e: BsonInvalidOperationException) {
            throw BadDbRequestException("Problem with matching account schema in db and in request.\n$e")
        } catch (e: IllegalArgumentException) {
            throw BadDbRequestException("Probably trying to get an entry with missing field.\n$e")
        }
    }

    override suspend fun getAccountInfo(chatId: Long?): AccountInfo {
        try {
            return accountInfoCollection.findOne(AccountInfo::chatId eq chatId)
                ?: throw NoGetException("Didn't find account.")
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
    override suspend fun setAccountInfoState(accountId: UUID, to: UserState) {
        val result =
            accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::state, to))
        if (result.matchedCount == 0L) {
            throw NoSetException("Could not set new state as didn't find account.")
        }
    }

    override suspend fun setAccountInfoName(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::name, to))
    }

    override suspend fun setAccountInfoSurname(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::surname, to))
    }

    override suspend fun setAccountInfoAbout(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::about, to))
    }

    override suspend fun setAccountInfoPhoto(accountId: UUID, to: String) {
        accountInfoCollection.updateOne(AccountInfo::accountInfoId eq accountId, setValue(AccountInfo::photoFileId, to))
    }

    fun stop() {
        client.close()
    }
}

class BadDbRequestException(override val message: String) : Exception(message)
class NoGetException(override val message: String) : Exception(message)
class NoSetException(override val message: String) : Exception(message)
