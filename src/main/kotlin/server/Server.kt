package server

import db.MongoDbConnector
import db.RedisConnector
import server.rides.RideInfoFetcher
import server.userInteractor.UserInteractor
import status.check.ServerStatusChecker
import utils.CustomLogger
import kotlin.system.exitProcess

object Server {
    fun start() {
        try {
            init()
            TelegramBotProxy.sendRecoverMessage()
            TelegramBotProxy.startPolling()
        } catch (e: IllegalArgumentException) {
            CustomLogger.logExceptionMessage("Probably couldn't open properties file", e)
        }
    }

    private fun init() {
        TelegramBotProxy.init()
        RideInfoFetcher.init()
        MongoDbConnector.init()
//        RedisConnector.init()
        NetworkInteractor.init()
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
