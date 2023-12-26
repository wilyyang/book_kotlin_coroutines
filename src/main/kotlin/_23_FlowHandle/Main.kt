package _23_FlowHandle

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun test1() = coroutineScope {
    flowOf(1,2,3).map { it * it }.collect{ println(it)}
}

suspend fun test2() = coroutineScope {
    (1..10).asFlow().filter { it <= 5 }.filter { isEven(it) }.collect { print(it) }
}

fun isEven(num: Int): Boolean = num % 2 == 0

suspend fun test3() = coroutineScope {
    ('A'..'Z').asFlow().take(23).drop(5).collect{print(it)}
}

suspend fun test4() = coroutineScope {
    val ints: Flow<Int> = flowOf(1,2,3).onEach { delay(1000) }
    val doubles : Flow<Double> = flowOf(0.1, 0.2, 0.3)
    val together: Flow<Number> = merge(ints, doubles)
    together.collect{ println(it)}
}

suspend fun test5() = coroutineScope {
    val flow1 = flowOf("A", "B", "C").onEach { delay(400) }
    val flow2 = flowOf(1,2,3,4).onEach { delay(1000) }
    flow1.zip(flow2){f1, f2 -> "${f1}_${f2}"}.collect{println(it)}
}

suspend fun test6() = coroutineScope {
    val flow1 = flowOf("A", "B", "C").onEach { delay(400) }
    val flow2 = flowOf(1,2,3,4).onEach { delay(1000) }
    flow1.combine(flow2){f1, f2 -> "${f1}_${f2}"}.collect{println(it)}
}

suspend fun test7() = coroutineScope {
//    val list = listOf(1,2,3,4)
//    val res = list.fold(0) { acc, i -> acc +i}
//    println(res)
//    val res2 = list.fold(1){acc, i -> acc*i}
//    println(res2)

    val list = flowOf(1,2,3,4).onEach { delay(1000) }
    val res = list.fold(0){acc, i -> acc+i}
    println(res)
}

fun main(): Unit = runBlocking {
    test7()
}