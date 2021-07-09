package uk.me.cmh.cesil.interpreter

class Executor() {

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
                Operator.ADD -> executeInstructionUsingVariable(instruction)
                Operator.SUBTRACT -> executeInstructionUsingVariable(instruction)
                Operator.MULTIPLY -> executeInstructionUsingVariable(instruction)
                Operator.DIVIDE -> executeInstructionUsingVariable(instruction)
                Operator.STORE -> variables[instruction.operand] = accumulator
                Operator.LOAD -> executeInstructionUsingVariable(instruction, false)
                Operator.JUMP -> executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes)
                Operator.JIZERO ->  if (accumulator == 0) { executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes) }
                Operator.JINEG -> if (accumulator < 0) { executeJumpInstruction(instruction.operand, program.labeledInstructionIndexes) }
                Operator.IN -> accumulator = data.removeFirst()
                Operator.HALT -> break
            }
            if (error.isNotBlank()) break
        }

        return when {
            error.isEmpty() -> ExecutionSuccess(outputBuffer.lines())
            else -> ExecutionFailure(listOf(error))
        }


    }

    private fun executeInstructionUsingVariable(instruction: Instruction, operandCanContainLiteral: Boolean = true) {
        val value = when {
            instruction.operand.matches(Regex("^\\d*\$")) && operandCanContainLiteral -> instruction.operand.toInt()
            else -> variables[instruction.operand]
        }
        if (value != null) {
            when(instruction.operator) {
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

