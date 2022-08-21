import db.RedisConnector
import server.Server

fun main() {
    val server = Server
//    server.start()
    RedisConnector.init()
    val info = RedisConnector.getApplicationInfo("1")
    println(info)
}
