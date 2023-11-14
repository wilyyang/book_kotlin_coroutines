package _8_Job_Child

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }

    job.children.forEach { it.join() }
    println("${job.isActive} ${job.isCancelled} ${job.isCompleted}")

    job.complete()
    job.join()
    println("Will not be printed")

    val job2 = launch(job) {
        delay(1000)
        println("Text 3")
    }
    println("job2 : ${job2.isActive} ${job2.isCompleted} ${job2.isCancelled}")
}