package org.example.w05_chapter10.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val job = launch {
        while (this.isActive) {
            println("작업 중")
            yield()
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
    프로세스 종료
 */