package _9_Cancel

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch (job){
        repeat(1_000){ i ->
            delay(200)
            println("Printing $i")
        }
    }

    delay(1100)
//    job.cancel()
//    job.join()
    job.cancelAndJoin()
    println("Cancelled successfully")
}