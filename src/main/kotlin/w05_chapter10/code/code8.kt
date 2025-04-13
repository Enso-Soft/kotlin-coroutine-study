package org.example.w05_chapter10.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val dispatcher = newFixedThreadPoolContext(2, "MyThread")
    launch(dispatcher) {
        repeat(5) {
            println("[${Thread.currentThread().name}] 코루틴 실행이 일시 중단 됩니다")
            Thread.sleep(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행이 재개 됩니다")
        }
    }
}

/** 결과:
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
    [MyThread-1] 코루틴 실행이 일시 중단 됩니다
    [MyThread-1] 코루틴 실행이 재개 됩니다
 */