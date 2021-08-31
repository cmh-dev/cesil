package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParseFullProgramsTest {

    private val parser = Parser()

    @Test
    fun `when valid source code is parsed without data a program is returned`() {
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
                Instruction("", Operator.PRINT, "HELLO WORLD"),
                Instruction("", Operator.JUMP, "LABEL"),
                Instruction("LABEL", Operator.HALT, "")
            ), program.instructions
        )
        assertEquals(mapOf("LABEL" to 2), program.labeledInstructionIndexes)
        assertEquals(listOf<Int>(), program.data)
    }

    @Test
    fun `when valid source code is parsed with comments a program is returned`() {
        val sourceCode = """
                ( PRINT HELLO WORLD OUT
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                ( END THE PROGRAM
                LABEL     HALT
                %
                *
        """
        val program = (parser.parse(sourceCode) as ParsedProgram).program
        assertEquals(
            listOf(
                Instruction("", Operator.PRINT, "HELLO WORLD"),
                Instruction("", Operator.JUMP, "LABEL"),
                Instruction("LABEL", Operator.HALT, "")
            ), program.instructions
        )
        assertEquals(mapOf("LABEL" to 2), program.labeledInstructionIndexes)
        assertEquals(listOf<Int>(), program.data)
    }

    @Test
    fun `when valid source code is parsed with data a program is returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
                %
                1 2
                3
                *
        """
        val program = (parser.parse(sourceCode) as ParsedProgram).program
        assertEquals(
            listOf(
                Instruction("", Operator.PRINT, "HELLO WORLD"),
                Instruction("", Operator.JUMP, "LABEL"),
                Instruction("LABEL", Operator.HALT, "")
            ), program.instructions
        )
        assertEquals(mapOf("LABEL" to 2), program.labeledInstructionIndexes)
        assertEquals(listOf(1, 2, 3), program.data)
    }

    @Test
    fun `when source code with invalid instructions is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          FIRST INVALID LINE
                          JUMP     LABEL
                          SECOND INVALID LINE
                LABEL     HALT
                %
                1 2
                3
                *
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "INSTRUCTION LINE INVALID [FIRST INVALID LINE]",
                "INSTRUCTION LINE INVALID [SECOND INVALID LINE]"
            ), errorMessages
        )
    }

    @Test
    fun `when source code with invalid data is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
                %
                1 2a
                x
                3
                *
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "DATA LINE INVALID [1 2a]",
                "DATA LINE INVALID [x]"
            ), errorMessages
        )
    }

    @Test
    fun `when source code with invalid instructions and data is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          FIRST INVALID LINE
                          JUMP     LABEL
                          SECOND INVALID LINE
                LABEL     HALT
                %
                1 2a
                x
                3
                *
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "INSTRUCTION LINE INVALID [FIRST INVALID LINE]",
                "INSTRUCTION LINE INVALID [SECOND INVALID LINE]",
                "DATA LINE INVALID [1 2a]",
                "DATA LINE INVALID [x]"
            ), errorMessages
        )
    }

    @Test
    fun `when source code with a missing data terminator is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
                %
                1 2
                3
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "MISSING DATA SET TERMINATOR"
            ), errorMessages
        )
    }

    @Test
    fun `when source code with a missing instruction set terminator is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
                1 2
                3
                *
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "INSTRUCTION LINE INVALID [1 2]",
                "INSTRUCTION LINE INVALID [3]",
                "MISSING INSTRUCTION SET TERMINATOR"
            ), errorMessages
        )
    }

    @Test
    fun `when source code with a missing instruction and set terminators is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                          JUMP     LABEL
                LABEL     HALT
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "MISSING INSTRUCTION SET TERMINATOR",
                "MISSING DATA SET TERMINATOR"
            ), errorMessages
        )
    }

    @Test
    fun `when source code without a halt instruction is parsed errors are returned`() {
        val sourceCode = """
                          PRINT    "HELLO WORLD"
                %
                *
        """
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "MISSING HALT INSTRUCTION"
            ), errorMessages
        )
    }

    @Test
    fun `when empty source codeis parsed errors are returned`() {
        val sourceCode = ""
        val errorMessages = (parser.parse(sourceCode) as ParsingErrors).errorMessages
        assertEquals(
            listOf(
                "NO SOURCE CODE"
            ), errorMessages
        )
    }

}