package _5_CoroutineBuilder

import kotlinx.coroutines.*

data class ArticleJson(val name : String)

suspend fun main() : Unit = coroutineScope {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    GlobalScope.launch {
        delay(4000L)
        println("4000L")
    }
}