package _6_CoroutineContext

import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

suspend fun printName(){
    println(coroutineContext[CoroutineName]?.name)
}

suspend fun main() = withContext(CoroutineName("Outer")){
    printName()
    launch(CoroutineName("Inner")) {
        printName()
    }
    delay(10)
    printName()
}