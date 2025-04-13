package org.example.w05_chapter10.code

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() = runBlocking<Unit> {
    launch {
        while(true) {
            println("자식 코루틴에서 작업 실행 중")
            yield()
        }
    }

    while(true) {
        println("부모코루틴에서 작업 실행 중")
        yield()
    }
}

/** 결과:
    부모코루틴에서 작업 실행 중
    자식 코루틴에서 작업 실행 중
    부모코루틴에서 작업 실행 중
    자식 코루틴에서 작업 실행 중
    ...
 */