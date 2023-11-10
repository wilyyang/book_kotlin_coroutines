package _3_suspend

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main(){
    val i: Int = suspendCoroutine<Int> { cont ->
        cont.resume(42)
    }
    println(i)

    val str: String = suspendCoroutine<String> { cont ->
        cont.resume("Some text")
    }
    println(str)

    val b: Boolean = suspendCoroutine<Boolean> { cont ->
        cont.resume(true)
    }
    println(b)
}