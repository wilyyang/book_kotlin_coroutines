package _14_Share

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

var counter = 0
val dispatcher = Dispatchers.IO.limitedParallelism(1)

suspend fun massiveRun(action: suspend () -> Unit) =
    withContext(Dispatchers.Default) {
        repeat(1000) {
            launch {
                repeat(1000) { action() }
            }
        }
    }

suspend fun test1()  = coroutineScope {
   massiveRun {
       counter++
   }
    println(counter)
}

val mutex = Mutex()

suspend fun delayAndPrint(){
    mutex.lock()
    delay(1000)
    println("Done")
    mutex.unlock()
}

suspend fun test2()  = coroutineScope {
    repeat(5) {
        launch {
            delayAndPrint()
        }
    }

}

suspend fun test3()  = coroutineScope {
    massiveRun {
        mutex.withLock {
            counter++
        }
    }
    println(counter)

}

suspend fun test4()  = coroutineScope {
    val mutex = Mutex()
    println("Started")
    mutex.withLock {
        mutex.withLock {
            println("Will never be printed")
        }
    }

}

suspend fun test5()  = coroutineScope {
    val semaphore = Semaphore(2)

    repeat(5){
        launch {
            semaphore.withPermit {
                delay(1000)
                print(it)
            }
        }
    }

}

suspend fun gogo() {
    delay(1000)
    println("GOgo")
    1/0
}

suspend fun test6()  = coroutineScope {
    val mutex = Mutex()
    supervisorScope {

        repeat(5){
            launch {
//                mutex.lock()
//                gogo()
//                mutex.unlock()
                mutex.withLock {
                    gogo()
                }
            }
        }
    }

}

fun main(): Unit = runBlocking {
    test5()
}