package _4_implement

suspend fun printUser(token: String){
    println("Before")
    val userId = getUserId(token)
    println("Got userId: $userId")
    val userName = getUserName(userId, token)
    println(User(userId, userName))
    println("After")
}