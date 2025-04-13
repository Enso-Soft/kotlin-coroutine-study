package org.example.w05_chapter10.code

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.getElapsedTime

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    repeat(10) { repeatTime ->
        launch {
            Thread.sleep(1000L)
            println("[${getElapsedTime(startTime)}] 코루틴${repeatTime} 실행 완료")
        }
    }
}

/** 결과:
    [지난 시간 : 1009ms] 코루틴0 실행 완료
    [지난 시간 : 2030ms] 코루틴1 실행 완료
    [지난 시간 : 3035ms] 코루틴2 실행 완료
    [지난 시간 : 4040ms] 코루틴3 실행 완료
    [지난 시간 : 5046ms] 코루틴4 실행 완료
    [지난 시간 : 6048ms] 코루틴5 실행 완료
    [지난 시간 : 7053ms] 코루틴6 실행 완료
    [지난 시간 : 8058ms] 코루틴7 실행 완료
    [지난 시간 : 9064ms] 코루틴8 실행 완료
    [지난 시간 : 10069ms] 코루틴9 실행 완료
 */