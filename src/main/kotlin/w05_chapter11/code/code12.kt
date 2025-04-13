package org.example.w05_chapter11.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit>(Dispatchers.IO) {
    launch(Dispatchers.Unconfined) {
        println("일시 중단 전 실행 스레드: ${Thread.currentThread().name}")
        delay(100L)
        println("일시 중단 후 실행 스레드: ${Thread.currentThread().name}")
    }
}

/** 결과:
    일시 중단 전 실행 스레드: DefaultDispatcher-worker-1
    일시 중단 후 실행 스레드: kotlinx.coroutines.DefaultExecutor
 */