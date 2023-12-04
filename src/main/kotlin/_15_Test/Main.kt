package _15_Test

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScheduler


suspend fun test1()  = coroutineScope {
    val scheduler = TestCoroutineScheduler()

    println(scheduler.currentTime)
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime)



}

fun main(): Unit = runBlocking {
    test1()
}