package org.example.w05_chapter11.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    launch(Dispatchers.Unconfined) {
        println("launch 코루틴 실행 스레드: ${Thread.currentThread().name}")
    }
}

/** 결과:
    launch 코루틴 실행 스레드: main
 */