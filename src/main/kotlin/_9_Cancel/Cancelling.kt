package _9_Cancel

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

suspend fun main(): Unit = coroutineScope {
    val job = Job()
//    launch (job){
//        try{
//            repeat(1_000){ i ->
//                delay(200)
//                println("Printing $i")
//            }
//        }catch(e : CancellationException){
//            println(e)
//            throw e
//        }finally {
//            println("Will always be printed")
//        }
//    }
//
//    delay(1100)
//    job.cancelAndJoin()
//    println("Cancelled successfully")
//    delay(1000)
    launch (job){
        try {
            delay(2000)
            println("Job is done")
        } finally {
            println("Finally")
            launch {
                println("Will not be printed")
            }
            delay(1000)
            println("Will not be printed")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancel done")
}