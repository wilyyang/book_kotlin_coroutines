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

suspend fun test8() = coroutineScope {

    val list = listOf(1,2,3,4)
    val res = list.scan(0){ acc, i ->  acc+i}
    println(res)

    flowOf(1, 2, 3, 4).onEach { delay(1000) }.scan(0) { acc, v -> acc + v }.collect{ println(it)}
}

fun flowFrom(elem : String) = flowOf(1,2,3).onEach { delay(1000) }.map{ "${it}_${elem}"}

suspend fun test9() = coroutineScope {
    flowOf("A", "B", "C").flatMapConcat { flowFrom(it) }.collect{ println(it) }
    flowOf("A123", "B456", "C789").flatMapConcat { it -> it.toList().asFlow().onEach { delay(1000) } }.collect{ println(it) }
}

suspend fun test10() = coroutineScope {
    flowOf("A", "B", "C").flatMapMerge { flowFrom(it) }.collect{ println(it) }
    flowOf("A123", "B456", "C789").flatMapMerge { it -> it.toList().asFlow().onEach { delay(1000) } }.collect{ println(it) }
}

suspend fun test11() = coroutineScope {
    flowOf("A", "B", "C").flatMapMerge(concurrency = 2) { flowFrom(it)}.collect{ println(it)}
}

suspend fun test12() = coroutineScope {
    flowOf("A", "B", "C").flatMapLatest { flowFrom(it) }.collect{ println(it) }
    flowOf("A123", "B456", "C789").flatMapLatest { it -> it.toList().asFlow().onEach { delay(1000) } }.collect{ println(it) }
}

suspend fun test13() = coroutineScope {
    flowOf("A", "B", "C").onEach { delay(1200) }.flatMapLatest { flowFrom(it) }.collect{println(it)}
}

suspend fun test14() = coroutineScope {
    flow {
        emit(1)
        emit(2)
        error("E")
        emit(3)
    }.retry(3) {
        println(it.message)
        true
    }.collect { print(it) }
}

suspend fun test15() = coroutineScope {
    flowOf(1,2,2,3,2,1,1,3).distinctUntilChanged().collect{print(it)}
}

data class User(val id:Int, val name:String){
    override fun toString(): String ="[$id] $name"
}

suspend fun test16() = coroutineScope {
    val users = flowOf(
        User(1, "Alex"),
        User(1, "Bob"),
        User(2, "Bob"),
        User(2, "Celine")
    )

    println(users.distinctUntilChangedBy { it.id }.toList())
    println(users.distinctUntilChangedBy { it.name }.toList())

    println(users.distinctUntilChanged { prev, next -> prev.id == next.id || prev.name == next.name }.toList())
}

suspend fun test17() = coroutineScope {
    val flow = flowOf(1, 2, 3, 4).map { it * it }

    println(flow.first())
    println(flow.count())

    println(flow.reduce { acc, value -> acc * value })
    println(flow.fold(0) { acc, value -> acc + value })
}

fun main(): Unit = runBlocking {
    test17()
}