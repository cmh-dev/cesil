package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProgramTest {

    @Test
    fun `when a program is created with instructions and data the resulting program is correct`() {
        val program = Program(
            listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.JUMP, "NEXT"),
                Instruction("NEXT", Operator.PRINT, "OUTPUT 2"),
                Instruction("", Operator.JUMP, "END"),
                Instruction("END", Operator.HALT, ""),
            ),
            listOf(1, 2, 3, 4)
        )
        assertEquals(
            listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.JUMP, "NEXT"),
                Instruction("NEXT", Operator.PRINT, "OUTPUT 2"),
                Instruction("", Operator.JUMP, "END"),
                Instruction("END", Operator.HALT, ""),
            ), program.instructions)
        assertEquals(mapOf("NEXT" to 2, "END" to 4), program.labeledInstructionIndexes)
        assertEquals(listOf(1, 2, 3, 4), program.data)
    }


}