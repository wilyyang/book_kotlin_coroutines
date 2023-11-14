package _8_Job_Child.cancel

import kotlinx.coroutines.*

suspend fun main(): Unit = coroutineScope {
    val parentJob = Job()
    val job = Job(parent = parentJob)
    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    delay(1000)

    parentJob.cancel()
    job.children.forEach { it.join() }
}