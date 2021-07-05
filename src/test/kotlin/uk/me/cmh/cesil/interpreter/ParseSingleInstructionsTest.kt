package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ParseSingleInstructionsTest {

    private val parser = Parser()

    // INPUT / OUTPUT

    @Test
    fun `when a IN statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "IN",
            Instruction("", Operator.IN, "")
        )

    @Test
    fun `when a IN statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   IN",
            Instruction("LABEL", Operator.IN, "")
        )

    @Test
    fun `when a OUT statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "OUT",
            Instruction("", Operator.OUT, "")
        )

    @Test
    fun `when a OUT statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   OUT",
            Instruction("LABEL", Operator.OUT, "")
        )

    @Test
    fun `when a PRINT statement with no label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "    PRINT \"HELLO WORLD\"",
            Instruction("", Operator.PRINT, "HELLO WORLD")
        )

    @Test
    fun `when a PRINT statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL  PRINT \"HELLO WORLD\"",
            Instruction("LABEL", Operator.PRINT, "HELLO WORLD")
        )

    @Test
    fun `when a LINE statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LINE",
            Instruction("", Operator.LINE, "")
        )

    @Test
    fun `when a LINE statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   LINE",
            Instruction("LABEL", Operator.LINE, "")
        )

    // STORAGE

    @Test
    fun `when a LOAD statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LOAD   VAR",
            Instruction("", Operator.LOAD, "VAR")
        )

    @Test
    fun `when a LOAD statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   LOAD   VAR",
            Instruction("LABEL", Operator.LOAD, "VAR")
        )

    @Test
    fun `when a STORE statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "STORE   VAR",
            Instruction("", Operator.STORE, "VAR")
        )

    @Test
    fun `when a STORE statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   STORE   VAR",
            Instruction("LABEL", Operator.STORE, "VAR")
        )

    // MATHEMATICAL

    @Test
    fun `when a ADD statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "ADD   VAR",
            Instruction("", Operator.ADD, "VAR")
        )

    @Test
    fun `when a ADD statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   ADD   VAR",
            Instruction("LABEL", Operator.ADD, "VAR")
        )

    @Test
    fun `when a SUBTRACT statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "SUBTRACT   VAR",
            Instruction("", Operator.SUBTRACT, "VAR")
        )

    @Test
    fun `when a SUBTRACT statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   SUBTRACT   VAR",
            Instruction("LABEL", Operator.SUBTRACT, "VAR")
        )

    @Test
    fun `when a MULTIPLY statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "MULTIPLY   VAR",
            Instruction("", Operator.MULTIPLY, "VAR")
        )

    @Test
    fun `when a MULTIPLY statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   MULTIPLY   VAR",
            Instruction("LABEL", Operator.MULTIPLY, "VAR")
        )

    @Test
    fun `when a DIVIDE statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "DIVIDE   VAR",
            Instruction("", Operator.DIVIDE, "VAR")
        )

    @Test
    fun `when a DIVIDE statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   DIVIDE   VAR",
            Instruction("LABEL", Operator.DIVIDE, "VAR")
        )

    // PROGRAM CONTROL

    @Test
    fun `when a HALT statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "HALT",
            Instruction("", Operator.HALT, "")
        )

    @Test
    fun `when a HALT statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   HALT",
            Instruction("LABEL", Operator.HALT, "")
        )

    @Test
    fun `when a JUMP statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "JUMP   LABEL2",
            Instruction("", Operator.JUMP, "LABEL2")
        )

    @Test
    fun `when a JUMP statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   JUMP   LABEL2",
            Instruction("LABEL", Operator.JUMP, "LABEL2")
        )

    @Test
    fun `when a JINEG statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "JINEG   LABEL2",
            Instruction("", Operator.JINEG, "LABEL2")
        )

    @Test
    fun `when a JINEG statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   JINEG   LABEL2",
            Instruction("LABEL", Operator.JINEG, "LABEL2")
        )

    @Test
    fun `when a JIZERO statement without a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "JIZERO   LABEL2",
            Instruction("", Operator.JIZERO, "LABEL2")
        )

    @Test
    fun `when a JIZERO statement with a label is parsed the correct instruction should be returned`() =
        assertThatOneStatementCanBeCorrectlyParsed(
            "LABEL   JIZERO   LABEL2",
            Instruction("LABEL", Operator.JIZERO, "LABEL2")
        )

    // ERROR SITUATIONS

    @Test
    fun `when a single numeric instruction line is parsed an exception should be thrown`() =
        assertThatOneInvalidStatementCannotBeParsed("3", "INSTRUCTION LINE INVALID [3]")

    @Test
    fun `when a print instruction line operand with no quotes is parsed an exception should be thrown`() =
        assertThatOneInvalidStatementCannotBeParsed("PRINT HELLO",
            "INSTRUCTION LINE OPERAND INVALID [PRINT HELLO]")

    @Test
    fun `when a print instruction line operand with no ending quote is parsed an exception should be thrown`() =
        assertThatOneInvalidStatementCannotBeParsed("PRINT \"HELLO",
            "INSTRUCTION LINE OPERAND INVALID [PRINT \"HELLO]")

    @Test
    fun `when a print instruction line operand with no starting quote is parsed an exception should be thrown`() =
        assertThatOneInvalidStatementCannotBeParsed("PRINT HELLO\"",
            "INSTRUCTION LINE OPERAND INVALID [PRINT HELLO\"]")

    private fun assertThatOneStatementCanBeCorrectlyParsed(instructionLine: String, expectedInstruction: Instruction) =
        assertEquals(expectedInstruction, parser.parseInstructionLine(instructionLine))

    private fun assertThatOneInvalidStatementCannotBeParsed(instructionLine: String, expectedErrorMessage: String) {
        val exception =
            assertThrows(Parser.ParserException::class.java) { parser.parseInstructionLine(instructionLine) }
        assertEquals(expectedErrorMessage, exception.message)
    }

}