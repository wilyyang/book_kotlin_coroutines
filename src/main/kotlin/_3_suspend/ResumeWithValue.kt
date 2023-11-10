package _3_suspend

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class User(val name: String)

fun requestUser(block: (User) -> Unit) {
    val user = User(name = "양동국")
    Thread.sleep(1000L)
    block.invoke(user)
}

suspend fun requestUser() : User {
    return suspendCoroutine<User> { cont ->
        requestUser { user ->
            cont.resume(user)
        }
    }
}

suspend fun main() {
    println("Before")
    val user = requestUser()
    println(user)
    println("After")
}