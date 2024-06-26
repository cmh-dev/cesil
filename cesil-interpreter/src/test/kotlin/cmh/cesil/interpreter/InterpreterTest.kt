package cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import cmh.cesil.interpreter.executor.ExecutionFailure
import cmh.cesil.interpreter.executor.ExecutionSuccess
import cmh.cesil.interpreter.executor.Executor
import cmh.cesil.interpreter.parser.ParsedProgram
import cmh.cesil.interpreter.parser.Parser
import cmh.cesil.interpreter.parser.ParsingErrors

class InterpreterTest {

    private val mockParser: Parser = mock()
    private val mockExecutor: Executor = mock()
    private val interpreter = Interpreter(mockParser, mockExecutor)

    @Test
    fun `when a program is interpreted with one parsing error it is returned`() {

        val sourceCode = """
            BAD PRORGAM LINE 1
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(ParsingErrors(listOf("INSTRUCTION LINE INVALID [BAD PROGRAM LINE 1]")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionFailure

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(listOf("*** INSTRUCTION LINE INVALID [BAD PROGRAM LINE 1] ***"), executionResult.output)

    }

    @Test
    fun `when a program is interpreted with multiple parsing errors they are returned`() {

        val sourceCode = """
            BAD PROGRAM LINE 1
            BAD PROGRAM LINE 2
        """.trimIndent()
        whenever(mockParser.parse(sourceCode)).thenReturn(
            ParsingErrors(
                listOf(
                    "INSTRUCTION LINE INVALID [BAD PROGRAM LINE 1]",
                    "INSTRUCTION LINE INVALID [BAD PROGRAM LINE 2]"
                )
            )
        )

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionFailure

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(0)).execute(any())
        assertEquals(
            listOf(
                "*** INSTRUCTION LINE INVALID [BAD PROGRAM LINE 1] ***",
                "*** INSTRUCTION LINE INVALID [BAD PROGRAM LINE 2] ***"
            ), executionResult.output
        )

    }

    @Test
    fun `when a program is interpreted without parsing errors results are returned`() {

        val sourceCode = """
               PRINT "HELLO"
               LINE
               PRINT "WORLD"
               HALT
            %
            1
            2
            *
        """.trimIndent()
        val program = Program(
            listOf(
                Instruction("", Operator.PRINT, "HELLO"),
                Instruction("", Operator.PRINT, "WORLD"),
                Instruction("", Operator.HALT, "")
            ), listOf(1, 2)
        )
        whenever(mockParser.parse(sourceCode)).thenReturn(ParsedProgram(program))
        whenever(mockExecutor.execute(program)).thenReturn(ExecutionSuccess(listOf("HELLO", "WORLD")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionSuccess

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(1)).execute(program)
        assertEquals(listOf("HELLO", "WORLD"), executionResult.output)

    }

    @Test
    fun `when a program is interpreted with execution errors then they are returned`() {

        val sourceCode = """
               IN
               DIVIDE   0
               OUT
               HALT
            %
            0
            *
        """.trimIndent()
        val program = Program(
            listOf(
                Instruction("", Operator.IN, "HELLO"),
                Instruction("", Operator.DIVIDE, "0"),
                Instruction("", Operator.OUT, ""),
                Instruction("", Operator.HALT, "")
            ), listOf(0)
        )
        whenever(mockParser.parse(sourceCode)).thenReturn(ParsedProgram(program))
        whenever(mockExecutor.execute(program)).thenReturn(ExecutionFailure(listOf("ERROR - DIVIDE BY ZERO")))

        val executionResult = interpreter.executeProgram(sourceCode) as ExecutionFailure

        verify(mockParser, times(1)).parse(sourceCode)
        verify(mockExecutor, times(1)).execute(program)
        assertEquals(listOf("*** ERROR - DIVIDE BY ZERO ***"), executionResult.output)

    }

}