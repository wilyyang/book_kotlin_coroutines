package _16_Channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

suspend fun test1()  = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) {index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
    }

    launch {
        repeat(5){
            val received = channel.receive()
            println(received)
        }
    }
}

suspend fun test2()  = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) {index ->
            println("Producing next one")
            delay(1000)
            channel.send(index * 2)
        }
        channel.close()
    }

    launch {
//        for(element in channel){
//            println(element)
//        }

        channel.consumeEach {element ->
            println(element)
        }
    }
}

fun CoroutineScope.produceNumbers(
    max: Int
): ReceiveChannel<Int> = produce {
    var x = 0
    while (x < 5) send(x++)
}

suspend fun test3()  = coroutineScope {
    val channel = produce {
        repeat(5) {index ->
            println("Producing next one")
            delay(1000)
            send(index * 2)
        }
    }

    for(element in channel){
        println(element)
    }
}

suspend fun test4()  = coroutineScope {
    val channel = produce(capacity = Channel.CONFLATED) {
        repeat(5) {index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)
    for(element in channel){
        println(element)
        delay(1000)
    }
}

suspend fun test5()  = coroutineScope {
    val channel = Channel<Int>(
        capacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    launch {
        repeat(5) { index ->
            channel.send(index * 2)
            delay(100)
            println("Sent")
        }
        channel.close()
    }

    delay(1000)
    for(element in channel){
        println(element)
        delay(1000)
    }
}

fun CoroutineScope.produceNumbers() = produce{
    repeat(10){
        delay(100)
        send(it)
    }
}

fun CoroutineScope.launchProcessor(
    id: Int,
    channel: ReceiveChannel<Int>
) = launch {
    for(msg in channel) {
        println("#$id received $msg")
    }
}

suspend fun test6()  = coroutineScope {
    val channel = produceNumbers()
    repeat(3) { id ->
        delay(10)
        launchProcessor(id, channel)
    }
}

suspend fun sendString(
    channel: SendChannel<String>,
    text : String,
    time : Long
){
    while(true){
        delay(time)
        channel.send(text)
    }
}

suspend fun test7()  = coroutineScope {
    val channel = Channel<String>()
    launch { sendString(channel, "foo", 200L)  }
    launch { sendString(channel, "BAR!", 500L)  }
    repeat(50) {
        println(channel.receive())
    }
    coroutineContext.cancelChildren()
}

fun <T> CoroutineScope.fanIn(
    channels : List<ReceiveChannel<T>>
): ReceiveChannel<T> = produce {
    for(channel in channels){
        launch {
            for(elem in channel){
                send(elem)
            }
        }
    }
}

fun CoroutineScope.numbers(): ReceiveChannel<Int> =
    produce{
        repeat(3) { num ->
            send(num+1)
        }
    }

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) =
    produce{
        for(num in numbers){
            send(num*num)
        }
    }

suspend fun test8()  = coroutineScope {
    val numbers = numbers()
    val squared = square(numbers)
    for(num in squared){
        println(num)
    }
}

fun main(): Unit = runBlocking {
    test8()
}