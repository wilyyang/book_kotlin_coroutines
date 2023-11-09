package _2_sequence_builder

import java.math.BigInteger

val fibonacci: Sequence<BigInteger> = sequence {
    var first = 0.toBigInteger()
    var second = 1.toBigInteger()
    while(true){
        yield(first)
        val temp = first
        first += second
        second = temp
    }
}

fun main(args: Array<String>) {
    println(fibonacci.take(1000).toList())

}