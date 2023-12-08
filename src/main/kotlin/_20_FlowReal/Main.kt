package _20_FlowReal

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random

fun interface FlowCollector<T> {
    suspend fun emit(value: T)
}

interface Flow<T> {
    suspend fun collect(collector : FlowCollector<T>)
}

fun <T> flow(
    builder: suspend FlowCollector<T>.() -> Unit
) = object: Flow<T> {
    override suspend fun collect(
        collector: FlowCollector<T>
    ) {
        collector.builder()
    }
}

suspend fun test1() = coroutineScope {
    val f:Flow<String> = flow{
        emit("A")
        emit("B")
        emit("C")
    }

    f.collect { print(it) }
    f.collect { print(it) }
}

fun <T, R> Flow<T>.map(
    transformation: suspend (T) -> R
): Flow<R> = flow {
    collect{
        emit(transformation(it))
    }
}

suspend fun test2() = coroutineScope {
    flowOf("A", "B", "C")
        .map {
            delay(1000)
            it.lowercase()
        }
        .collect{ println(it)}
}

fun <T> Flow<T>.filter(
    predicate: suspend (T) -> Boolean
): Flow<T> = flow {
    collect{
        if(predicate(it)){
            emit(it)
        }
    }
}

fun <T> Flow<T>.onEach(
    action: suspend (T) -> Unit
): Flow<T> = flow {
    collect {
        action(it)
        emit(it)
    }
}

fun <T> Flow<T>.onStart(
    action: suspend () -> Unit
): Flow<T> = flow {
    action()
    collect {
        emit(it)
    }
}

suspend fun test3() = coroutineScope {
    flowOf("A", "B", "C")
        .onEach { delay(1000) }
        .collect{ println(it) }
}

fun <T, K> Flow<T>.distinctBy(
    keySelector: (T) -> K
) = flow {
    val sendKeys = mutableListOf<K>()
    collect{ value ->
        val key = keySelector(value)
        if(key !in sendKeys){
            sendKeys.add(key)
            emit(value)
        }
    }
}


fun main(): Unit = runBlocking {
    test3()
}