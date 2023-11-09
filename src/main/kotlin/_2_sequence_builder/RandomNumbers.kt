package _2_sequence_builder

import kotlin.random.Random

fun randomNumbers(
    seed: Long = System.currentTimeMillis()
): Sequence<Int> = sequence {
    val random = Random(seed)
    while(true){
        yield(random.nextInt())
    }
}

fun randomUniqueStrings(
    length:Int,
    seed: Long = System.currentTimeMillis()
): Sequence<String> = sequence {
    val random = Random(seed)
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    while(true){
        val randomString = (1..length)
            .map { i -> random.nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
        yield(randomString)
    }
}.distinct()

fun main(args: Array<String>) {
    val itr = randomNumbers().iterator()
    println(itr.next())

    val itr2 = randomUniqueStrings(length = 4).iterator()
    println(itr2.next())

}