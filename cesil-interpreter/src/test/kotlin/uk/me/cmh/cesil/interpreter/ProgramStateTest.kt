package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProgramStateTest {

    lateinit var programState:ProgramState

    @BeforeEach
    fun setUp() {
        programState = ProgramState()
    }

    @Test
    fun `when the next program state is created with a new accumulator value old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState(accumulator = 42),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedAccumulator = 42)

    @Test
    fun `when the next program state is created with a new output value old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState(output = "42"),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedOutput = "42")

    @Test
    fun `when the next program state is created with a new error value old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState(error = "Error"),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedError = "Error")

    @Test
    fun `when the next program state is created with a new halted value old value is overwritten and instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState(isHalted = true),
            expectedCurrentInstructionIndex = 1, expectedNumberOfExecutedInstruction = 1, expectedIsHalted = true)

    @Test
    fun `when the next program state is created with a new current instruction index the instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState(currentInstructionIndex = 42),
            expectedCurrentInstructionIndex = 42, expectedNumberOfExecutedInstruction = 1)

    @Test
    fun `when the second next program state is created the instruction values are correct`() =
        assertThatProgramStateIsAsExpected(programState.nextProgramState().nextProgramState(),
            expectedCurrentInstructionIndex = 2, expectedNumberOfExecutedInstruction = 2)

    private fun assertThatProgramStateIsAsExpected(programState: ProgramState,
                                                   expectedCurrentInstructionIndex: Int,
                                                   expectedNumberOfExecutedInstruction: Int,
                                                   expectedOutput: String = "",
                                                   expectedError: String = "",
                                                   expectedAccumulator: Int = 0,
                                                   expectedVariables: Map<String, Int> = mapOf(),
                                                   expectedData: List<Int> = listOf(),
                                                   expectedIsHalted: Boolean = false,

                            ) {
        val expectedProgramState = ProgramState(output = expectedOutput,
                                                error = expectedError,
                                                accumulator = expectedAccumulator,
                                                variables = expectedVariables,
                                                data = expectedData,
                                                isHalted = expectedIsHalted,
                                                numberOfExecutedInstructions = expectedNumberOfExecutedInstruction,
                                                currentInstructionIndex = expectedCurrentInstructionIndex)
        assertEquals(expectedProgramState, programState)
    }

}