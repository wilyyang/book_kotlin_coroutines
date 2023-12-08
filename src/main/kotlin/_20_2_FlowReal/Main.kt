package _20_2_FlowReal

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

fun Flow<*>.counter() = flow<Int> {
    var counter = 0

    collect {
        counter++
        List(100) { Random.nextLong() }.shuffled().sorted()
//        println(counter)
        emit(counter)
    }
}

suspend fun test4() = coroutineScope {
    val f1 = List(1000) { "$it"}.asFlow()
    val f2 = List(1000) { "$it"}.asFlow().counter()


    launch { println(f1.counter().last()) }
    launch { println(f1.counter().last()) }
    launch { println(f2.last()) }
    launch { println(f2.last()) }
}

fun Flow<*>.counter2(): Flow<Int> {
    var counter = 0

    return this.map {
        counter++
        List(100) { Random.nextLong() }.shuffled().sorted()
        counter
    }
}

suspend fun test5() = coroutineScope {
    val f1 = List(1000) { "$it"}.asFlow()
    val f2 = List(1000) { "$it"}.asFlow().counter2()


    launch { println(f1.counter2().last()) }
    launch { println(f1.counter2().last()) }
    launch { println(f2.last()) }
    launch { println(f2.last()) }
}

var counter = 0

fun Flow<*>.counter3(): Flow<Int> = this.map{
    counter++
    List(100) { Random.nextLong() }.shuffled().sorted()
    counter
}

suspend fun test6() = coroutineScope {
    val f1 = List(1000) { "$it"}.asFlow()
    val f2 = List(1000) { "$it"}.asFlow().counter3()


    launch { println(f1.counter3().last()) }
    launch { println(f1.counter3().last()) }
    launch { println(f2.last()) }
    launch { println(f2.last()) }
}

fun main(): Unit = runBlocking {
    test6()
}