package _22_FlowLifeCycle

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

suspend fun test2() = coroutineScope {
//    flowOf(1,2).onEach { delay(1000) }.onStart { println("Before") }.collect{ println(it)}

//    flowOf(1,2).onEach { delay(1000) }.onStart { emit(0) }.collect{ println(it)}

//    flowOf(1,2).onEach { delay(1000) }.onCompletion { println("Completed") }.collect{ println(it)}

    val job = launch {
        flowOf(1,2).onEach { delay(1000) }.onCompletion { println("Completed") }.collect{ println(it)}
    }

    delay(1100)
    job.cancel()

}

class MyError : Throwable("My error")

val flow = flow {
    emit(1)
    emit(2)
    throw MyError()
}

val flow2 = flow {
    emit("Message1")
    throw MyError()
}

suspend fun test3() = coroutineScope {
//        flow<List<Int>> { delay(1000) }.onEmpty{ emit(emptyList()) }.collect { println(it) }

//    flow.onEach { println("Got $it") }.catch { println("Caught $it") }.collect{ println("Collected $it")}

//    flow2.catch { emit("Error") }.collect{ println("Collected $it")}

    flowOf("Message1").catch { emit("Error") }.onEach { throw Error(it) }.collect { println("Collected $it")}
}

val flow3 = flow {
    emit("Message1")
    throw MyError()
}


suspend fun test4() = coroutineScope {
    try {
        flow3.collect { println("Collected $it")}
    }catch(e: MyError){
        println("Caught")
    }
}


fun main(): Unit = runBlocking {
    test4()
}