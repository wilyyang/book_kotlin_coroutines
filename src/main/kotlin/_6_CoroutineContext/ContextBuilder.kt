package _6_CoroutineContext

import kotlinx.coroutines.*

fun CoroutineScope.log(msg : String){
    val name = coroutineContext[CoroutineName]?.name
    println("[$name] $msg")
}

fun main() = runBlocking(CoroutineName("main")) {
    log("Started")
    val v1 = async(CoroutineName("c1")) {
        delay(500)
        log("Running async")
        42
    }
    launch(CoroutineName("c2")) {
        delay(1000)
        log("Running launch")
    }
    log("The answer is ${v1.await()}")
}