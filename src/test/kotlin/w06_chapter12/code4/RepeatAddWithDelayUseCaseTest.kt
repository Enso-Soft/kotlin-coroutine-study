package w06_chapter12.code4

import kotlinx.coroutines.runBlocking
import org.example.w06_chapter12.code.code3.RepeatAddWithDelayUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RepeatAddWithDelayUseCaseTest {
    @Test
    fun `100번 더하면 100이 반환된다-runBlocking`() = runBlocking {
        // Given
        val repeatAddUseCase = RepeatAddWithDelayUseCase()

        // When
        val result = repeatAddUseCase.add(100)

        // Then
        assertEquals(100, result)
    }
}