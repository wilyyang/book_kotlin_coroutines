package _18_HotCold

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.flow

suspend fun test1()  = coroutineScope {
    val l = buildList {
        repeat(3){
            add("User$it")
            println("L: Added User")
        }
    }

    val l2 = l.map {
        println("L: Processing")
        "Processed $it"
    }

    val s = sequence {
        repeat(3){
            yield("User$it")
            println("S: Added User")
        }
    }

    val s2 = l.map {
        println("S: Processing")
        "Processed $it"
    }
}

fun m(i:Int): Int {
    print("m$i ")
    return i * i
}

fun f(i:Int): Boolean {
    print("f$i ")
    return i >= 10
}

suspend fun test2() = coroutineScope {

    listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) }
        .find { f(it) }
        .let { print(it) }

    println()

    sequenceOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .map { m(it) }
        .find { f(it) }
        .let { print(it) }
}

fun m2(i: Int): Int {
    print("m$i ")
    return i * i
}

suspend fun test3() = coroutineScope {
    val l = listOf(1,2,3,4,5,6,7,8,9,10).map { m2(it) }
    println(l)
    println(l.find {it > 10})
    println(l.find {it > 10})
    println(l.find {it > 10})

    val s = sequenceOf(1,2,3,4,5,6,7,8,9,10).map { m2(it) }
    println(s.toList())
    println(s.find {it > 10})
    println(s.find {it > 10})
    println(s.find {it > 10})
}

private fun CoroutineScope.makeChannel() = produce {
    println("Channel started")
    for(i in 1..3){
        delay(1000)
        println("send $i")
        send(i)
    }
}

suspend fun test4() = coroutineScope {
    val channel = makeChannel()
    delay(1000)
    println("Calling channel...")
    for(value in channel){
        println(value)
    }
    println("Consuming again...")
    for(value in channel){
        println(value)
    }
}

private fun makeFlow() = flow {
    println("Flow started")
    for(i in 1..3){
        delay(1000)
        println("send $i")
        emit(i)
    }
}

suspend fun test5() = coroutineScope {
    val flow = makeFlow()
    delay(1000)
    println("Calling flow...")
    flow.collect { value -> println(value) }
    println("Consuming again...")
    flow.collect { value -> println(value) }
}

fun main(): Unit = runBlocking {
    test5()
}