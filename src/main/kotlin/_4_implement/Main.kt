package _4_implement

import kotlinx.coroutines.delay

class User(val userId : String, val userName : String)
suspend fun getUserId(token: String) = "userId : $token"

suspend fun getUserName(id:String, token: String) = "userName $id $token"

suspend fun myFunction(){
    println("Before")
    var counter = 0
    delay(1000)
    counter++
    println("Counter: $counter")
    println("After")
}