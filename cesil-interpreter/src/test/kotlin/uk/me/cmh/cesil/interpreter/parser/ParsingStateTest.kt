package uk.me.cmh.cesil.interpreter.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.me.cmh.cesil.interpreter.Instruction
import uk.me.cmh.cesil.interpreter.Operator

class ParsingStateTest {

    private val parsingState = ParsingState(
        instructions = listOf(Instruction("", Operator.PRINT, "OUTPUT 1")),
        data = listOf(1),
        isInstructionSetTerminated = false,
        isDataSetTerminated = false,
        isHaltStatementPresent = false,
        errors = listOf("ERROR 1")
    )

    @Test
    fun `when an instruction is added a new state is returned with the additional instruction`() {
        val expectedParserState = parsingState.copy(
            instructions = listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.PRINT, "OUTPUT 2")
            )
        )
        assertEquals(expectedParserState, parsingState.addInstruction(Instruction("", Operator.PRINT, "OUTPUT 2")))
    }

    @Test
    fun `when a HALT instruction is added a new state is returned with the additional instruction and the halt instruction flag on`() {
        val expectedParserState = parsingState.copy(
            instructions = listOf(
                Instruction("", Operator.PRINT, "OUTPUT 1"),
                Instruction("", Operator.HALT, ""),
            ),
            isHaltStatementPresent = true
        )
        assertEquals(expectedParserState, parsingState.addInstruction(Instruction("", Operator.HALT, "")))
    }

    @Test
    fun `when data is added a new state is returned with the additional data`() {
        val expectedParserState = parsingState.copy(data = listOf(1, 2, 3, 4))
        assertEquals(expectedParserState, parsingState.addData(listOf(2, 3, 4)))
    }

    @Test
    fun `when the instruction set is terminated a new state is returned with the flag on`() {
        val expectedParserState = parsingState.copy(isInstructionSetTerminated = true)
        assertEquals(expectedParserState, parsingState.terminateInstructionSet())
    }

    @Test
    fun `when the data set is terminated a new state is returned with the flag on`() {
        val expectedParserState = parsingState.copy(isDataSetTerminated = true)
        assertEquals(expectedParserState, parsingState.terminateDataSet())
    }

    @Test
    fun `when error is added a new state is returned with the error`() {
        val expectedParserState = parsingState.copy(errors = listOf("ERROR 1", "ERROR 2"))
        assertEquals(expectedParserState, parsingState.addError("ERROR 2"))
    }

}