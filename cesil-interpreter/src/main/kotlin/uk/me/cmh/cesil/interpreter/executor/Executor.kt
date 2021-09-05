package uk.me.cmh.cesil.interpreter.executor

import uk.me.cmh.cesil.interpreter.*

class Executor {

    fun execute(program: Program): ExecutionResult {
        var executionState = ExecutionState(data = program.data)
        while (executionState.instructionIndex <= program.instructions.lastIndex
            && executionState.numberOfExecutedInstructions <= MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED
            && executionState.isRunning
        ) {
            executionState = executeNextInstruction(executionState, program)
        }
        if (executionState.numberOfExecutedInstructions > MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED)
            executionState = executionState.nextWithError(error = "MAXIMUM NUMBER OF EXECUTED INSTRUCTIONS EXCEEDED")
        return when {
            executionState.error.isEmpty() -> ExecutionSuccess(executionState.output.lines())
            else -> ExecutionFailure(listOf(executionState.error))
        }
    }

    private fun executeNextInstruction(executionState: ExecutionState, program: Program): ExecutionState {
        val instruction = program.instructions[executionState.instructionIndex]
        return when (instruction.operator) {
            Operator.PRINT -> executeOutputInstruction(instruction.operand, executionState)
            Operator.LINE -> executeOutputInstruction(System.lineSeparator(), executionState)
            Operator.OUT -> executeOutputInstruction(executionState.accumulator.toString(), executionState)
            Operator.IN -> executeDataItemReadInstruction(executionState)
            Operator.STORE -> executeStoreInstruction(executionState, instruction)
            Operator.LOAD -> executeInstructionUsingValue(instruction, executionState)
            Operator.ADD -> executeInstructionUsingValue(instruction, executionState)
            Operator.SUBTRACT -> executeInstructionUsingValue(instruction, executionState)
            Operator.MULTIPLY -> executeInstructionUsingValue(instruction, executionState)
            Operator.DIVIDE -> executeInstructionUsingValue(instruction, executionState)
            Operator.JUMP -> executeJumpInstruction(instruction, executionState, program)
            Operator.JIZERO -> executeJumpInstruction(instruction, executionState, program)
            Operator.JINEG -> executeJumpInstruction(instruction, executionState, program)
            Operator.HALT -> executionState.terminate()
            Operator.INVALID_OPERATOR -> executionState
        }
    }

    private fun executeOutputInstruction(valueToAppend: String, executionState: ExecutionState) =
        executionState.nextWithNewOutputValue(newOutputValue = executionState.output + valueToAppend)

    private fun executeDataItemReadInstruction(executionState: ExecutionState) =
        if (executionState.data.isNotEmpty())
            executionState.nextNewAccumulatorValueAndData(
                newAccumulatorValue = executionState.data.first(),
                newData = executionState.data.drop(1)
            )
        else
            executionState.nextWithError(error = "PROGRAM REQUIRES MORE DATA")

    private fun executeStoreInstruction(executionState: ExecutionState, instruction: Instruction) =
        executionState.nextWithNewVariableList(
            newVariableList = mapOf(
                *executionState.variables.entries.map { it.key to it.value }.toTypedArray(),
                instruction.operand to executionState.accumulator
            )
        )

    private fun executeJumpInstruction(instruction: Instruction, executionState: ExecutionState, program: Program) =
        if (instruction.operator == Operator.JUMP ||
            (instruction.operator == Operator.JIZERO && executionState.accumulator == 0) ||
            (instruction.operator == Operator.JINEG && executionState.accumulator < 0)
        )
            executeJumpToLabel(instruction.operand, program.labeledInstructionIndexes, executionState)
        else
            executionState.next()

    private fun executeJumpToLabel(
        label: String,
        labeledInstructionIndexes: Map<String, Int>,
        executionState: ExecutionState
    ) =
        when (val instructionIndex = labeledInstructionIndexes[label] ?: -1) {
            -1 -> executionState.nextWithError(error = "JUMP TO NON EXISTENT LABEL ($label)")
            else -> executionState.nextWithNewInstructionIndex(newInstructionIndex = instructionIndex)
        }

    private fun executeInstructionUsingValue(instruction: Instruction, executionState: ExecutionState): ExecutionState {
        val value = extractValueFromOperand(instruction.operand, executionState)
            ?: return executionState.nextWithError(error = "NON EXISTENT VARIABLE (${instruction.operand})")
        return when (instruction.operator) {
            Operator.ADD -> executionState.nextWithNewAccumulatorValue(newAccumulatorValue = executionState.accumulator + value)
            Operator.SUBTRACT -> executionState.nextWithNewAccumulatorValue(newAccumulatorValue = executionState.accumulator - value)
            Operator.MULTIPLY -> executionState.nextWithNewAccumulatorValue(newAccumulatorValue = executionState.accumulator * value)
            Operator.DIVIDE ->
                if (value == 0)
                    executionState.nextWithError(error = "DIVISION BY ZERO")
                else
                    executionState.nextWithNewAccumulatorValue(newAccumulatorValue = executionState.accumulator / value)
            Operator.LOAD -> executionState.nextWithNewAccumulatorValue(newAccumulatorValue = value)
            else -> executionState
        }
    }

    private fun extractValueFromOperand(operand: String, currentExecutionState: ExecutionState) =
        when {
            operand.matches(Regex("^[+-]*\\d*\$")) -> operand.toInt()
            else -> currentExecutionState.variables[operand]
        }

    companion object {
        const val MAXIMUM_NUMBER_OF_EXECUTED_INSTRUCTIONS_ALLOWED = 100
    }

}

