package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecutorTest {

    private val executor = Executor()

    @Test
    fun `when a simple program is executed the output is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.PRINT, "HELLO WORLD"),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("HELLO WORLD")
        )

    @Test
    fun `when a program with basic text output is executed the output is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.PRINT, "HELLO "),
                    Instruction("", Operator.PRINT, "WORLD"),
                    Instruction(operator = Operator.LINE),
                    Instruction("", Operator.PRINT, "GOOD BYE"),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("HELLO WORLD", "GOOD BYE")
        )

    @Test
    fun `when a program puts a literal value in the accumulator is executed the correct value is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.IN, "42"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE: "),
                    Instruction(operator = Operator.OUT),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("ACCUMULATOR VALUE: 42")
        )

    @Test
    fun `when a program performs arithmetic with literal values correct value is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.ADD, "2"),
                    Instruction("", Operator.MULTIPLY, "50"),
                    Instruction("", Operator.SUBTRACT, "16"),
                    Instruction("", Operator.DIVIDE, "2"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE: "),
                    Instruction(operator = Operator.OUT),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("ACCUMULATOR VALUE: 42")
        )


    private fun assertThatAValidProgramCanBeCorrectlyExecuted(program: Program, expectedOutput: List<String>) =
        assertEquals(expectedOutput, (executor.execute(program) as ExecutionSuccess).output)

}