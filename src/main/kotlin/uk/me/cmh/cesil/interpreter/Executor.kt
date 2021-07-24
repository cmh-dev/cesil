package uk.me.cmh.cesil.interpreter

class Executor {

    private val outputBuffer = StringBuilder()
    private var error = ""
    private var accumulator = 0
    private val variables = mutableMapOf<String, Int>()
    private var currentInstructionIndex = 0

    fun execute(program: Program): ExecutionResult {

        outputBuffer.clear()
        accumulator = 0
        variables.clear()
        currentInstructionIndex = 0
        val data = program.data.toMutableList()

        while (currentInstructionIndex <= program.instructions.lastIndex && error.isBlank()) {
            val instruction = program.instructions[currentInstructionIndex]
            currentInstructionIndex++
            when(instruction.operator) {
                Operator.PRINT -> outputBuffer.append(instruction.operand)
                Operator.LINE -> outputBuffer.appendLine()
                Operator.OUT -> outputBuffer.append(accumulator)
                Operator.ADD -> executeInstructionUsingValue(instruction)
                Operator.SUBTRACT -> executeInstructionUsingValue(instruction)
                Operator.MULTIPLY -> executeInstructionUsingValue(instruction)
                Operator.DIVIDE -> executeInstructionUsingValue(instruction)
                Operator.STORE -> variables[instruction.operand] = accumulator
                Operator.LOAD -> executeInstructionUsingValue(instruction)
                Operator.JUMP -> executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes)
                Operator.JIZERO ->  if (accumulator == 0) { executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes) }
                Operator.JINEG -> if (accumulator < 0) { executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes) }
                Operator.IN -> if (data.isNotEmpty()) { accumulator = data.removeFirst() } else error = "PROGRAM REQUIRES MORE DATA"
                Operator.HALT -> break
            }
            if (error.isNotBlank()) break
        }

        return when {
            error.isEmpty() -> ExecutionSuccess(outputBuffer.lines())
            else -> ExecutionFailure(listOf(error))
        }

    }

    private fun executeInstructionUsingValue(instruction: Instruction) {
        val value = when {
            instruction.operand.matches(Regex("^(\\+|-)*\\d*\$")) -> instruction.operand.toInt()
            else -> variables[instruction.operand]
        }
        if (value != null) {
            when (instruction.operator) {
                Operator.ADD -> accumulator += value
                Operator.SUBTRACT -> accumulator -= value
                Operator.MULTIPLY -> accumulator *= value
                Operator.DIVIDE -> {
                    if (value == 0) {
                        error = "DIVISION BY ZERO"
                    } else {
                        accumulator /= value
                    }
                }
                Operator.LOAD -> accumulator = value
            }
        } else {
            error = buildNonExistentVariableMessage(instruction.operand)
        }
    }

    private fun buildNonExistentVariableMessage(variableName: String) = "NON EXISTENT VARIABLE ($variableName)"

    private fun executeJumpInstruction(label: String, labeledInstructionIndexes: Map<String, Int>) {
        currentInstructionIndex = labeledInstructionIndexes[label] ?: -1
        if (currentInstructionIndex == -1) {
            error = "JUMP TO NON EXISTENT LABEL ($label)"
        }
    }

}

