package org.example.w05_chapter11.code

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Volatile
var chapter11Code4Count = 0
val chapter11Code4Mutex = Mutex()

fun main() = runBlocking<Unit> {
    withContext(Dispatchers.Default) {
        repeat(10_000) {
            launch {
                chapter11Code4Mutex.withLock {
                    chapter11Code4Count += 1
                }
            }
        }
    }

    println("count = $chapter11Code4Count")
}

/** 결과:
    count = 10000
 */