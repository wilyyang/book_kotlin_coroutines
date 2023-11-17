package _11_CoroutineScope

import _3_suspend.continuation
import kotlinx.coroutines.*

data class Details(val name : String, val followers: Int)
data class Tweet(val text: String)


suspend fun getUserName(): String {
    delay(500)
    return "양동국"
}

suspend fun getTweets(): List<Tweet> {
    return listOf(Tweet("Hello, world"))
}

//suspend fun CoroutineScope.getUserDetails(): Details {
//    val userName = async { getUserName() }
//    val followersNumber = async { getFollowersNumber() }
//    return Details(userName.await(), followersNumber.await())
//}

suspend fun CoroutineScope.test1(){
    val details = try {
        getUserDetails()
    }catch (e: Error){
        null
    }

    val tweets = async { getTweets() }
    println("User: $details")
    println("Tweets: ${tweets.await()}")
}

suspend fun CoroutineScope.test2(){
    val rootCoroutine = this
    println("1. ${this.coroutineContext}")

    val a = coroutineScope {
        println("2.1 ${this.coroutineContext}")
        delay(1000)
        10
    }
    println("a is calculated")
    val b = coroutineScope {
        println("2.2 ${this.coroutineContext}")
        delay(500)
        20

        this.launch {
            rootCoroutine.cancel()
            println("2.2.1 ${this.coroutineContext}")
        }
    }

    this.launch {
        println("2.3 ${this.coroutineContext}")
    }

//    println(a)
//    println(b)
}

suspend fun test3(){
    coroutineScope{
        val rootCoroutine = this
        println("1. $coroutineContext")

        launch {
            delay(1000)
            println("2.0 $coroutineContext")
//            throw Error("2.0")
        }.invokeOnCompletion {
            println("2.0 complete $it")
        }

        coroutineScope {
            delay(500)
            println("2.1 $coroutineContext")

            launch {
                println("2.1.1 $coroutineContext")
//                throw Error("2.1.1")
//                rootCoroutine.cancel()
            }
        }

        coroutineScope {
            delay(1200)
            println("2.2 $coroutineContext")
        }
    }
}


suspend fun test4() = coroutineScope{
    launch {
        delay(1000)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 1")
    }

    launch {
        delay(2000)
        val name = coroutineContext[CoroutineName]?.name
        println("[$name] Finished task 2")
    }
}

suspend fun test5() = coroutineScope{
    val job = launch(CoroutineName("parent")){
        test4()
    }
    delay(1500)
    job.cancel()
}

class ApiException(
    val code : Int,
    message : String
): Throwable(message)

fun getFollowersNumber():Int = throw ApiException(500, "Service unavailable")

suspend fun getUserDetails(): Details = coroutineScope {
    val userName = async { getUserName() }
    val followersNumber = async { getFollowersNumber() }
    Details(userName.await(), followersNumber.await())
}

suspend fun test6() = coroutineScope{
    val details = try {
        getUserDetails()
    }catch (e: ApiException){
        null
    }

    val tweets = async { getTweets() }
    println("User: $details")
    println("Tweets: ${tweets.await()}")
}

suspend fun produceCurrentUserSeq() : Details {
    val userName = getUserName()
    val followersNumber = getFollowersNumber()
    return Details(userName, followersNumber)
}

suspend fun produceCurrentUserSym() = coroutineScope {
    val userName = async { getUserName() }
    val followersNumber = async { getFollowersNumber() }
    Details(userName.await(), followersNumber.await())
}


suspend fun CoroutineScope.te1(){
    launch {
        println("te1 $coroutineContext")
    }
}

suspend fun te2(){
    coroutineScope {
        delay(2000)
        println("te2 $coroutineContext")
    }
}

suspend fun te3(){
    coroutineScope {
        println("te3 $coroutineContext")
    }
}

fun CoroutineScope.log(text : String){
    val name = this.coroutineContext[CoroutineName]?.name
    println("[$name] $text")
}
suspend fun test7() = withContext(CoroutineName("Parent")){
    log("Before")
    withContext(CoroutineName("Child 1")){
        delay(1000)
        log("Hello 1")
    }
    withContext(CoroutineName("Child 2")){
        delay(500)
        log("Hello 2")
    }

    coroutineScope {
        delay(500)
        log("Hello 2")
    }

    launch (Dispatchers.Main){

    }
}

fun main(): Unit = runBlocking(CoroutineName("parent")) {
    test7()
}