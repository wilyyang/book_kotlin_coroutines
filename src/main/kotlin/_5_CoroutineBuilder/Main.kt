package _5_CoroutineBuilder

import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }

    launch {
        delay(2000L)
        println("World!")
    }

    println("Hello,")
}