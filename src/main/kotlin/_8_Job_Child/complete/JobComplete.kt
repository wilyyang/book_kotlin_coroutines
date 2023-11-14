package _8_Job_Child.complete

import kotlinx.coroutines.*

suspend fun main() = runBlocking {
    val job = Job()

//    launch (job){
//        repeat(5) {num ->
//            delay(200)
//            println("Rep$num")
//        }
//    }
//    launch {
//        delay(500)
////        job.complete()
//        job.completeExceptionally(Error("Some error"))
//    }
//    job.join()
//
//    launch(job) {
//        println("Will not be printed")
//    }
//    println("Done")

    launch(job) {
        delay(1000)
        println("Text 1")
    }
    launch(job) {
        delay(2000)
        println("Text 2")
    }
    job.complete()
    job.join()
    Unit
}