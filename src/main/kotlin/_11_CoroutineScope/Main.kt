package _11_CoroutineScope

import kotlinx.coroutines.*

data class Details(val name : String, val followers: Int)
data class Tweet(val text: String)

fun getFollowersNumber() : Int = throw Error("Service exception")

suspend fun getUserName(): String {
    delay(500)
    return "양동국"
}

suspend fun getTweets(): List<Tweet> {
    return listOf(Tweet("Hello, world"))
}

suspend fun CoroutineScope.getUserDetails(): Details {
    val userName = async { getUserName() }
    val followersNumber = async { getFollowersNumber() }
    return Details(userName.await(), followersNumber.await())
}

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

suspend fun test2(){

    val a = coroutineScope {
        delay(1000)
        10
    }
    println("a is calculated")
    val b = coroutineScope {
        delay(1000)
        20
    }

    println(a)
    println(b)
}

fun main(): Unit = runBlocking {
    test2()
}