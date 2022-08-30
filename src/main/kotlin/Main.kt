import db.MongoDbConnector
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.util.CollectionNameFormatter
import server.Server
import server.userInteractor.UserInteractor
import kotlin.reflect.KClass

fun main() {
//    val server = Server
//    server.start()

    MongoDbConnector.init()
    runBlocking {
        MongoDbConnector.test()
    }
}
