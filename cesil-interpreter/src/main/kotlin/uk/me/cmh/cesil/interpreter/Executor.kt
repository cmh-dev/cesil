package uk.me.cmh.cesil.interpreter

class Executor {

    companion object {
        const val MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED = 100
    }

    fun execute(program: Program): ExecutionResult {

        var programState = ProgramState(data = program.data)

        while (programState.instructionIndex <= program.instructions.lastIndex
            && programState.error.isBlank()
            && programState.numberOfExecutedInstructions <= MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED
            && programState.isRunning
        ) {
            programState = executeNextInstruction(programState, program)
        }

        if (programState.numberOfExecutedInstructions > MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED) {
            programState = programState.copy(error = "MAXIMUM NUMBER OF EXECUTED INSTRUCTIONS EXCEEDED")
        }

        return when {
            programState.error.isEmpty() -> ExecutionSuccess(programState.output.lines())
            else -> ExecutionFailure(listOf(programState.error))
        }

    }

    private fun executeNextInstruction(programState: ProgramState, program: Program): ProgramState {
        val instruction = program.instructions[programState.instructionIndex]
        return when (instruction.operator) {
            Operator.PRINT -> programState.nextProgramState(output = programState.output + instruction.operand)
            Operator.LINE -> programState.nextProgramState(output = programState.output + System.lineSeparator())
            Operator.OUT -> programState.nextProgramState(output = programState.output + programState.accumulator.toString())
            Operator.ADD -> executeInstructionUsingValue(instruction, programState)
            Operator.SUBTRACT -> executeInstructionUsingValue(instruction, programState)
            Operator.MULTIPLY -> executeInstructionUsingValue(instruction, programState)
            Operator.DIVIDE -> executeInstructionUsingValue(instruction, programState)
            Operator.STORE -> programState.nextProgramState(
                variables = mapOf(
                    *programState.variables.entries.map { it.key to it.value }.toTypedArray(),
                    instruction.operand to programState.accumulator
                )
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
                programState.nextProgramState()
            }
            Operator.JINEG -> if (programState.accumulator < 0) {
                executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes, programState)
            } else {
                programState.nextProgramState()
            }
            Operator.IN -> if (programState.data.isNotEmpty()) {
                programState.nextProgramState(
                    accumulator = programState.data.first(),
                    data = programState.data.drop(1),
                )
            } else {
                programState.nextProgramState(error = "PROGRAM REQUIRES MORE DATA")
            }
            Operator.HALT -> programState.nextProgramState(isRunning = false)
            Operator.INVALID_OPERATOR -> programState
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
                Operator.ADD -> currentProgramState.nextProgramState(accumulator = currentProgramState.accumulator + value)
                Operator.SUBTRACT -> currentProgramState.nextProgramState(accumulator = currentProgramState.accumulator - value)
                Operator.MULTIPLY -> currentProgramState.nextProgramState(accumulator = currentProgramState.accumulator * value)
                Operator.DIVIDE -> {
                    if (value == 0) {
                        currentProgramState.nextProgramState(error = "DIVISION BY ZERO")
                    } else {
                        currentProgramState.nextProgramState(accumulator = currentProgramState.accumulator / value)
                    }
                }
                Operator.LOAD -> currentProgramState.nextProgramState(accumulator = value)
                else -> currentProgramState
            }
        } else {
            currentProgramState.nextProgramState(error = "NON EXISTENT VARIABLE (${instruction.operand})")
        }
    }

    private fun executeJumpInstruction(
        label: String,
        labeledInstructionIndexes: Map<String, Int>,
        currentProgramState: ProgramState
    ): ProgramState {
        return when (val instructionIndex = labeledInstructionIndexes[label] ?: -1) {
            -1 -> currentProgramState.nextProgramState(error = "JUMP TO NON EXISTENT LABEL ($label)")
            else -> currentProgramState.nextProgramState(instructionIndex = instructionIndex)
        }
    }


}

