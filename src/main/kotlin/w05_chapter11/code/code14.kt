package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.thread
import kotlin.coroutines.resume

fun main() = runBlocking<Unit>(Dispatchers.IO) {
    val result = suspendCancellableCoroutine<String> { continuation: CancellableContinuation<String> ->
        thread {
            Thread.sleep(1000L)
            continuation.resume("실행 결과")
        }
    }

    println(result)
}

/** 결과:
    실행 결과
 */