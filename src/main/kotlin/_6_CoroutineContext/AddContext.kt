package _6_CoroutineContext

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun main()  {
    val ctx1: CoroutineContext = CoroutineName("Name1")
    println(ctx1[CoroutineName]?.name)
    println(ctx1[Job]?.isActive)

    val ctx2: CoroutineContext = Job()
    println(ctx2[CoroutineName]?.name)
    println(ctx2[Job]?.isActive)

    val ctx3 = ctx1 + ctx2
    println(ctx3[CoroutineName]?.name)
    println(ctx3[Job]?.isActive)

    //

    val ctx4: CoroutineContext = CoroutineName("Name4")
    println(ctx4[CoroutineName]?.name)

    val ctx5: CoroutineContext = CoroutineName("Name5")
    println(ctx5[CoroutineName]?.name)

    val ctx6 = ctx4 + ctx5
    println(ctx6[CoroutineName]?.name)

    //


    val empty : CoroutineContext = EmptyCoroutineContext
    println(empty[CoroutineName])
    println(empty[Job])

    val ctxName = empty + CoroutineName("Name1")+empty
    println(ctxName[CoroutineName])

    //

    val ctx7: CoroutineContext = CoroutineName("Name7") + Job()
    println(ctx7[CoroutineName]?.name)
    println(ctx7[Job]?.isActive)

    val ctx8: CoroutineContext = ctx7.minusKey(CoroutineName)
    println(ctx8[CoroutineName]?.name)
    println(ctx8[Job]?.isActive)

    val ctx9 = (ctx7 + CoroutineName("Name8")).minusKey(CoroutineName)
    println(ctx9[CoroutineName]?.name)
    println(ctx9[Job]?.isActive)

    //

    ctx7.fold("") { acc, element -> "$acc$element "}.also(::println)

    val empty2 = emptyList<CoroutineContext>()
    ctx7.fold(empty2) { acc, element -> acc + element }.joinToString().also(::println)

}