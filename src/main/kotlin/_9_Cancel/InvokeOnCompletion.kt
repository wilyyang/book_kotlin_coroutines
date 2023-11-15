package _9_Cancel

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

suspend fun main(): Unit = coroutineScope {
    test4()
}

suspend fun CoroutineScope.test1(){
    val job = launch {
        delay(Random.nextLong(2400))
        println("Finished")
    }
    delay(800)
    job.invokeOnCompletion { exception : Throwable? ->
        println("Will always be printed")
        println("The exception was : $exception")
    }
    delay(800)
    job.cancelAndJoin()
}

suspend fun CoroutineScope.test2(){
    val job = Job()
    launch(job) {
        repeat(1_000){ i ->
            Thread.sleep(200)
            println("Printing $i")
        }
    }
    delay(1000)
    job.cancelAndJoin()
    delay(1000)
}

suspend fun CoroutineScope.test3(){
    val job = Job()
    launch(job) {
        repeat(1_000){ i ->
            Thread.sleep(200)
            yield()
            println("Printing $i")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
    delay(1000)
}

suspend fun CoroutineScope.test4(){
//    val job = Job()
//    launch(job) {
//        do{
//            Thread.sleep(200)
//            println("Printing")
//        }while(isActive)
//    }
//    delay(1100)
//    job.cancelAndJoin()
//    println("Cancelled successfully")

    val job = Job()
    launch (job){
        repeat(1000){ num ->
            Thread.sleep(200)
            ensureActive()
            println("Printing $num")
        }
    }
    delay(1100)
    job.cancelAndJoin()
    println("Cancelled successfully")
}

suspend fun someTask() = suspendCancellableCoroutine<Unit> {
    cont ->
    cont.invokeOnCancellation {
    }
}