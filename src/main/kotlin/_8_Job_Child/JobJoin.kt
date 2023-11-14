package _8_Job_Child

import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
//    val job1 = launch {
//        delay(1000)
//        println("Test1")
//    }
//
//    val job2 = launch {
//        delay(1000)
//        println("Test2")
//    }
//
//    job1.join()
//    job2.join()
//    println("All tests are done")

    launch {
        delay(1000)
        println("Test1")
    }
    launch {
        delay(1000)
        println("Test2")
    }

    val children = coroutineContext[Job]
        ?.children

    val childrenNum = children?.count()
    println("Number of children: $childrenNum")
    children?.forEach { it.join() }
    println("All tests are done")
}