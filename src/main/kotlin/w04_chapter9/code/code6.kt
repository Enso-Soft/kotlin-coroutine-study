package org.example.w04_chapter9.code

import kotlinx.coroutines.*
import org.example.getElapsedTime

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val results = searchByKeywordAsyncException("keyword")

    results.forEach { println(it) }
    println(getElapsedTime(startTime))

    supervisorScope {  }
}

suspend fun searchByKeywordAsyncException(keyword: String): Array<String> = supervisorScope {
    val dbResultsDeferred = async {
        throw Exception("dbResultDeferred에서 예외가 발생했습니다.")
        searchFromDB(keyword)
    }
    val serverResultsDeferred = async { searchFromServer(keyword) }

    val dbResult = try { dbResultsDeferred.await() } catch (_: Exception) {  arrayOf() }
    val serverResult = try { serverResultsDeferred.await() } catch (_: Exception) {  arrayOf() }

    return@supervisorScope arrayOf(*dbResult, *serverResult)
}

/** 결과:
    [Server]keyword1
    [Server]keyword2
    지난 시간 : 1022ms
 */