package _24_ShareStateFlow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun test1() = coroutineScope {
    flowOf(1,2,3,4).onEach { print(it) }.collect()
    println()
    flowOf(1,2,3,4).collect{ print(it)}
    println()
    flowOf(1,2).onEach { delay(1000) }.collect{ print(it)}

    println()
    flowOf(1,2).onEach { delay(1000) }.onStart { println("Before") }.collect{ print(it)}
}

fun main(): Unit = runBlocking {
    test1()
}