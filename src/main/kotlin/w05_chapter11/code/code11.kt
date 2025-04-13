package org.example.w05_chapter11.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit>(Dispatchers.IO) {
    println("runBlocking 코루틴 실행 스레드: ${Thread.currentThread().name}")
    launch(Dispatchers.Unconfined) {
        println("launch 코루틴 실행 스레드: ${Thread.currentThread().name}")
    }

    println("runBlocking 코루틴 실행 스레드: ${Thread.currentThread().name}")
}

/** 결과:
    runBlocking 코루틴 실행 스레드: DefaultDispatcher-worker-1
    launch 코루틴 실행 스레드: DefaultDispatcher-worker-1
 */