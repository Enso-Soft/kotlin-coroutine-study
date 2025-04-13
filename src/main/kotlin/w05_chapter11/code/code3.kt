package org.example.w05_chapter11.code

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext

@Volatile
var chapter11Code3Count = 0
val chapter11Code3Mutex = Mutex()

fun main() = runBlocking<Unit> {
    withContext(Dispatchers.Default) {
        repeat(10_000) {
            launch {
                chapter11Code3Mutex.lock()
                chapter11Code3Count += 1
                chapter11Code3Mutex.unlock()
            }
        }
    }

    println("count = $chapter11Code3Count")
}

/** 결과:
    count = 10000
 */