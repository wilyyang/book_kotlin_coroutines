package _8_Job_Child

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    println(job) // active
    job.complete()
    println(job) // completed

    val activeJob = launch {
        delay(1000)
    }

    println(activeJob) // active
    activeJob.join()
    println(activeJob) // completed

    val lazyJob = launch(start = CoroutineStart.LAZY){
        delay(1000)
    }
    println(lazyJob) // new
    lazyJob.start()
    println(lazyJob) // active
    lazyJob.join()
    println(lazyJob) // completed

    println(coroutineContext.job.isActive)
    println(">>>")
    val name = CoroutineName("Some name")
    val job2 = Job()

    val job3 = launch (name + job2){
        val childName = coroutineContext[CoroutineName]
        println(childName == name)
        val childJob = coroutineContext[Job]
        println(childJob == job2)
        println(childJob == job2.children.first())
    }
    job3.join()
}