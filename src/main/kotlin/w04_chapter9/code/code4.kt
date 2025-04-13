package org.example.w04_chapter9.code

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.getElapsedTime

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val results = searchByKeyword("keyword")

    results.forEach { println(it) }
    println(getElapsedTime(startTime))
}

suspend fun searchByKeyword(keyword: String): Array<String> {
    coroutineScope {  }
    val dbResults = searchFromDB(keyword)
    val serverResults = searchFromServer(keyword)
    return arrayOf(*dbResults, *serverResults)
}

suspend fun searchFromDB(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[DB]${keyword}1", "[DB]${keyword}2")
}

suspend fun searchFromServer(keyword: String): Array<String> {
    delay(1000L)
    return arrayOf("[Server]${keyword}1", "[Server]${keyword}2")
}

/** 결과:
    [DB]keyword1
    [DB]keyword2
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 2024ms
 */