package org.example.w04_chapter9.code

import kotlinx.coroutines.*
import org.example.getElapsedTime

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val results = searchByKeywordAsync("keyword")

    results.forEach { println(it) }
    println(getElapsedTime(startTime))

    supervisorScope {  }
}

suspend fun searchByKeywordAsync(keyword: String): Array<String> = coroutineScope {
    val dbResultsDeferred = async { searchFromDB(keyword) }
    val serverResultsDeferred = async { searchFromServer(keyword) }
    return@coroutineScope arrayOf(*dbResultsDeferred.await(), *serverResultsDeferred.await())
}

/** 결과:
    [DB]keyword1
    [DB]keyword2
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 1022ms
 */