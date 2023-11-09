package _2_sequence_builder
val seq = sequence {
    println("Generating first")
    yield(1)
    println("Generating second")
    yield(2)
    println("Generating third")
    yield(3)
    println("Done")
}

fun main(args: Array<String>) {
    val iterator = seq.iterator()
    println("Starting")
    val first = iterator.next()
    println("First: $first")
    val second = iterator.next()
    println("Second: $second")
//    for(num in seq){
//        println("The next number is $num")
//    }

}