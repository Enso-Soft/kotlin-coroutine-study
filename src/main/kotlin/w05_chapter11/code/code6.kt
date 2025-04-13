package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun main() = runBlocking<Unit> {
    launch {
        println("작업1")
    }

    println("작업2")
}

/** 결과:
    작업2
    작업1
 */