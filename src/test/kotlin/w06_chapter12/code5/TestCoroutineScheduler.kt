package w06_chapter12.code5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestCoroutineScheduler {
    @Test
    fun `가상 시간 조절 테스트`() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(5000L, testCoroutineScheduler.currentTime)

        testCoroutineScheduler.advanceTimeBy(6000L)
        assertEquals(11000L, testCoroutineScheduler.currentTime)

        testCoroutineScheduler.advanceTimeBy(10000L)
        assertEquals(21000L, testCoroutineScheduler.currentTime)
    }

    @Test
    fun `가상 시간 위에서 테스트 진행`() {
        val testCoroutineScheduler: TestCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher: TestDispatcher = StandardTestDispatcher(scheduler = testCoroutineScheduler)

        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // Then
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(6000L)
        assertEquals(1, result)

        testCoroutineScheduler.advanceTimeBy(10000L)
        assertEquals(2, result)
    }

    @Test
    fun `advanceUntilIdle의 동작 살펴보기`() {
        val testCoroutineScheduler: TestCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher: TestDispatcher = StandardTestDispatcher(scheduler = testCoroutineScheduler)

        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testCoroutineScheduler.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }

    @Test
    fun `StandardTestDispatcher 사용하기`() {
        val testDispatcher: TestDispatcher = StandardTestDispatcher()
        val testCoroutineScope: CoroutineScope = CoroutineScope(context = testDispatcher)

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }

    @Test
    fun `TestScope 사용하기`() {
        val testCoroutineScope: TestScope = TestScope()

        // Given
        var result = 0

        // When
        testCoroutineScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }
        testCoroutineScope.advanceUntilIdle()

        // Then
        assertEquals(2, result)
    }

    @Test
    fun `runTest 사용하기`() {
        // Given
        var result = 0

        // When
        runTest {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
        }

        // Then
        assertEquals(2, result)
    }
}