package org.example.w05_chapter11.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    while (true) {
        val input: String = readln()
        if (input == "n") {
            break
        } else {
            doSomething(input)
        }
    }
}

private fun CoroutineScope.doSomething(input: String) {
    launch(Dispatchers.Unconfined) {
        Thread.sleep(3000L)
        println("Working... $input")
    }
}