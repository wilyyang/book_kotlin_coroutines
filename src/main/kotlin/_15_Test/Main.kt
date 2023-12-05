package _15_Test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.jetbrains.annotations.TestOnly
import kotlin.random.Random
import kotlin.system.measureTimeMillis


suspend fun test1()  = coroutineScope {
    val scheduler = TestCoroutineScheduler()

    println(scheduler.currentTime)
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime)
    scheduler.advanceTimeBy(1_000)
    println(scheduler.currentTime)
}

suspend fun test2() = coroutineScope {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        println("Some work 1")
        delay(1000)
        println("Some work 2")
        delay(1000)
        println("Coroutine done")
    }

    println("[${testDispatcher.scheduler.currentTime}] Before")
    testDispatcher.scheduler.advanceUntilIdle()
    println("[${testDispatcher.scheduler.currentTime}] After")
}

suspend fun test3() = coroutineScope {
    val testDispatcher = StandardTestDispatcher()

    runBlocking(testDispatcher) {
        delay(1)
        println("Coroutine done")
    }
}

suspend fun test4() = coroutineScope {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        delay(1)
        println("Done1")
    }

    CoroutineScope(testDispatcher).launch {
        delay(2)
        println("Done2")
    }

    testDispatcher.scheduler.advanceTimeBy(2)
    testDispatcher.scheduler.runCurrent()
}

suspend fun test5() = coroutineScope {
    val testDispatcher = StandardTestDispatcher()

    CoroutineScope(testDispatcher).launch {
        delay(2)
        print("Done")
    }

    CoroutineScope(testDispatcher).launch {
        delay(4)
        print("Done2")
    }

    CoroutineScope(testDispatcher).launch {
        delay(6)
        print("Done3")
    }

    for(i in 1..5){
        print(".")
        testDispatcher.scheduler.advanceTimeBy(1)
        testDispatcher.scheduler.runCurrent()
    }
}

suspend fun test6() = coroutineScope {
    val dispatcher = StandardTestDispatcher()

    CoroutineScope(dispatcher).launch {
        delay(1000)
        println("Coroutine done")
    }

    Thread.sleep(Random.nextLong(1000))

    val time = measureTimeMillis {
        println("[${dispatcher.scheduler.currentTime}] Before")
        dispatcher.scheduler.advanceUntilIdle()
        println("[${dispatcher.scheduler.currentTime}] After")
    }
    println("Took $time ms")
}

suspend fun test7() = coroutineScope {
    val scope = TestScope()

    scope.launch {
        delay(1000)
        println("First done")
        delay(1000)
        println("Coroutine done")
    }

    println("[${scope.currentTime}] Before")
    scope.advanceTimeBy(1000)
    scope.runCurrent()
    println("[${scope.currentTime}] Middle")
    scope.advanceUntilIdle()
    println("[${scope.currentTime}] After")
}


fun `should map async and keep elements order`() = runTest {
    coroutineContext
    currentCoroutineContext()
    val transforms = listOf(
        suspend { delay(3000); "A" },
        suspend { delay(2000); "B" },
        suspend { delay(4000); "C" },
        suspend { delay(1000); "D" }
    )
}

fun main(): Unit = runBlocking {
    test7()
}