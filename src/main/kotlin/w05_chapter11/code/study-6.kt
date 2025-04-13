package org.example.w05_chapter11.code

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

fun main() = runBlocking<Unit> {
    while (true) {
        val input: String = readln()
        when (input) {
            "n" -> break
            "a" -> apiRequest(input)
            "b" -> dbResult(input)
            "c" -> apiAndDBResult(input)
            else -> {}
        }
    }
}

private var latestJob: Job? = null

private suspend fun apiRequest(input: String) = coroutineScope {
    val job = launch(context = Dispatchers.Default, start = CoroutineStart.LAZY) {
        println("API 요청 중 $input")
        delay(2000L)
        println("API 요청 완료 $input")
    }

    if (latestJob?.isActive == true) {
        latestJob?.invokeOnCompletion {
            latestJob = job
            job.start()
        }
    } else {
        latestJob = job
        job.start()
    }
}

private suspend fun dbResult(input: String) = coroutineScope {
    val job = launch(context = Dispatchers.Default, start = CoroutineStart.LAZY) {
        println("DB 요청 중 $input")
        delay(2000L)
        println("DB 요청 완료 $input")
    }

    if (latestJob?.isActive == true) {
        latestJob?.invokeOnCompletion {
            latestJob = job
            job.start()
        }
    } else {
        latestJob = job
        job.start()
    }
}

private suspend fun  apiAndDBResult(input: String) = coroutineScope {
    val job = launch(context = Dispatchers.Default, start = CoroutineStart.LAZY) {
        println("API, DB 요청 중 $input")
        delay(2000L)
        println("API, DB 요청 완료 $input")
    }

    if (latestJob?.isActive == true) {
        latestJob?.invokeOnCompletion {
            latestJob = job
            job.start()
        }
    } else {
        latestJob = job
        job.start()
    }
}