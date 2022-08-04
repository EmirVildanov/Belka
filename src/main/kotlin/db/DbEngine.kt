package db

import model.AccountInfo
import model.UserState
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.*

object DbEngine {
    const val BLA_BLA_ELKA_DB_NAME = "blablaelka"
    const val ACCOUNTS_COLLECTION_NAME = "accounts"

    private lateinit var client: CoroutineClient
    private lateinit var db: CoroutineDatabase
    private lateinit var accountsInfoCollection: CoroutineCollection<AccountInfo>

    /*
    Called from Server object from @start function to initialize db
     */
    fun init() {
        client = KMongo.createClient().coroutine
        db = client.getDatabase(BLA_BLA_ELKA_DB_NAME)
        accountsInfoCollection = db.getCollection()
    }

    suspend fun getAccountInfo(userId: Long): AccountInfo {
        return accountsInfoCollection.findOne(AccountInfo::id eq userId)
            ?: throw BadDbRequestException("No account with id $userId")
    }

    suspend fun changeUserState(userId: Long, userState: UserState) {
        TODO("Push to GitHub. Change OS.")
    }

    suspend fun testInsert(accountInfo: AccountInfo) {
        accountsInfoCollection.insertOne(accountInfo)
    }
}

class BadDbRequestException(override val message: String) : Exception()