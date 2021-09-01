package uk.me.cmh.cesil.interpreter

data class ProgramState(
    val output: String = "",
    val error: String = "",
    val accumulator: Int = 0,
    val variables: Map<String, Int> = mapOf(),
    val data: List<Int> = listOf(),
    val instructionIndex: Int = 0,
    val isRunning: Boolean = true,
    val numberOfExecutedInstructions: Int = 0
) {

    fun nextWithNewAccumulatorValue(newAccumulatorValue: Int): ProgramState =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(accumulator = newAccumulatorValue)

    fun nextWithNewOutputValue(newOutputValue: String): ProgramState =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(output = newOutputValue)

    fun nextWithNewVariableList(newVariableList: Map<String, Int>) =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(variables = newVariableList)

    fun nextWithError(error: String): ProgramState =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(error = error, isRunning = false)

    fun next(): ProgramState = getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet()

    fun nextWithNewInstructionIndex(newInstructionIndex: Int) =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(instructionIndex = newInstructionIndex)

    fun nextNewAccumulatorValueAndData(newAccumulatorValue: Int, newData: List<Int>) =
        getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(accumulator = newAccumulatorValue, data = newData)

    fun terminate() = getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(isRunning = false)

    private fun getNextProgramStateAndUpdateInstructionIndexAndCountIfNotSet(
        accumulator: Int = this.accumulator,
        output: String = this.output,
        variables: Map<String, Int> = this.variables,
        data: List<Int> = this.data,
        error: String = this.error,
        isRunning: Boolean = this.isRunning,
        instructionIndex: Int = this.instructionIndex + 1
    ): ProgramState = this.copy(
        accumulator = accumulator,
        output = output,
        variables = variables,
        data = data,
        error = error,
        isRunning = isRunning,
        instructionIndex = instructionIndex,
        numberOfExecutedInstructions = this.numberOfExecutedInstructions + 1
    )

}