package _17_Select

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select

suspend fun requestData1(): String {
    delay(10_000)
    return "Data1"
}

suspend fun requestData2() : String {
    delay(1000)
    return "Data2"
}

val scope = CoroutineScope(SupervisorJob())

suspend fun askMultipleForData():String {
    val defData1 = scope.async { requestData1() }
    val defData2 = scope.async { requestData2() }

    return select {
        defData1.onAwait { it}
        defData2.onAwait { it}
    }
}

suspend fun askMultipleForData2():String = coroutineScope {
    select<String> {
        async { requestData1() }.onAwait{ it }
        async { requestData2() }.onAwait{ it }
    }.also { coroutineContext.cancelChildren() }
}

suspend fun test1()  = coroutineScope {
    println(askMultipleForData2())
}

suspend fun CoroutineScope.produceString(
    s: String,
    time:Long
) = produce {
    while(true){
        delay(time)
        send(s)
    }
}

suspend fun test2()  = coroutineScope {
    val fooChannel = produceString("foo", 210L)
    val barChannel = produceString("BAR", 500L)

    repeat(7){
        select {
            fooChannel.onReceive{
                println("From fooChannel: $it")
            }
            barChannel.onReceive {
                println("From barChannel: $it")
            }
        }
    }
}

suspend fun test3()  = coroutineScope {
    val c1 = Channel<Char>(capacity = 2)
    val c2 = Channel<Char>(capacity = 2)

    launch {
        for(c in 'A'..'H'){
            delay(400)
            select<Unit> {
                c1.onSend(c){
                    println("Sent $c to 1")
                }
                c2.onSend(c) {
                    println("Sent $c to 2")
                }
            }
        }
    }

    launch {
        while(true){
            delay(1000)
            val c = select<String> {
                c1.onReceive { "$it form 1" }
                c2.onReceive { "$it form 2" }
            }
            println("Received $c")
        }
    }
}

fun main(): Unit = runBlocking {
    test3()
}