package _8_Job_Child

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main(): Unit = runBlocking {
//    val job : Job = launch {
//        delay(1000)
//    }
//    val parentJob: Job = coroutineContext.job
//    println(job == parentJob)
//
//    val parentChildren: Sequence<Job> = parentJob.children
//    println(parentChildren.first() == job)

    launch (Job()){
        delay(1000L)
        println("Will not be printed")
    }
}