package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ExecutorTest {

    private val executor = Executor()

    // SUCCESSFUL EXECUTION

    @Test
    fun `when a simple program is executed the correct output is given`() =
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
    fun `when a program with basic text printing is executed the output is given`() =
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
    fun `when a program performs arithmetic with literal values the correct value is calculated`() =
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
    fun `when a program stores and retrieves a value from a variable the correct value is retrieved`() =
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
    fun `when a program performs arithmetic with values from variables the correct value is calculated`() =
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

    @Test
    fun `when a jump intsruction is executed lines between the jump and labled statements will be skipped`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.PRINT, "HELLO"),
                    Instruction("", Operator.JUMP, "LABEL"),
                    Instruction("", Operator.PRINT, " SKIPPED"),
                    Instruction("LABEL", Operator.PRINT, " WORLD"),
                    Instruction(operator = Operator.HALT)
                ),
                mapOf("LABEL" to 3),
                listOf()
            ), listOf("HELLO WORLD")
        )

    @Test
    fun `when a program with a jizero instruction is executed it will jump if the accumulator is zero`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.ADD, "2"),
                    Instruction("LOOP", Operator.OUT,""),
                    Instruction("", Operator.PRINT, " "),
                    Instruction("", Operator.SUBTRACT, "1"),
                    Instruction("", Operator.JIZERO, "EXIT"),
                    Instruction("", Operator.JUMP, "LOOP"),
                    Instruction("EXIT", Operator.HALT,"" )
                ),
                mapOf("LOOP" to 1, "EXIT" to 6),
                listOf()
            ), listOf("2 1 ")
        )

    @Test
    fun `when a program with a jineg instruction is excuted it will jump if the accumulator is negative`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.ADD, "2"),
                    Instruction("LOOP", Operator.OUT,""),
                    Instruction("", Operator.PRINT, " "),
                    Instruction("", Operator.SUBTRACT, "1"),
                    Instruction("", Operator.JINEG, "EXIT"),
                    Instruction("", Operator.JUMP, "LOOP"),
                    Instruction("EXIT", Operator.HALT,"" )
                ),
                mapOf("LOOP" to 1, "EXIT" to 6),
                listOf()
            ), listOf("2 1 0 ")
        )

    @Test
    fun `when a program has a halt instruction in the middle following instructions should not be executed`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("", Operator.PRINT, "HELLO"),
                    Instruction(operator = Operator.HALT),
                    Instruction("", Operator.PRINT, " WORLD")
                ),
                mapOf(),
                listOf()
            ), listOf("HELLO")
        )

    @Test
    fun `when a program with data is executed the data is read and acted upon`() =
        assertThatAValidProgramCanBeCorrectlyExecuted(
            Program(
                listOf(
                    Instruction("LOOP", Operator.IN,""),
                    Instruction("", Operator.JINEG, "EXIT"),
                    Instruction(operator = Operator.OUT),
                    Instruction("", Operator.PRINT, " "),
                    Instruction("", Operator.JUMP, "LOOP"),
                    Instruction("EXIT", Operator.HALT,"" )
                ),
                mapOf("LOOP" to 0, "EXIT" to 5),
                listOf(3, 2, 1, 0, -1)
            ), listOf("3 2 1 0 ")
        )

    private fun assertThatAValidProgramCanBeCorrectlyExecuted(program: Program, expectedOutput: List<String>) =
        assertEquals(expectedOutput, (executor.execute(program) as ExecutionSuccess).output)

    // ERROR SCENARIOS

    // TODO: divide by zero
    // TODO: jumping to a label which does not exist
    // TODO: infinite loop
    // TODO: performing an action using a variable which does not exist
    // TODO: running out of data

}