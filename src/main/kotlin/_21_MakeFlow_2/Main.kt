package _21_MakeFlow_2

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*

data class User(val name: String)

interface UserApi {
    suspend fun takePage(pageNumber: Int): List<User>
}

class FakeUserApi : UserApi {
    private val users = List(20) { User("User$it")}
    private val pageSize: Int = 3
    override suspend fun takePage(pageNumber: Int): List<User> {
        delay(1000)
        return users.drop(pageSize * pageNumber).take(pageSize)
    }
}

fun allUsersFlow(api:UserApi): Flow<User> = flow {
    var page = 0
    do {
        println("Fetching page $page")
        val users = api.takePage(page++)
        emitAll(users.asFlow())
    }while(!users.isNullOrEmpty())
}

fun allUsersFlowChannel(api:UserApi): Flow<User> = channelFlow {
    var page = 0
    do {
        println("Fetching page $page")
        val users = api.takePage(page++)
        users?.forEach { send(it) }
    }while(!users.isNullOrEmpty())
}

suspend fun test1() = coroutineScope {
    val api = FakeUserApi()
    val users = allUsersFlow(api)
    val user = users.first {
        println("Checking $it")
        delay(1000)
        it.name == "User3"
    }
    println(user)
}

suspend fun test2() = coroutineScope {
    val api = FakeUserApi()
    val users = allUsersFlowChannel(api)
    val user = users.first {
        println("Checking $it")
        delay(1000)
        it.name == "User3"
    }
    println(user)
}

interface ProducerScope<in E> :
    CoroutineScope, SendChannel<E> {
    val channel: SendChannel<E>
}

fun <T> Flow<T>.merge(other:Flow<T>):Flow<T> =
    channelFlow{
        launch {
            collect{ send(it)}
        }
        other.collect { send(it) }
    }

fun <T> contextualFlow(): Flow<T> = channelFlow {
    launch (Dispatchers.IO){
//        send(computeIoValue())
    }

    launch (Dispatchers.Default){
//        send(computeCpuValue())
    }
}

interface CallbackBaseApi {
    fun register(callback : CallBack)
    fun unRegister(callback : CallBack)
}
interface CallBack {
    fun onNextValue(value:String)
    fun onAPiError(cause : Throwable)
    fun onCompleted() : Boolean
}

fun flowFrom(api: CallbackBaseApi) : Flow<String> = callbackFlow {
    val callback = object : CallBack {
        override fun onNextValue(value: String) {
            trySendBlocking(value)
        }

        override fun onAPiError(cause: Throwable) {
            cancel(CancellationException("API Error", cause))
        }

        override fun onCompleted() = channel.close()
    }
    api.register(callback)
    awaitClose{ api.unRegister(callback) }
}

suspend fun test3() = coroutineScope {
    flowFrom(api = object : CallbackBaseApi{
        override fun register(callback: CallBack) {
            TODO("Not yet implemented")
        }

        override fun unRegister(callback: CallBack) {
            TODO("Not yet implemented")
        }
    })
}

fun main(): Unit = runBlocking {
    test2()
}