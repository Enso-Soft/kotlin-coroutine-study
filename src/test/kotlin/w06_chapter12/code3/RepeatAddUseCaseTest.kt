package w06_chapter12.code3

import kotlinx.coroutines.runBlocking
import org.example.w06_chapter12.code.code3.RepeatAddUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RepeatAddUseCaseTest {
    @Test
    fun `100번 더하면 100이 반환된다`() = runBlocking {
        // Given
        val repeatAddUseCase = RepeatAddUseCase()

        // When
        val result = repeatAddUseCase.add(100)

        // Then
        assertEquals(100, result)
    }
}