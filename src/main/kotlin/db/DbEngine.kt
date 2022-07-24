package db

import AccountInfo
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*

class DbEngine {
    private lateinit var client: MongoClient
    private lateinit var db: MongoDatabase
    private lateinit var accountsCollection: MongoCollection<AccountInfo>

    init {
        client = KMongo.createClient()
        db = client.getDatabase(ACCOUNTS_COLLECTION_NAME)
        accountsCollection = db.getCollection<AccountInfo>()
    }

    fun testInsert(accountInfo: AccountInfo) {
        accountsCollection.insertOne(accountInfo)
    }

    fun testGet(accountId: Int): AccountInfo? {
        return accountsCollection.findOne(AccountInfo::id eq accountId)
    }

    companion object {
        const val ACCOUNTS_COLLECTION_NAME = "accounts"
    }
}