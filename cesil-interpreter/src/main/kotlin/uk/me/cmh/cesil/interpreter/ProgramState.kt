package uk.me.cmh.cesil.interpreter

data class ProgramState(
    val output: String = "",
    val error: String = "",
    val accumulator: Int = 0,
    val variables: Map<String, Int> = mapOf(),
    val data: List<Int> = listOf(),
    val instructionIndex: Int = 0,
    val isHalted: Boolean = false,
    val numberOfExecutedInstructions: Int = 0
) {

    fun nextProgramState(
        accumulator: Int = this.accumulator,
        output: String = this.output,
        variables: Map<String, Int> = this.variables,
        data: List<Int> = this.data,
        error: String = this.error,
        isHalted: Boolean = this.isHalted,
        instructionIndex: Int = this.instructionIndex + 1
    ): ProgramState = this.copy(
        accumulator = accumulator,
        output = output,
        variables = variables,
        data = data,
        error = error,
        isHalted = isHalted,
        instructionIndex = instructionIndex,
        numberOfExecutedInstructions = this.numberOfExecutedInstructions + 1
    )

}