package db

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import kotlinx.coroutines.runBlocking
import model.AccountInfo
import model.Statistics
import server.userInteractor.UserState
import org.bson.BsonInvalidOperationException
import org.joda.time.DateTime
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.gte
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue
import server.userInteractor.getChatId

object MongoDbConnector: DbInterface {
    private const val BLA_BLA_ELKA_DB_NAME = "blablaelka"
    private const val ACCOUNT_COLLECTION_NAME = "account"
    private const val STATISTICS_COLLECTION_NAME = "statistics"

    private var client: CoroutineClient? = null
    private lateinit var db: CoroutineDatabase
    private lateinit var accountInfoCollection: CoroutineCollection<AccountInfo>
    private lateinit var statisticsCollection: CoroutineCollection<Statistics>


    fun init() {
        client = KMongo.createClient().coroutine
        db = client!!.getDatabase(BLA_BLA_ELKA_DB_NAME)
        runBlocking {
            val collections = db.listCollectionNames()
            if (!collections.contains(ACCOUNT_COLLECTION_NAME)) {
                db.createCollection(ACCOUNT_COLLECTION_NAME)
            }
            if (!collections.contains(STATISTICS_COLLECTION_NAME)) {
                db.createCollection(STATISTICS_COLLECTION_NAME)
            }
        }
        accountInfoCollection = db.getCollection()
        statisticsCollection = db.getCollection()
    }

    override suspend fun createNewAccount(env: MessageHandlerEnvironment): AccountInfo {
        val chatId = env.getChatId().id
        val accountInfo = AccountInfo(id = chatId, createdAt = DateTime.now(), statistics = createNewStatistics(chatId))
        accountInfoCollection.insertOne(accountInfo)
        return accountInfo
    }

    override suspend fun getAccountInfo(id: Long): AccountInfo? {
        try {
            return accountInfoCollection.findOne(AccountInfo::id eq id)
        } catch (e: BsonInvalidOperationException) {
            throw BadDbRequestException("Problem with matching account schema in db and in request.\n$e")
        } catch (e: IllegalArgumentException) {
            throw BadDbRequestException("Probably trying to get an entry with missing field.\n$e")
        }
    }

    override suspend fun changeAccountState(id: Long, to: UserState) {
        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(AccountInfo::state, to))
    }

    // Don't know if we can use it properly
//    suspend fun <T> updateAccountInfoField(id: Long, property: KProperty<T>, value: T) {
//        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(property, value))
//    }

    override suspend fun changeName(id: Long, to: String) {
        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(AccountInfo::name, to))
    }

    override suspend fun changeSurname(id: Long, to: String) {
        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(AccountInfo::surname, to))
    }

    override suspend fun createNewStatistics(userId: Long): Statistics {
        val newId = statisticsCollection.countDocuments(Statistics::id gte 1) + 1
        return Statistics(newId, userId, 0)
    }

    fun stop() {
        if (client != null) {
            client!!.close()
        }
    }
}

class BadDbRequestException(override val message: String) : Exception()
