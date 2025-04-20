package w06_chapter12.code1

import org.example.w06_chapter12.code.code1.AddUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AddUseCaseTestBeforeEach {
    lateinit var addUseCase: AddUseCase

    @BeforeEach
    fun setup() {
        addUseCase = AddUseCase()
    }

    @Test
    fun `1 더하기 2는 3이다`() {
        val result = addUseCase.add(1, 2)
        println(result)
        assertEquals(3, result)
    }

    @Test
    fun `1 더하기 2는 4이다`() {
        val result = addUseCase.add(1, 2)
        println(result)
        assertEquals(4, result)
    }

    @Test
    fun `-1 더하기 2는 1이다`() {
        val result = addUseCase.add(-1, 2)
        println(result)
        assertEquals(1, result)
    }
}