package uk.me.cmh.cesil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class InterpreterTest {

    private val mockParser: Parser = mock()
    private val mockExecutor: Executor = mock()
    private val interpreter = Interpreter(mockParser, mockExecutor)

    @Test
    fun `when a program is executed with one parsing error it should be returned`() {

        val sourceCode = """
            BAD PROGAM LINE 1
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(ParserErrors(listOf("LINE 1 IS INVALID")))

        val results = interpreter.executeProgram(sourceCode)

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(listOf("LINE 1 IS INVALID"), results)

    }

    @Test
    fun `when a program is executed with multiple parsing errors they should be returned`() {

        val sourceCode = """
            BAD PROGAM LINE 1
            BAD PROGAM LINE 2
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(ParserErrors(listOf("LINE 1 IS INVALID", "LINE 2 IS INVALID")))

        val results = interpreter.executeProgram(sourceCode)

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(listOf("LINE 1 IS INVALID", "LINE 2 IS INVALID"), results)

    }

    @Test
    fun `when a program is executed without parsing errors then the results should be returned`() {

        val sourceCode = """
            PRINT "HELLO"
            LINE
            PRINT "WORLD"
        """.trimIndent()
        val instructions = listOf(
                Instruction("", Operator.PRINT, "HELLO"),
                Instruction("", Operator.PRINT, "WORLD")
            )
        whenever(mockParser.parse(sourceCode)).thenReturn(ParsedInstructions(instructions))
        whenever(mockExecutor.execute(instructions)).thenReturn(listOf("HELLO", "WORLD"))

        val results = interpreter.executeProgram(sourceCode)

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(1)).execute(instructions)
        assertEquals(listOf("HELLO", "WORLD"), results)

    }


}