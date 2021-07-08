package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecutorTest {

    private val executor = Executor()

    // SUCCESSFUL EXECUTION

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

    @Test
    fun `when a program performs arithmetic with variables correct value is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.ADD, "2"),
                    Instruction("", Operator.STORE, "VAR"),
                    Instruction("", Operator.ADD, "2"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE AFTER SECOND ADD: "),
                    Instruction(operator = Operator.OUT),
                    Instruction(operator = Operator.LINE),
                    Instruction("", Operator.LOAD, "VAR"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE AFTER LOAD: "),
                    Instruction(operator = Operator.OUT),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("ACCUMULATOR VALUE AFTER SECOND ADD: 4", "ACCUMULATOR VALUE AFTER LOAD: 2")
        )

    @Test
    fun `when a program stores a value in a variable and loads it back the correct value is returned`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.ADD, "2"),
                    Instruction("", Operator.STORE, "TWO"),
                    Instruction("", Operator.ADD, "14"),
                    Instruction("", Operator.STORE, "SIXTEEN"),
                    Instruction("", Operator.ADD, "34"),
                    Instruction("", Operator.STORE, "FIFTY"),
                    Instruction("", Operator.SUBTRACT, "50"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE AFTER SET UP: "),
                    Instruction(operator = Operator.OUT),
                    Instruction(operator = Operator.LINE),
                    Instruction("", Operator.ADD, "TWO"),
                    Instruction("", Operator.MULTIPLY, "FIFTY"),
                    Instruction("", Operator.SUBTRACT, "SIXTEEN"),
                    Instruction("", Operator.DIVIDE, "TWO"),
                    Instruction("", Operator.PRINT, "ACCUMULATOR VALUE AFTER ARITHMETIC: "),
                    Instruction(operator = Operator.OUT),
                    Instruction("", Operator.HALT, "")
                ),
                mapOf(),
                listOf()
            ), listOf("ACCUMULATOR VALUE AFTER SET UP: 0", "ACCUMULATOR VALUE AFTER ARITHMETIC: 42")
        )

    // TODO: mathematical operations using a variable
    // TODO: jump
    // TODO: jump if zero
    // TODO: jump if negative
    // TODO: exit program on a halt middle of program
    // TODO: loading data
    // TODO: out does not print a cr

    private fun assertThatAValidProgramCanBeCorrectlyExecuted(program: Program, expectedOutput: List<String>) =
        assertEquals(expectedOutput, (executor.execute(program) as ExecutionSuccess).output)

    // ERROR SCENARIOS

    // TODO: divide by zero
    // TODO: jumping to a label which does not exist
    // TODO: infinite loop
    // TODO: performing an action using a variable which does not exist
    // TODO: running out of data

}