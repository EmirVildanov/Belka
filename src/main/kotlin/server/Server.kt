package server

import db.MongoDbConnector
import db.RedisConnector
import server.rides.RideInfoFetcher
import server.userInteractor.UserInteractor
import status.check.ServerStatusChecker
import utils.CustomLogger
import kotlin.system.exitProcess

object Server {
    private const val MONGODB_SERVER_START_SLEEP_TIME_MILLIS = 1000L;

    fun start() {
        try {
            init()
            TelegramBotProxy.startPolling()
        } catch (e: IllegalArgumentException) {
            CustomLogger.logExceptionMessage("Probably couldn't open properties file", e)
        }
    }

    private fun init() {
        TelegramBotProxy.init()
        RideInfoFetcher.init()
//        startMongoDbServer()
        MongoDbConnector.init()
//        RedisConnector.init()
        NetworkInteractor.init()
    }

    private fun startMongoDbServer() {
        ServerStatusChecker.runMongoDb()
        while (!ServerStatusChecker.isMongodbRunning()) {
            Thread.sleep(MONGODB_SERVER_START_SLEEP_TIME_MILLIS)
            continue
        }
    }

    fun stop() {
        TelegramBotProxy.stopPolling()
        CustomLogger.logInfoMessage("Bot stopped polling")
        MongoDbConnector.stop()
        RedisConnector.stop()
        NetworkInteractor.stop()
        exitProcess(0)
    }

    fun forceKill() {
        UserInteractor.stop()
        stop()
    }
}
