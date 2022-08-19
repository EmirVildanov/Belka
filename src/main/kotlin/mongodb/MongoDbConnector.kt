package mongodb

import com.mongodb.client.model.UpdateOptions
import model.AccountInfo
import model.enum.UserState
import org.bson.BsonInvalidOperationException
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.setValue

object MongoDbConnector {
    const val BLA_BLA_ELKA_DB_NAME = "blablaelka"

    private var client: CoroutineClient? = null
    private lateinit var db: CoroutineDatabase
    private lateinit var accountInfoCollection: CoroutineCollection<AccountInfo>

    fun init() {
        client = KMongo.createClient().coroutine
        db = client!!.getDatabase(BLA_BLA_ELKA_DB_NAME)
        accountInfoCollection = db.getCollection()
    }

    suspend fun saveAccountInfo(info: AccountInfo) {
        accountInfoCollection.insertOne(info)
    }

    suspend fun getAccountInfo(id: Long): AccountInfo {
        try {
            return accountInfoCollection.findOne(AccountInfo::id eq id)
                ?: throw BadDbRequestException("No account with id $id")
        } catch (e: BsonInvalidOperationException) {
            throw BadDbRequestException("Problem with matching account schema in db and in request.\n$e")
        } catch (e: IllegalArgumentException) {
            throw BadDbRequestException("Probably trying to get an entry with missing field.\n$e")
        }
    }

    suspend fun changeAccountState(id: Long, to: UserState) {
        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(AccountInfo::state, to))
    }

    suspend fun changeName(id: Long, to: String) {
        accountInfoCollection.updateOne(AccountInfo::id eq id, setValue(AccountInfo::name, to))
    }

    suspend fun testInsert(accountInfo: AccountInfo) {
        accountInfoCollection.insertOne(accountInfo)
    }

    fun stop() {
        if (client != null) {
            client!!.close()
        }
    }
}

class BadDbRequestException(override val message: String) : Exception()
