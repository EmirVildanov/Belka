package db

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import model.Application
import utils.Utils
import utils.customJson

object RedisConnector {
    private const val REDIS_CONFIG_FILE_NAME = "redis.properties"
    private const val REDIS_CONFIG_HOST_KEY_NAME = "host"
    private const val REDIS_CONFIG_PORT_KEY_NAME = "port"

    private lateinit var client: RedisClient
    private var connection: StatefulRedisConnection<String, String>? = null
    private lateinit var asyncCommands: RedisAsyncCommands<String, String>

    fun init() {
        val host = Utils.getProperty(REDIS_CONFIG_FILE_NAME, REDIS_CONFIG_HOST_KEY_NAME)
        val port = Utils.getProperty(REDIS_CONFIG_FILE_NAME, REDIS_CONFIG_PORT_KEY_NAME)

        client = RedisClient.create("redis://@$host:$port/")
        connection = client.connect()
        asyncCommands = connection!!.async()
    }

    fun setApplicationInfo(applicationId: Long, application: Application) {
        asyncCommands.set(applicationId.toString(), customJson.encodeToString(application))
    }

    fun getApplicationInfo(key: String): Application? {
        val res = asyncCommands.get(key).get()
        return res?.let { customJson.decodeFromString(it) }
    }

    fun stop() {
        if (connection != null) {
            connection!!.close()
            client.shutdown()
        }
    }
}
