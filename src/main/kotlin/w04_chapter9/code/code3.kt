package org.example.w04_chapter9.code

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    delayAndPrint(keyword = "I'm Parent Coroutine")
    launch {
        delayAndPrint(keyword = "I'm Child Coroutine")
    }
}

suspend fun delayAndPrint(keyword: String) {
    delay(1000L)
    println(keyword)
}

/** 결과:
    I'm Parent Coroutine
    I'm Child Coroutine
 */