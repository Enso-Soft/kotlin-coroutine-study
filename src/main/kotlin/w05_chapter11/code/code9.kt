package org.example.w05_chapter11.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val job = launch(start = CoroutineStart.UNDISPATCHED) {
        println("작업1")
    }

    job.cancel()
    println("작업2")
}

/** 결과:
    작업1
    작업2
 */
