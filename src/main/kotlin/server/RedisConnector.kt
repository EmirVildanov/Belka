package server

import io.lettuce.core.RedisClient

object RedisConnector {
    fun start() {
        val redisClient = RedisClient
            .create("redis://@localhost:6379/")
        val connection = redisClient.connect()
    }
}
