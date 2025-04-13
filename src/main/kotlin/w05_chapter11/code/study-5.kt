package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

fun main() = runBlocking<Unit> {
    val channel = Channel<String>()
    launch(Dispatchers.Default) {
        for (output in channel) {
            delay(3000L)
            println("println(\"Working... $output\")")
        }
    }

    while (true) {
        val input: String = readln()
        if (input == "n") {
            break
        } else {
            channel.send(input)
        }
    }
}