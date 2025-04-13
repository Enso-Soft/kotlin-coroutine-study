package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

fun main() = runBlocking<Unit> {
    val queue = LinkedList<String>()
    val mutex = Mutex()
    CoroutineScope(Dispatchers.Default).launch {
        while(true) {
            mutex.withLock {
                if (queue.isNotEmpty()) queue.poll() else null
            }?.let { output ->
                delay(3000L)
                println("println(\"Working... $output\")")
            } ?: run {
                yield()
            }
        }
    }

    while (true) {
        val input: String = readln()
        if (input == "n") {
            break
        } else {
            mutex.withLock {
                queue.add(input)
            }
        }
    }
}