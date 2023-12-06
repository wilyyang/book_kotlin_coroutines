package _18_HotCold

import kotlinx.coroutines.*

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

fun main(): Unit = runBlocking {
    test2()
}