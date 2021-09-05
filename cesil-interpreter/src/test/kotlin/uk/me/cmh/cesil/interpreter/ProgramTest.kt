package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProgramTest {

    private val program = Program(
        listOf(Instruction("", Operator.JUMP, "EXIT"),
            Instruction("EXIT", Operator.PRINT, "THE ANSWER IS ")),
        mapOf("EXIT" to 0),
        listOf(42)
    )

    @Test
    fun `an instruction without a label can be correctly added to a program`() {
        val expectedProgram = Program(
            listOf(Instruction("", Operator.JUMP, "EXIT"),
                Instruction("EXIT", Operator.PRINT, "THE ANSWER IS "),
                Instruction("", Operator.IN, "")
            ),
            mapOf("EXIT" to 0),
            listOf(42)
        )
        assertEquals(expectedProgram, program.addInstruction(Instruction("", Operator.IN, "")))
    }

    @Test
    fun `an instruction with a label can be correctly added to a program`() {
        val expectedProgram = Program(
            listOf(Instruction("", Operator.JUMP, "EXIT"),
                Instruction("EXIT", Operator.PRINT, "THE ANSWER IS "),
                Instruction("LOAD", Operator.IN, "")
            ),
            mapOf("EXIT" to 0, "LOAD" to 2),
            listOf(42)
        )
        assertEquals(expectedProgram, program.addInstruction(Instruction("LOAD", Operator.IN, "")))
    }

    @Test
    fun `data items can be correctly added to a program`() {
        val expectedProgram = Program(
            listOf(Instruction("", Operator.JUMP, "EXIT"),
                Instruction("EXIT", Operator.PRINT, "THE ANSWER IS "),
            ),
            mapOf("EXIT" to 0),
            listOf(42, 2, 260000, 199)
        )
        assertEquals(expectedProgram, program.addData(listOf(2, 260000, 199)))
    }


}