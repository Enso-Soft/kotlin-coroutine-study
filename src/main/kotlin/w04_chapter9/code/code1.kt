package w04_chapter9.code

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    delay(1000L)
    println("Hello World")
    delay(1000L)
    println("Hello World")

    println("normalDelayAndPrintHelloWorld Run")
    normalDelayAndPrintHelloWorld().join()
    normalDelayAndPrintHelloWorld().join()

    println("suspendDelayAndPrintHelloWorld Run")
    launch(CoroutineName("suspend Coroutine")) {
        suspendDelayAndPrintHelloWorld()
        suspendDelayAndPrintHelloWorld()
    }
}

fun normalDelayAndPrintHelloWorld() = CoroutineScope(Dispatchers.Default).launch {
    delay(1000L)
    println("Hello World")
}

suspend fun suspendDelayAndPrintHelloWorld() {
    delay(1000L)
    println("[${Thread.currentThread().name}] Hello World")
}