package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProgramStateTest {

    private lateinit var programState: ProgramState

    @BeforeEach
    fun setUp() {
        programState = ProgramState()
    }

    @Test
    fun `when the next program state is created with a new accumulator value the old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextWithNewAccumulatorValue(newAccumulatorValue = 42),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedAccumulator = 42
        )

    @Test
    fun `when the next program state is created with new accumulator and data values the old values are overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextNewAccumulatorValueAndData(newAccumulatorValue = 42, newData = listOf(2, 260199, 1)),
            expectedCurrentInstructionIndex = 1,
            expectedNumberOfExecutedInstruction = 1,
            expectedAccumulator = 42,
            expectedData = listOf(2, 260199, 1)
        )


    @Test
    fun `when the next program state is created with a new output value the old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextWithNewOutputValue(newOutputValue = "42"),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedOutput = "42"
        )

    @Test
    fun `when the next program state is created with a new variable list the old list is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextWithNewVariableList(newVariableList = mapOf("The Answer" to 42)),
            expectedCurrentInstructionIndex = 1,
            expectedNumberOfExecutedInstruction = 1,
            expectedVariables = mapOf("The Answer" to 42)
        )

    @Test
    fun `when the next program state is created with an error it is recorded, the running state set off and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextWithError(error = "Error"),
            expectedCurrentInstructionIndex = 1,
            expectedNumberOfExecutedInstruction = 1,
            expectedIsRunning = false,
            expectedError = "Error"
        )

    @Test
    fun `when the next program state is created with a new current instruction index the instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.nextWithNewInstructionIndex(newInstructionIndex = 42),
            expectedCurrentInstructionIndex = 42, expectedNumberOfExecutedInstruction = 1
        )

    @Test
    fun `when a terminated program state is the running flag is set to false and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.terminate(),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedIsRunning = false
        )

    @Test
    fun `when the second next program state is created with no changes instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.next(),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1
        )

    @Test
    fun `when the second next program state is created the instruction values are correct`() =
        assertThatProgramStateIsAsExpected(
            programState.next().next(),
            expectedCurrentInstructionIndex = 2, expectedNumberOfExecutedInstruction = 2
        )

    private fun assertThatProgramStateIsAsExpected(
        programState: ProgramState,
        expectedCurrentInstructionIndex: Int,
        expectedNumberOfExecutedInstruction: Int,
        expectedOutput: String = "",
        expectedError: String = "",
        expectedAccumulator: Int = 0,
        expectedVariables: Map<String, Int> = mapOf(),
        expectedData: List<Int> = listOf(),
        expectedIsRunning: Boolean = true
    ) {
        val expectedProgramState = ProgramState(
            output = expectedOutput,
            error = expectedError,
            accumulator = expectedAccumulator,
            variables = expectedVariables,
            data = expectedData,
            isRunning = expectedIsRunning,
            numberOfExecutedInstructions = expectedNumberOfExecutedInstruction,
            instructionIndex = expectedCurrentInstructionIndex
        )
        assertEquals(expectedProgramState, programState)
    }

}