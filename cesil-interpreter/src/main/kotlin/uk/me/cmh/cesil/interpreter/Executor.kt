package uk.me.cmh.cesil.interpreter

class Executor {

    companion object {
        const val MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED = 100
    }

    fun execute(program: Program): ExecutionResult {

        var programState = ProgramState(data = program.data)

        while (programState.instructionIndex <= program.instructions.lastIndex
            && programState.numberOfExecutedInstructions <= MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED
            && programState.isRunning
        ) {
            programState = executeNextInstruction(programState, program)
        }

        if (programState.numberOfExecutedInstructions > MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED)
            programState = programState.nextWithError(error = "MAXIMUM NUMBER OF EXECUTED INSTRUCTIONS EXCEEDED")

        return when {
            programState.error.isEmpty() -> ExecutionSuccess(programState.output.lines())
            else -> ExecutionFailure(listOf(programState.error))
        }

    }

    private fun executeNextInstruction(programState: ProgramState, program: Program): ProgramState {
        val instruction = program.instructions[programState.instructionIndex]
        return when (instruction.operator) {
            Operator.PRINT -> executeOutputInstruction(instruction.operand, programState)
            Operator.LINE -> executeOutputInstruction(System.lineSeparator(), programState)
            Operator.OUT -> executeOutputInstruction(programState.accumulator.toString(), programState)
            Operator.IN -> executeDataItemReadInstruction(programState)
            Operator.STORE -> executeStoreInstruction(programState, instruction)
            Operator.LOAD -> executeInstructionUsingValue(instruction, programState)
            Operator.ADD -> executeInstructionUsingValue(instruction, programState)
            Operator.SUBTRACT -> executeInstructionUsingValue(instruction, programState)
            Operator.MULTIPLY -> executeInstructionUsingValue(instruction, programState)
            Operator.DIVIDE -> executeInstructionUsingValue(instruction, programState)
            Operator.JUMP -> executeJumpInstruction(instruction, programState, program)
            Operator.JIZERO -> executeJumpInstruction(instruction, programState, program)
            Operator.JINEG -> executeJumpInstruction(instruction, programState, program)
            Operator.HALT -> programState.terminate()
            Operator.INVALID_OPERATOR -> programState
        }
    }

    private fun executeOutputInstruction(valueToAppend: String, programState: ProgramState) =
        programState.nextWithNewOutputValue(newOutputValue = programState.output + valueToAppend)

    private fun executeStoreInstruction(programState: ProgramState, instruction: Instruction) =
        programState.nextWithNewVariableList(
            newVariableList = mapOf(
                *programState.variables.entries.map { it.key to it.value }.toTypedArray(),
                instruction.operand to programState.accumulator
            )
        )

    private fun executeDataItemReadInstruction(programState: ProgramState) =
        if (programState.data.isNotEmpty())
            programState.nextNewAccumulatorValueAndData(
                newAccumulatorValue = programState.data.first(),
                newData = programState.data.drop(1)
            )
        else
            programState.nextWithError(error = "PROGRAM REQUIRES MORE DATA")

    private fun executeJumpInstruction(instruction: Instruction, programState: ProgramState, program: Program) =
        if (instruction.operator == Operator.JUMP ||
            (instruction.operator == Operator.JIZERO && programState.accumulator == 0) ||
            (instruction.operator == Operator.JINEG && programState.accumulator < 0)
        )
            executeJumpToLabel(instruction.operand, program.labeledInstructionIndexes, programState)
        else
            programState.next()

    private fun executeJumpToLabel(label: String, labeledInstructionIndexes: Map<String, Int>, programState: ProgramState) =
        when (val instructionIndex = labeledInstructionIndexes[label] ?: -1) {
            -1 -> programState.nextWithError(error = "JUMP TO NON EXISTENT LABEL ($label)")
            else -> programState.nextWithNewInstructionIndex(newInstructionIndex = instructionIndex)
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
                Operator.ADD -> currentProgramState.nextWithNewAccumulatorValue(newAccumulatorValue = currentProgramState.accumulator + value)
                Operator.SUBTRACT -> currentProgramState.nextWithNewAccumulatorValue(newAccumulatorValue = currentProgramState.accumulator - value)
                Operator.MULTIPLY -> currentProgramState.nextWithNewAccumulatorValue(newAccumulatorValue = currentProgramState.accumulator * value)
                Operator.DIVIDE -> {
                    if (value == 0) {
                        currentProgramState.nextWithError(error = "DIVISION BY ZERO")
                    } else {
                        currentProgramState.nextWithNewAccumulatorValue(newAccumulatorValue = currentProgramState.accumulator / value)
                    }
                }
                Operator.LOAD -> currentProgramState.nextWithNewAccumulatorValue(newAccumulatorValue = value)
                else -> currentProgramState
            }
        } else {
            currentProgramState.nextWithError(error = "NON EXISTENT VARIABLE (${instruction.operand})")
        }
    }


}

