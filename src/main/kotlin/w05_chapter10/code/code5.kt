package org.example.w05_chapter10.code

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val job = launch {
        while (this.isActive) {
            println("작업 중")
        }
    }

    delay(100L)
    job.cancel()
}

/** 결과:
    작업 중
    작업 중
    작업 중
    작업 중
    ...
 */