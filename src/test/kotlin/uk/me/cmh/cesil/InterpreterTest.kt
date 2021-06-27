package uk.me.cmh.cesil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class InterpreterTest {

    private val mockParser : Parser = mock()
    private val mockExecutor : Executor = mock()
    private val interpreter = Interpreter(mockParser, mockExecutor)

    @Test
    fun `when a program which cannot be parsed is executed an error should be returned`() {

        whenever(mockParser.parse("BAD COMMAND")).thenReturn(ParserError("LINE 1 IS INVALID"))

        val results = interpreter.executeProgram("BAD COMMAND")

        verify(mockParser, times(1)).parse("BAD COMMAND")
        assertEquals(listOf("LINE 1 IS INVALID"),results)

    }

}