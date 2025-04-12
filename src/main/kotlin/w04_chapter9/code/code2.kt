package org.example.w04_chapter9.code

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.getElapsedTime

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()

    launch { delayAndPrintHelloWorld() }
    launch { delayAndPrintHelloWorld() }

    println(getElapsedTime(startTime))
}

suspend fun delayAndPrintHelloWorld() {
    delay(1000L)
    println("Hello World")
}

/** 결과:
    지난 시간 : 2ms
    Hello World
    Hello World
 */