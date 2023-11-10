package _4_implement

import kotlinx.coroutines.delay


suspend fun myFunction(){
    println("Before")
    delay(1000)
    println("After")
}