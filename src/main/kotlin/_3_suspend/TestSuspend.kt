package _3_suspend

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

var continuation : Continuation<Unit>? = null

suspend fun suspendAndSetContinuation() {
    suspendCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun main() = coroutineScope {
    println("Before")

    launch {
        delay(1000)
        continuation?.resume(Unit)
    }
    suspendAndSetContinuation()

//    continuation?.resume(Unit)

    println("After")
}