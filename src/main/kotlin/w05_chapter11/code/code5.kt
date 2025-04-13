package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Volatile
var chapter11Code5Count = 0
val chapter11Code5Dispatcher = newSingleThreadContext("CountCHangeThread")

fun main() = runBlocking<Unit> {
    withContext(Dispatchers.Default) {
        repeat(10_000) {
            launch {
                increaseCount()
            }
        }
    }

    println("count = $chapter11Code5Count")
}

suspend fun increaseCount() = coroutineScope {
    withContext(chapter11Code5Dispatcher) {
        chapter11Code5Count += 1
    }
}

/** 결과:
    count = 10000
 */