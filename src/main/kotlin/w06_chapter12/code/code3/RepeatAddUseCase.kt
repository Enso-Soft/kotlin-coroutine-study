package org.example.w06_chapter12.code.code3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepeatAddUseCase {
    suspend fun add(repeatTime: Int): Int = withContext(Dispatchers.Default) {
        var result = 0
        repeat(repeatTime) {
            result += 1
        }
        return@withContext result
    }
}