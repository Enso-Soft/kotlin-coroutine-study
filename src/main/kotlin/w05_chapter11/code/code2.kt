package org.example.w05_chapter11.code

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Volatile
var chapter11Code2Count = 0

fun main() = runBlocking<Unit> {
    withContext(Dispatchers.Default) {
        repeat(10_000) {
            launch {
                chapter11Code2Count += 1
            }
        }
    }

    println("count = $chapter11Code2Count")
}

/** 결과:
    count = 9770
 */