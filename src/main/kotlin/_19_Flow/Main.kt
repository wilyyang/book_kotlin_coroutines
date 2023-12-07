package _19_Flow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.math.BigInteger

fun getList(): List<String> = List(3){
    Thread.sleep(1000)
    "User$it"
}

fun getSequence(): Sequence<String> = sequence {
    repeat(3){
        Thread.sleep(1000)
        yield("User$it")
    }
}

suspend fun test1()  = coroutineScope {
    val list = getList()
    println("Function started")
    list.forEach { println(it) }

    val seq = getSequence()
    println("Function started")
    seq.forEach { println(it) }
}

val fibonacci: Sequence<BigInteger> = sequence {
    var first = 0.toBigInteger()
    var second = 1.toBigInteger()

    while(true){
        yield(first)
        val temp = first
        first += second
        second = temp
    }
}

fun countCharactersInFile(path:String): Int =
    File(path).useLines { lines ->
        lines.sumBy { it.length }
    }

fun getSequence2() : Sequence<String> = sequence {
    repeat(3){
        Thread.sleep(1000)
        yield("User$it")
    }
}

suspend fun test2()  = coroutineScope {
    withContext(newSingleThreadContext("main")){
        launch {
            repeat(3){
                delay(100)
                println("Processing on coroutine")
            }
        }
        val list = getSequence2()
        list.forEach { println(it) }
    }
}

fun getFlow(): Flow<String> = flow {
    repeat(3){
        delay(1000)
        emit("User$it")
    }
}

suspend fun test3()  = coroutineScope {
    withContext(newSingleThreadContext("main")){
        launch {
            repeat(3){
                delay(100)
                println("Processing on coroutine")
            }
        }
        val list = getFlow()
        list.collect { println(it) }
    }
}


fun usersFlow() :Flow<String> = flow {
    repeat(3){
        delay(1000)
        val ctx = currentCoroutineContext()
        val name = ctx[CoroutineName]?.name
        emit("User$it in $name")
    }
}

suspend fun test4()  = coroutineScope {
    val users = usersFlow()
    withContext(CoroutineName("Name")){
        val job = launch {
            users.collect{ println(it)}
        }

        launch {
            delay(2100)
            println("I got enough")
            job.cancel()
        }
    }
}

fun main(): Unit = runBlocking {
    test4()
}