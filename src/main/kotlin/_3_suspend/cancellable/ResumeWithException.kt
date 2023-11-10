package _3_suspend.cancellable

import _3_suspend.User
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ApiException(val code : Int, override val message:String) : Throwable(message)

class Response<T>(open val isSuccessful: Boolean, val data:T, val code: Int, val message: String)


fun requestUser(block: (Response<User>) -> Unit) {
    val success = Response(isSuccessful = true, data = User(name = "양동국"), code = 200, message = "Success")
    val fail = Response(isSuccessful = false, data = User(name = ""), code = 404, message = "I don't know")
    Thread.sleep(1000L)
    block.invoke(fail)
}

suspend fun requestUser() : User {
    return suspendCancellableCoroutine <User> { cont ->
        requestUser { resp ->
            if(resp.isSuccessful){
                cont.resume(resp.data)
            }else{
                val e = ApiException(
                    resp.code,
                    resp.message
                )
                cont.resumeWithException(e)
            }
        }
    }
}

data class News(val title: String)

fun requestNews(onSuccess: (News) -> Unit, onError: (Throwable) -> Unit) {
    val success = Response(isSuccessful = true, data = News(title = "이것은 뉴스다"), code = 200, message = "Success")
    val fail = Response(isSuccessful = false, data = News(title = ""), code = 404, message = "I don't know this news")
    Thread.sleep(1000L)

    onSuccess.invoke(success.data)
//    onError.invoke(Throwable(message = fail.message))
}

suspend fun requestNews() : News {
    return suspendCancellableCoroutine<News> { cont ->
        requestNews (
            onSuccess = { news -> cont.resume(news) },
            onError = { e -> cont.resumeWithException(e) }
        )
    }
}

suspend fun main() {
    try {
//        val user = requestUser()
//        println(user)
        val news = requestNews()
        println(news)
    }catch (e: ApiException){
        println(e.message)
    }
}