package _10_Exception_handle

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main() : Unit  = runBlocking {
    test5()
}

suspend fun CoroutineScope.test1(){
    launch {
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }

        launch {
            delay(500)
            println("Will be printed")
        }
    }

    launch {
        delay(2000)
        println("Will not be printed")
    }
}

suspend fun CoroutineScope.test2(){
    try {
        launch {
            delay(1000)
            throw Error("Some error")
        }
    }catch(e : Throwable){
        println("Will not be printed")
    }

    launch {
        delay(2000)
        println("Will not be printed")
    }
}

suspend fun CoroutineScope.test3(){
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
        delay(1000)
        throw Error("Some error")
    }

    launch {
        delay(2000)
        println("Will be printed")
    }

    delay(3000)
}

suspend fun CoroutineScope.test4(){
    launch(SupervisorJob()){
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will not be printed")
        }
    }
    delay(3000)
}

suspend fun CoroutineScope.test5(){
    val job = SupervisorJob()
    launch(job) {
        delay(1000)
        throw Error("Some error")
    }

    launch(job) {
        delay(2000)
        println("Will be printed")
    }
    job.join()
}