package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecutorTest {

    private val executor = Executor()

    @Test
    fun `when a simple program is executed the correct output should be returned`() {
        val program = Program(
            listOf(
                Instruction("", Operator.PRINT, "HELLO WORLD"),
                Instruction("", Operator.HALT, "")
            ),
            mapOf(),
            listOf()
        )
        val executionResult = (executor.execute(program) as ExecutionSuccess)
        assertEquals(listOf("HELLO WORLD"), executionResult.output)
    }

}