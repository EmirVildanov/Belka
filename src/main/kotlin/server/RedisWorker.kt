package server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object RedisWorker {
    fun start() {
        runBlocking {
            val job  = launch(Dispatchers.Default) {
                newClient(Endpoint.from("127.0.0.1:6379")).use { client ->
                    client.set("foo", "100")
                    // prints 101
                    println("incremented value of foo ${client.incr("foo")}")
                    client.expire("foo", 3u) // set expiration to 3 seconds
                    delay(3000)
                    assert(client.get("foo") == null)
                    println("done")
                } // <--- the client/connection to redis is closed.
            }
            job.join() // wait for the co-routine to complete
            shutdown() // shutdown the Kreds Event loop.
        }
    }
}