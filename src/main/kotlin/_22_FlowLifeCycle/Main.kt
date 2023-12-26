package _22_FlowLifeCycle

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

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

val flow4 = flow {
    emit("Message1")
    emit("Message2")
}

suspend fun test5() = coroutineScope {
//    flow4.onStart { println("Before") }.catch { println("Caught $it") }.collect{ throw MyError() }
    flow4.onStart { println("Before") }.onEach{ throw MyError()}.catch { println("Caught $it") }.collect()
}

fun usersFLow(): Flow<String> = flow {
    repeat(2){
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("User$it in $name")
    }
}

suspend fun test6() = coroutineScope {
    val users = usersFLow()
    withContext(CoroutineName("Name1")){
        users.collect{ println(it)}
    }

    withContext(CoroutineName("Name2")){
        users.collect{ println(it)}
    }
}

suspend fun present(place : String, message : String){
    val ctx = coroutineContext
    val name = ctx[CoroutineName]?.name
    println("[$name] $message on $place")
}

fun messageFlow(): Flow<String> = flow {
    present("flow builder", "Message")
    emit("Message")
}

suspend fun test7() = coroutineScope {
    val users = messageFlow()
    withContext(CoroutineName("Name1")) {
        users.flowOn(CoroutineName("Name3"))
            .onEach { present("onEach", it) }
            .flowOn(CoroutineName("Name2"))
            .collect { present("collect", it) }
    }
}

suspend fun test8() = coroutineScope {
    flowOf("User1", "User2")
        .onStart { println("Users:") }
        .onEach { println(it)
        }
        .launchIn(this)
}

fun main(): Unit = runBlocking {
    test8()
}