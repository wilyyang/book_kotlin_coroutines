package _3_suspend

import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MyException : Throwable("Just an exception")

suspend fun main() {
    try {
        suspendCoroutine<Unit> { cont ->
            cont.resumeWithException(MyException())
        }
    }catch (e: MyException){
        println("Caught!")
    }
}