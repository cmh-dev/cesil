package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ParseFullProgramsTest {

    private val parser = Parser()

    @Disabled
    @Test
    fun `when valid source code is parsed without data the program structure will be returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
                %
                *
        """
        val program = (parser.parse(sourceCode) as ParsedProgram).program
        assertEquals(
            listOf(
                Instruction("", Operator.PRINT, "\"HELLO \"WORLD"),
                Instruction("", Operator.JUMP, "LABEL"),
                Instruction("LABEL", Operator.HALT, "")
            ), program.instructions
        )
        assertEquals(listOf<Int>(), program.data)
    }

}