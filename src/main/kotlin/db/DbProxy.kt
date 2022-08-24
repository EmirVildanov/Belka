package db

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import model.AccountInfo
import model.Statistics
import server.userInteractor.UserState

// TODO: Use Redis to store some information in cache
//  Replace MongoDbConnector calls with it
class DbProxy: DbInterface {
    override suspend fun createNewAccount(env: MessageHandlerEnvironment): AccountInfo {
        return MongoDbConnector.createNewAccount(env)
    }

    override suspend fun getAccountInfo(id: Long): AccountInfo? {
        return MongoDbConnector.getAccountInfo(id)
    }

    override suspend fun changeAccountState(id: Long, to: UserState) {
        MongoDbConnector.changeAccountState(id, to)
    }

    override suspend fun changeName(id: Long, to: String) {
        MongoDbConnector.changeName(id, to)
    }

    override suspend fun changeSurname(id: Long, to: String) {
        MongoDbConnector.changeSurname(id, to)
    }

    override suspend fun changePhoto(id: Long, to: String) {
        MongoDbConnector.changePhoto(id, to)
    }

    override suspend fun createNewStatistics(userId: Long): Long {
        return MongoDbConnector.createNewStatistics(userId)
    }

    override suspend fun changeAbout(id: Long, to: String) {
        MongoDbConnector.changeAbout(id, to)
    }
}