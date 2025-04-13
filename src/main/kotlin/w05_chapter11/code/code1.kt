package org.example.w05_chapter11.code

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() = runBlocking<Unit> {
    var count = 0

    withContext(Dispatchers.Default) {
        repeat(10_000) {
            launch {
                count += 1
            }
        }
    }

    println("count = $count")
}

/** 결과:
    count = 9665
 */