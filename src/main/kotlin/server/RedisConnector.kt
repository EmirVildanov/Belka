package server

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import model.Application
import utils.Utils

object RedisConnector {
    private const val REDIS_CONFIG_FILE_NAME = "redis.properties"
    private const val REDIS_CONFIG_HOST_KEY_NAME = "host"
    private const val REDIS_CONFIG_PORT_KEY_NAME = "port"

    private lateinit var client: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>
    private lateinit var asyncCommands: RedisAsyncCommands<String, String>

    fun start() {
        val host = Utils.getProperty(REDIS_CONFIG_FILE_NAME, REDIS_CONFIG_HOST_KEY_NAME)
        val port = Utils.getProperty(REDIS_CONFIG_FILE_NAME, REDIS_CONFIG_PORT_KEY_NAME)


        client = RedisClient.create("redis://@$host:$port/")
        connection = client.connect()
        asyncCommands = connection.async()
    }

    fun setApplicationInfo(key: String, application: Application) {
        asyncCommands.set(key, Json.encodeToString(application))
    }

    fun getApplicationInfo(key: String): Application {
        return Json.decodeFromString<Application>(asyncCommands.get(key).get())
    }
}
