package uk.me.cmh.cesil.interpreter.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.me.cmh.cesil.interpreter.Instruction
import uk.me.cmh.cesil.interpreter.Operator

class ParserStateTest {

    private val parserState = ParserState(
        instructions = listOf(Instruction("", Operator.PRINT, "OUTPUT 1")),
        data = listOf(1),
        isInstructionSetTerminated = false,
        isDataSetTerminated = false,
        isHaltStatementPresent = false,
        errors = listOf("ERROR 1")
    )

    @Test
    fun `when an instruction is added a new state is returned with the additional instruction`() {
        val expectedParserState = parserState.copy(
            instructions = listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.PRINT, "OUTPUT 2")
            ))
        assertEquals(expectedParserState, parserState.addInstruction(Instruction("", Operator.PRINT, "OUTPUT 2")))
    }

    @Test
    fun `when a HALT instruction is added a new state is returned with the additional instruction and the halt instruction flag on`() {
        val expectedParserState = parserState.copy(
            instructions = listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.HALT, ""),
            ),
            isHaltStatementPresent = true)
        assertEquals(expectedParserState, parserState.addInstruction(Instruction("", Operator.HALT, "")))
    }

    @Test
    fun `when data is added a new state is returned with the additional data`() {
        val expectedParserState = parserState.copy(data = listOf(1, 2, 3, 4))
        assertEquals(expectedParserState, parserState.addData(listOf(2, 3, 4)))
    }

    @Test
    fun `when the instruction set is terminated a new state is returned with the flag on`() {
        val expectedParserState = parserState.copy(isInstructionSetTerminated = true)
        assertEquals(expectedParserState, parserState.terminateInstructionSet())
    }

    @Test
    fun `when the data set is terminated a new state is returned with the flag on`() {
        val expectedParserState = parserState.copy(isDataSetTerminated = true)
        assertEquals(expectedParserState, parserState.terminateDataSet())
    }

    @Test
    fun `when error is added a new state is returned with the error`() {
        val expectedParserState = parserState.copy(errors = listOf("ERROR 1", "ERROR 2"))
        assertEquals(expectedParserState, parserState.addError("ERROR 2"))
    }


}