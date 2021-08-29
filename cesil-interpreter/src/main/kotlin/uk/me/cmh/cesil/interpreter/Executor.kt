package uk.me.cmh.cesil.interpreter

class Executor {

    companion object {
        const val MAXIMUM_EXECUTED_INSTRUCTIONS = 100
    }

    private data class ProgramState(
        val output: String = "",
        val error: String = "",
        val accumulator: Int = 0,
        val variables: Map<String, Int> = mapOf(),
        val data: List<Int> = listOf(),
        val currentInstructionIndex: Int = 0
    )

    fun execute(program: Program): ExecutionResult {

        var programState = ProgramState(data = program.data)
        var executedInstructionCount = 0

        while (programState.currentInstructionIndex <= program.instructions.lastIndex && programState.error.isBlank() && executedInstructionCount <= MAXIMUM_EXECUTED_INSTRUCTIONS) {
            val instruction = program.instructions[programState.currentInstructionIndex]
            programState = when (instruction.operator) {
                Operator.PRINT -> getNextProgramStateWithOutputAddition(instruction.operand, programState)
                Operator.LINE -> getNextProgramStateWithOutputAddition(System.lineSeparator(), programState)
                Operator.OUT -> getNextProgramStateWithOutputAddition(programState.accumulator.toString(), programState)
                Operator.ADD -> executeInstructionUsingValue(instruction, programState)
                Operator.SUBTRACT -> executeInstructionUsingValue(instruction, programState)
                Operator.MULTIPLY -> executeInstructionUsingValue(instruction, programState)
                Operator.DIVIDE -> executeInstructionUsingValue(instruction, programState)
                Operator.STORE -> programState.copy(
                    variables = mapOf(
                        *programState.variables.entries.map { it.key to it.value }.toTypedArray(),
                        instruction.operand to programState.accumulator
                    ), currentInstructionIndex = programState.currentInstructionIndex + 1
                )
                Operator.LOAD -> executeInstructionUsingValue(instruction, programState)
                Operator.JUMP -> executeJumpInstruction(
                    instruction.operand,
                    program.labeledInstructionIndexes,
                    programState
                )
                Operator.JIZERO -> if (programState.accumulator == 0) {
                    executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes, programState)
                } else {
                    programState.copy(currentInstructionIndex = programState.currentInstructionIndex + 1)
                }
                Operator.JINEG -> if (programState.accumulator < 0) {
                    executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes, programState)
                } else {
                    programState.copy(currentInstructionIndex = programState.currentInstructionIndex + 1)
                }
                Operator.IN -> if (programState.data.isNotEmpty()) {
                    programState.copy(
                        accumulator = programState.data.first(),
                        data = programState.data.drop(1),
                        currentInstructionIndex = programState.currentInstructionIndex + 1
                    )
                } else {
                    getNextProgramStateWithError("PROGRAM REQUIRES MORE DATA", programState)
                }
                Operator.HALT -> break
                Operator.INVALID_OPERATOR -> programState
            }
            executedInstructionCount++
            if (programState.error.isNotBlank()) break
        }

        if (executedInstructionCount > MAXIMUM_EXECUTED_INSTRUCTIONS) {
            programState = programState.copy(error = "MAXIMUM NUMBER OF EXECUTED INSTRUCTIONS EXCEEDED")


        }

        return when {
            programState.error.isEmpty() -> ExecutionSuccess(programState.output.lines())
            else -> ExecutionFailure(listOf(programState.error))
        }

    }

    private fun executeInstructionUsingValue(
        instruction: Instruction,
        currentProgramState: ProgramState
    ): ProgramState {
        val value = when {
            instruction.operand.matches(Regex("^[+-]*\\d*\$")) -> instruction.operand.toInt()
            else -> currentProgramState.variables[instruction.operand]
        }
        return if (value != null) {
            return when (instruction.operator) {
                Operator.ADD -> getNextProgramStateWithNewAccumulatorValue(
                    currentProgramState.accumulator + value,
                    currentProgramState
                )
                Operator.SUBTRACT -> getNextProgramStateWithNewAccumulatorValue(
                    currentProgramState.accumulator - value,
                    currentProgramState
                )
                Operator.MULTIPLY -> getNextProgramStateWithNewAccumulatorValue(
                    currentProgramState.accumulator * value,
                    currentProgramState
                )
                Operator.DIVIDE -> {
                    if (value == 0) {
                        getNextProgramStateWithError("DIVISION BY ZERO", currentProgramState)
                    } else {
                        getNextProgramStateWithNewAccumulatorValue(
                            currentProgramState.accumulator / value,
                            currentProgramState
                        )
                    }
                }
                Operator.LOAD -> getNextProgramStateWithNewAccumulatorValue(value, currentProgramState)
                else -> currentProgramState
            }
        } else {
            getNextProgramStateWithError(buildNonExistentVariableMessage(instruction.operand), currentProgramState)
        }
    }

    private fun executeJumpInstruction(
        label: String,
        labeledInstructionIndexes: Map<String, Int>,
        currentProgramState: ProgramState
    ): ProgramState {
        return when (val instructionIndex = labeledInstructionIndexes[label] ?: -1) {
            -1 -> getNextProgramStateWithError("JUMP TO NON EXISTENT LABEL ($label)", currentProgramState)
            else -> currentProgramState.copy(currentInstructionIndex = instructionIndex)
        }
    }

    private fun getNextProgramStateWithOutputAddition(
        additionalOutput: String,
        currentProgramState: ProgramState
    ): ProgramState =
        currentProgramState.copy(
            output = "${currentProgramState.output}${additionalOutput}",
            currentInstructionIndex = currentProgramState.currentInstructionIndex + 1
        )

    private fun getNextProgramStateWithNewAccumulatorValue(
        accumulatorValue: Int,
        currentProgramState: ProgramState
    ): ProgramState =
        currentProgramState.copy(
            accumulator = accumulatorValue,
            currentInstructionIndex = currentProgramState.currentInstructionIndex + 1
        )

    private fun getNextProgramStateWithError(errorValue: String, currentProgramState: ProgramState): ProgramState =
        currentProgramState.copy(
            error = errorValue,
            currentInstructionIndex = currentProgramState.currentInstructionIndex + 1
        )


    private fun buildNonExistentVariableMessage(variableName: String) = "NON EXISTENT VARIABLE ($variableName)"

}

