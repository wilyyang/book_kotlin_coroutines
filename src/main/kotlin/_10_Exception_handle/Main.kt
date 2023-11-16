package _10_Exception_handle

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

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

suspend fun CoroutineScope.test6(){
    supervisorScope {
        launch {
            delay(1000)
            throw Error("Some error")
        }

        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    delay(1000)
    print("Done")
}

class MyException : Throwable()

suspend fun test7() = supervisorScope {
    val str1 = async<String> {
        delay(1000)
        throw MyException()
    }
    val str2 = async {
        delay(2000)
        "Text2"
    }

    try {
        println(str1.await())
    }catch(e : MyException){
        println(e)
    }
    println(str2.await())
}

object MyNonPropagationException : CancellationException()

suspend fun test8() = coroutineScope {
    launch {
        launch {
            delay(2000)
            println("Will not be printed")
        }
        throw MyNonPropagationException
    }
    launch {
        delay(2000)
        println("Will be printed")
    }
}

suspend fun test9() {
    val handler = CoroutineExceptionHandler{ ctx, exception ->
        println("Caught $exception")
    }
    val scope = CoroutineScope(SupervisorJob() + handler)
    scope.launch {
        delay(1000)
        throw Error("Some error")
    }

    scope.launch {
        delay(2000)
        println("Will be printed")
    }
    delay(3000)
}

fun main() : Unit  = runBlocking {
    test9()
}