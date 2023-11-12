package _6_CoroutineContext

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main()  {
    val name: CoroutineName = CoroutineName("A name")
    val element : CoroutineContext.Element = name
    val context : CoroutineContext = element

    val job: Job = Job()
    val jobElement : CoroutineContext.Element = job
    val jobContext : CoroutineContext = jobElement

    val ctx : CoroutineContext = context + job

    val coroutineName: CoroutineName? = ctx[CoroutineName]
    println(coroutineName?.name)

    val job2: Job? = ctx[Job]
    println(job2)
}