package _12_Dispatcher

import _3_suspend.User
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
        val startTime = System.currentTimeMillis()

        launch (newSingleThreadContext("Thread2")){
            println("1.1 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
            delay(2000)
            println("1.2 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
            continuation?.resume(Unit)
            println("1.3 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
        }

        launch (Dispatchers.Unconfined){
            println("2.1 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")

            suspendCancellableCoroutine<Unit> {
                println("2.1.1 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
                continuation = it
                println("2.1.2 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
            }
            println("2.2 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
            delay(1000)
            println("2.3 "+ Thread.currentThread().name + " : ${ (System.currentTimeMillis() - startTime) / 1000f} s")
        }
    }
}

suspend fun showUser(user: User) = withContext(Dispatchers.Main.immediate){}

//public interface ContinuationInterceptor :
//    CoroutineContext.Element {
//    companion object Key : CoroutineContext.Key<ContinuationInterceptor>
//
//    fun <T> interceptContinuation(
//        continuation: Continuation<T>
//    ): Continuation<T>
//
//    fun releaseInterceptedContinuation(
//        continuation: Continuation<*>
//    ) {
//
//    }
//}

suspend fun test9() = coroutineScope {
//    val interceptor = object :ContinuationInterceptor{
//        override val key: CoroutineContext.Key<*>
//            get() = CoroutineContext.Key<ContinuationInterceptor>
//
//        override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
//            println("Intercept : ${continuation}")
//            return continuation
//        }
//    }
}

data class Order(val count : Int = 0, val customer: Int = 0)
data class Coffee(val order : Order)

fun cpu(order: Order): Coffee {
    var i = Int.MAX_VALUE
    while (i > 0) {
        i -= if (i % 2 == 0) 2 else 1
    }
    return Coffee(order.copy(customer = order.customer + i))
}

fun memory(order: Order):Coffee {
    val list = List(1_000){ it }
    val list2 = List(1_000){ list }
    val list3 = List(1_000){ list2 }
    return Coffee(
        order.copy(
            customer = order.customer + list3.hashCode()
        )
    )
}

fun blocking(order : Order) : Coffee {
    Thread.sleep(1000)
    return Coffee(order)
}

suspend fun suspending(order : Order) : Coffee {
    delay(1000)
    return Coffee(order)
}

suspend fun measureDispatcher(dispatcher: CoroutineDispatcher){
    withContext(dispatcher){
        val time1 = measureTimeMillis {
            cpu(Order())
        }

        val time2 = measureTimeMillis {
//            memory(Order())
        }

        val time3 = measureTimeMillis {
//            repeat(1_000){
//                async {
//                    blocking(Order())
//                }
//            }
        }

        val time4 = measureTimeMillis {
//            repeat(1_000){
//                launch {
//                    suspending(Order())
//                }
//            }
        }
        println("${time1/1000f} ms ${time2/1000f} ms ${time3/1000f} ms ${time4/1000f} ms")
    }
}

suspend fun test10(){
    measureDispatcher(Dispatchers.IO.limitedParallelism(1))
    measureDispatcher(Dispatchers.Default)
    measureDispatcher(Dispatchers.IO)
    measureDispatcher(Dispatchers.IO.limitedParallelism(100))
}


fun main(): Unit = runBlocking {
    test10()
}