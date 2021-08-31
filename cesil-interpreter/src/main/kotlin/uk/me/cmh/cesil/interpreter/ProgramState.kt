package uk.me.cmh.cesil.interpreter

data class ProgramState(
    val output: String = "",
    val error: String = "",
    val accumulator: Int = 0,
    val variables: Map<String, Int> = mapOf(),
    val data: List<Int> = listOf(),
    val currentInstructionIndex: Int = 0,
    val isHalted: Boolean = false,
    val numberOfExecutedInstructions: Int = 0
) {

    fun nextProgramState(
        accumulator: Int = this.accumulator,
        output: String = this.output,
        error: String = this.error,
        isHalted: Boolean = this.isHalted,
        currentInstructionIndex: Int = this.currentInstructionIndex + 1
    ): ProgramState {
        return this.copy(
            accumulator = accumulator,
            output = output,
            error = error,
            isHalted = isHalted,
            currentInstructionIndex = currentInstructionIndex,
            numberOfExecutedInstructions = this.numberOfExecutedInstructions + 1
        )
    }

}