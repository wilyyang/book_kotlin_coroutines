package _24_ShareStateFlow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun test1() = coroutineScope {

    val mutableSharedFlow = MutableSharedFlow<String>(replay = 0)

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
        }
    }

    launch {
        mutableSharedFlow.collect {
            println("#2 received $it")
        }
    }

    delay(1000)
    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")
}

suspend fun test2() = coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>(replay = 2)
    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")
    mutableSharedFlow.emit("Message3")

    println(mutableSharedFlow.replayCache)

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
        }
    }

    delay(100)
    mutableSharedFlow.resetReplayCache()
    println(mutableSharedFlow.replayCache)
}

//interface MutableSharedFlow<T> : SharedFlow<T>, FlowCollector<T> {
//    fun tryEmit(value : T): Boolean
//    val subscriptionCount: StateFlow<Int>
//    fun resetReplayCache()
//}
//
//interface SharedFlow<out T> : Flow<T> {
//    val replayCache: List<T>
//}
//
//interface FlowCollector<in T>  {
//    suspend fun emit(value : T)
//}

suspend fun test3() = coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>()
    val sharedFlow: SharedFlow<String> = mutableSharedFlow
    val collector: FlowCollector<String> = mutableSharedFlow

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
        }
    }

    launch {
        sharedFlow.collect {
            println("#2 received $it")
        }
    }

    delay(1000)
    mutableSharedFlow.emit("Message1")
    collector.emit("Message2")
}

suspend fun test4() = coroutineScope {
    val flow = flowOf("A", "B", "C").onEach { delay(1000) }
    val sharedFlow: SharedFlow<String> = flow.shareIn(scope = this, started = SharingStarted.Eagerly)

    delay(500)
    launch {
        sharedFlow.collect{ println("#1 $it")}
    }
    delay(1000)
    launch {
        sharedFlow.collect{ println("#2 $it")}
    }
    delay(1000)
    launch {
        sharedFlow.collect{ println("#3 $it")}
    }
}

suspend fun test5() = coroutineScope {
    val flow = flowOf("A", "B", "C")

    val sharedFlow: SharedFlow<String> = flow.shareIn(scope = this, started = SharingStarted.Eagerly)

    delay(100)
    launch {
        sharedFlow.collect{ println("#1 $it")}
    }
    print("Done")
}

suspend fun test6() = coroutineScope {
    val flow1 = flowOf("A", "B", "C")
    val flow2 = flowOf("D", "E").onEach { delay(1000) }

    val sharedFlow = merge(flow1, flow2).shareIn(
        scope = this,
        started = SharingStarted.Lazily,
        replay = 1
    )

    delay(100)
    launch {
        sharedFlow.collect{ println("#1 $it")}
    }
    delay(1000)
    launch {
        sharedFlow.collect{ println("#2 $it")}
    }
    delay(1000)
    launch {
        sharedFlow.collect{ println("#3 $it")}
    }
}

suspend fun test7() = coroutineScope {
    val flow = flowOf("A", "B", "C", "D")
        .onStart { println("Started") }
        .onCompletion { println("Finished") }
        .onEach { delay(1000) }

    val sharedFlow = flow.shareIn(
        scope = this,
        started = SharingStarted.WhileSubscribed(),
    )

    delay(3000)
    launch {
        println("#1 ${sharedFlow.first()}")
    }
    launch {
        println("#2 ${sharedFlow.take(2).toList()}")
    }

    delay(3000)
    launch {
        println("#3 ${sharedFlow.first()}")
    }
}

//interface StateFlow<out T> : SharedFlow<T> {
//    val value : T
//}
//
//interface MutableStateFlow<T> :
//    StateFlow<T>, MutableSharedFlow<T> {
//    override var value: T
//    fun compareAndSet(expect: T, update: T): Boolean
//}

suspend fun test8() = coroutineScope {
    val state = MutableStateFlow("A")
    println(state.value)
    launch {
        state.collect{ println("Value changed to $it")}
    }
    delay(1000)
    state.value = "B"

    delay(1000)
    launch {
        state.collect{ println("and now it is $it") }
    }
    delay(1000)
    state.value = "C"
}

suspend fun test9():Unit = coroutineScope {
    val state = MutableStateFlow('X')
    launch {
        for (c in 'A'..'E') {
            delay(300)
            state.value = c
        }
    }

    state.collect {
        delay(1000)
        println(it)
    }
}

suspend fun test10(): Unit = coroutineScope {
    val flow = flowOf("A", "B", "C").onEach { delay(1000) }.onEach { println("Produced $it") }
    val stateFlow: StateFlow<String> = flow.stateIn(this)

    println("Listening")
    println(stateFlow.value)
    stateFlow.collect { println("Received $it") }
}

suspend fun test11(): Unit = coroutineScope {
    val flow = flowOf("A", "B").onEach { delay(1000) }.onEach { println("Produced $it") }
    val stateFlow: StateFlow<String> = flow.stateIn(
        scope = this,
        started = SharingStarted.Lazily,
        initialValue = "Empty"
    )

    println(stateFlow.value)
    delay(2000)
    stateFlow.collect { println("Received $it") }
}

fun main(): Unit = runBlocking {
    test11()
}