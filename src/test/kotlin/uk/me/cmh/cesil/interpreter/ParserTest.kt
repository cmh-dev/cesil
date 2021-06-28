package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserTest {

    private val parser = Parser()

    @Test
    fun `when a PRINT statement with no label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "    PRINT \"Hello World\"",
            Instruction("", Operator.PRINT, "\"Hello World\"")
        )

    private fun assertThatOneStatementCanBeCorrectlyParsed(sourceCode: String, expectedInstruction: Instruction) {
        val program = (parser.parse(sourceCode) as ParsedProgram).program
        assertEquals(1, program.instructions.size)
        assertEquals(expectedInstruction, program.instructions[0])
    }

}