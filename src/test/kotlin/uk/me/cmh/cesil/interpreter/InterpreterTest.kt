package uk.me.cmh.cesil.interpreter

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
            BAD PRORGAM LINE 1
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(ParserErrors(listOf("LINE 1 IS INVALID")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionFailure

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(listOf("LINE 1 IS INVALID"), executionResult.output)

    }

    @Test
    fun `when a program is executed with multiple parsing errors they should be returned`() {

        val sourceCode = """
            BAD PROGRAM LINE 1
            BAD PROGRAM LINE 2
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(ParserErrors(listOf("LINE 1 IS INVALID", "LINE 2 IS INVALID")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionFailure

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(listOf("LINE 1 IS INVALID", "LINE 2 IS INVALID"), executionResult.output)

    }

    @Test
    fun `when a program is executed without parsing errors then the results should be returned`() {

        val sourceCode = """
               PRINT "HELLO"
               LINE
               PRINT "WORLD"
            %
            1
            2
            *
        """.trimIndent()
        val program = Program(listOf(
            Instruction("", Operator.PRINT, "HELLO"),
            Instruction("", Operator.PRINT, "WORLD")
        ), listOf(1, 2))
        whenever(mockParser.parse(sourceCode)).thenReturn(ParsedProgram(program))
        whenever(mockExecutor.execute(program)).thenReturn(ExecutionSuccess(listOf("HELLO", "WORLD")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionSuccess

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(1)).execute(program)
        assertEquals(listOf("HELLO", "WORLD"), executionResult.output)

    }


}