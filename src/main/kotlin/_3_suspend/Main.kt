package _3_suspend

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun continueAfterSecond(continuation: Continuation<Unit>){
    thread {
        Thread.sleep(1000)
        continuation.resume(Unit)
    }
}

private val executor =
    Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }

suspend fun delay(timeMillis: Long): Unit =
    suspendCoroutine { cont ->
        executor.schedule({
            cont.resume(Unit)
        }, timeMillis, TimeUnit.MILLISECONDS)
    }

suspend fun main(){
    println("Before")

//    suspendCoroutine<Unit> {  continuation ->
//        println("Before too")
//        continuation.resumeWith(Result.success(Unit))
//    }

//    suspendCoroutine<Unit> {  continuation ->
//        thread {
//            println("Suspended")
//            Thread.sleep(1000)
//            continuation.resume(Unit)
//            println("Resumed")
//        }
//    }

//    suspendCoroutine<Unit> { continuation ->
//        continueAfterSecond(continuation)
//    }

//    suspendCoroutine<Unit> { continuation ->
//        executor.schedule({
//            continuation.resume(Unit)
//        }, 1000, TimeUnit.MILLISECONDS)
//    }

    delay(1000)

    println("After")
}