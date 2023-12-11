package _21_MakeFlow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


suspend fun test1() = coroutineScope {
    flowOf(1,2,3,4).collect{ print(it) }

    println()
    emptyFlow<Int>().collect { print(it) }

    listOf(1,2,3,4,5).asFlow().collect{ print(it)}
}

suspend fun test2() = coroutineScope {
    val function = suspend {
        delay(1000)
        "UserName"
    }
    function.asFlow().collect{ println(it) }
}

suspend fun getUserName() : String {
    delay(1000)
    return "UserName"
}

suspend fun test3() = coroutineScope {
    ::getUserName.asFlow().collect{ println(it) }
}

suspend fun test4() = coroutineScope {
    ::getUserName.asFlow().collect{ println(it) }
}

fun makeFlow(): Flow<Int> = flow {
    repeat(3) {num ->
        delay(1000)
        emit(num)
    }
}

suspend fun test5() = coroutineScope {
    makeFlow().collect{ println(it) }
}

fun <T> flow(
    block: suspend FlowCollector<T>.() -> Unit
):Flow<T> = object : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        collector.block()
    }
}

interface Flow<out T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun interface FlowCollector<in T>{
    suspend fun emit(value: T)
}
suspend fun test6() = coroutineScope {
    flow {
        emit("A")
        emit("B")
        emit("C")
    }.collect{
        value -> println(value)
    }

    flow(block = {
        emit("A")
        emit("B")
        emit("C")
    }).collect(
        collector = {
            value -> println(value)
        }
    )
}
fun main(): Unit = runBlocking {
    test6()
}