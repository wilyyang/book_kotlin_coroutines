package _12_Dispatcher

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.random.Random
import kotlin.system.measureTimeMillis

suspend fun test1()  = coroutineScope {
    repeat(1000){
        launch(Dispatchers.Default) {
            List(1000) { Random.nextLong()}.maxOrNull()

            val threadName = Thread.currentThread().name
            println("Running on thread: $threadName")
        }
    }
}

class SomeTest {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
//    @Before
//    fun setup(){
//        Dispatchers.setMain(dispatcher)
//    }
//
//    @After
//    fun tearDown(){
//        Dispatchers.resetMain()
//        dispatcher.close()
//    }
}

suspend fun test2() = coroutineScope {
    val time = measureTimeMillis {
        coroutineScope {
            repeat(50){
                launch (Dispatchers.IO){
                    Thread.sleep(1000)
                }
            }
        }
    }
    println(time)
}

suspend fun test3() = coroutineScope {
    repeat(1000){
        launch(Dispatchers.IO) {
            Thread.sleep(200)
            val threadName = Thread.currentThread().name
            println("Running on thread : $threadName")
        }
    }
}

suspend fun test4() = coroutineScope {
    launch(Dispatchers.Default) {
        println(Thread.currentThread().name)
        withContext(Dispatchers.IO){
            println(Thread.currentThread().name)
        }
    }
}

suspend fun printCoroutinesTime(
    dispatcher: CoroutineDispatcher
){
    val test = measureTimeMillis {
        coroutineScope {
            repeat(100){
                launch(dispatcher){
                    Thread.sleep(1000)
                }
            }
        }
    }
    println("$dispatcher took: $test")
}

val NUMBER_OF_THREADS = 20
val dispatcher = Executors.newFixedThreadPool(NUMBER_OF_THREADS).asCoroutineDispatcher()

suspend fun test5() = coroutineScope {
    launch(Dispatchers.Default) {
        printCoroutinesTime(Dispatchers.IO)
    }

    launch {
        val dispatcher = Dispatchers.IO.limitedParallelism(100)
        printCoroutinesTime(dispatcher)
    }
    dispatcher.close()
}

var i = 0
val dispatcher2 = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
suspend fun test6() = coroutineScope {
    val dispatcher = Dispatchers.Default.limitedParallelism(1)

    repeat(10_000) {
        launch(dispatcher) {
            i++
        }
    }
    delay(1000)
    println(i)
}

suspend fun test7() = coroutineScope {
    val dispatcher = Dispatchers.Default.limitedParallelism(1)

    val job = Job()
    repeat(5) {
        launch(dispatcher + job) {
            Thread.sleep(1000)
        }
    }
    job.complete()
    val time = measureTimeMillis { job.join() }
    println("Took $time")
}

suspend fun test8() = coroutineScope {
    withContext(newSingleThreadContext("Thread1")){
        var continuation: Continuation<Unit>? = null

        launch (newSingleThreadContext("Thread2")){
            delay(1000)
            continuation?.resume(Unit)
        }

        launch (Dispatchers.Unconfined){
            println(Thread.currentThread().name)

            suspendCancellableCoroutine<Unit> {
                continuation = it
            }
            println(Thread.currentThread().name)
            delay(1000)
            println(Thread.currentThread().name)
        }
    }
}


fun main(): Unit = runBlocking {

    test8()
}