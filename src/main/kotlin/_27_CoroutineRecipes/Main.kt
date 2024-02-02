package _27_CoroutineRecipes

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration

suspend fun <T, R> List<T>.mapAsync(
    concurrencyLimit: Int = Int.MAX_VALUE,
    transformation: suspend (T) -> R
): List<R> = coroutineScope {
    val semaphore = Semaphore(concurrencyLimit)
    this@mapAsync.map {
        async {
            semaphore.withPermit {
                transformation(it)
            }
        }
    }.awaitAll()
}

suspend fun test1() = coroutineScope {
    val result1 = listOf(1,2,3,4,5).mapAsync { it*it }.maxBy { it }
    println(result1)
    val result2 = listOf(1,2,3,4,5).mapAsync { it*it }.filterNot { it % 2 != 0 }.sortedBy { it }
    println(result2)
}

suspend fun getOffers(
    categories: List<String>
): List<String> = coroutineScope {
    categories
        .map { async { "$it :: $it" } }
        .map { it.await() }
}

fun <T> suspendLazy(
    initializer: suspend () -> T
): suspend () -> T {
    var initializer: (suspend () -> T)? = initializer
    val mutex = Mutex()
    var holder: Any? = Any()

    return {
        if (initializer == null) holder as T
        else mutex.withLock {
            initializer?.let {
                holder = it()
                initializer = null
            }
            holder as T
        }
    }
}

suspend fun makeConnection() :String{
    println("Creating connection")
    delay(1000)
    return "Connection"
}
val getConnection = suspendLazy { makeConnection() }

data class UserData(val name : String)
val userData: suspend() -> UserData = suspendLazy {
    println("Creating User")
    delay(1000)
    UserData(name = "양동국")
}
suspend fun test2() = coroutineScope {
    println(getConnection())
    println(getConnection())
    println(getConnection())

    println(userData())
    println(userData())
    println(userData())
}

class ConnectionPool<K, V>(
    private val scope: CoroutineScope,
    private val replay: Int = 0,
    private val stopTimeOut: Duration? = null,
    private val replayExpiration: Duration? = null,
    private val builder: (K) -> Flow<V>,
) {
    private val connections = mutableMapOf<K, Flow<V>>()

    fun getConnection(key: K): Flow<V> = synchronized(this) {
        connections.getOrPut(key) {
            builder(key).shareIn(
                scope,
                started = if (stopTimeOut != null && replayExpiration != null) SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = stopTimeOut.inWholeMilliseconds,
                    replayExpirationMillis = replayExpiration.inWholeMilliseconds
                ) else SharingStarted.WhileSubscribed(),
                replay = replay,
            )
        }
    }
}

private val scope = CoroutineScope(SupervisorJob())
private val messageConnections =
    ConnectionPool(scope) { threadId: String ->
        flowOf("$threadId 1", "$threadId 2", "$threadId 3", "$threadId 4")
            .onStart { println("Start") }
            .onEach { delay(1000) }
    }

fun observeMessageThread(threadId: String) = messageConnections.getConnection(threadId)

suspend fun test3() = coroutineScope {
    val flow1 = observeMessageThread("adb")
    val flow2 = observeMessageThread("shell")

    launch {
        flow1.collect {
            println(it)
        }
    }

    launch {
        flow2.collect {
            println(it)
        }
    }
}

suspend fun <T> raceOf(
    racer: suspend CoroutineScope.() -> T,
    vararg racers : suspend CoroutineScope.() -> T
): T = coroutineScope {
    select {
        (listOf(racer) + racers).forEach { racer ->
            async { racer() }.onAwait {
                coroutineContext.job.cancelChildren()
                it
            }
        }
    }
}

suspend fun a(): String {
    delay(1000)
    return "A"
}

suspend fun b(): String {
    delay(2000)
    return "B"
}

suspend fun c(): String {
    delay(3000)
    return "C"
}

suspend fun test4() = coroutineScope {
    println(raceOf({ c() }))
    println(raceOf({ b() }, { a() }))
    println(raceOf({ b() }, { c() }))
    println(raceOf({ b() }, { a() }, { c() }))
}

inline fun <T> retry2(operation: () -> T): T {
    while(true){
        try{
            return operation()
        }catch (e :Throwable){

        }
    }
}

suspend fun requestData() : String {
    if(Random.nextInt(0, 10) == 0){
        return "ABC"
    }
    else {
        error("Error")
    }
}

inline fun <T> retryWhen(
    predicate: (Throwable, retries: Int) -> Boolean,
    operation: () -> T
): T {
    var retries = 0
    var fromDownstream : Throwable? = null
    while(true){
        try {
            return operation()
        }catch (e:Throwable){
            if(fromDownstream != null){
                e.addSuppressed(fromDownstream)
            }
            fromDownstream = e
            if(e is CancellationException || !predicate(e, retries++)){
                throw e
            }
        }
    }
}

suspend fun requestWithRetry() = retryWhen(
    predicate = { e, retries ->
        delay(100L)
        println("Retried")
        retries < 20 && e is IllegalStateException
    }
){
    requestData()
}

suspend fun test5() = coroutineScope {
    println(requestWithRetry())
}

inline suspend fun <T> retry (
    operation: () -> T
): T {
    var retries = 0
    while(true){
        try {
            return operation()
        }catch (e:Throwable){
            delay(100L)
            if(e is CancellationException || retries >= 10){
                throw e
            }
            retries++
            println("Retrying")
        }
    }
}

suspend fun requestWithRetry2() = retry{
    requestData()
}

suspend fun test6() = coroutineScope {
    println(requestWithRetry2())
}

fun main(): Unit = runBlocking {
    test6()
}