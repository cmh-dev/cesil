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
                Operator.ADD -> accumulator += getValue(instruction.operand, variables)
                Operator.SUBTRACT -> accumulator -= getValue(instruction.operand, variables)
                Operator.MULTIPLY -> accumulator *= getValue(instruction.operand, variables)
                Operator.DIVIDE -> {
                    val divisor = getValue(instruction.operand, variables)
                    if (divisor == 0) {
                        error = "DIVISION BY ZERO"
                    } else {
                        accumulator /= divisor
                    }
                }
                Operator.STORE -> variables[instruction.operand] = accumulator
                Operator.LOAD -> accumulator = variables[instruction.operand] ?: 0
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

    private fun executeJumpInstruction(label: String, labeledInstructionIndexes: Map<String, Int>) {
        currentInstructionIndex = labeledInstructionIndexes[label] ?: -1
        if (currentInstructionIndex == -1) {
            error = "JUMP TO NON EXISTENT LABEL ($label)"
        }
    }

    private fun getValue(operand: String, variables: Map<String, Int>) : Int {
        return when {
            operand.matches(Regex("^\\d*\$")) -> operand.toInt()
            else -> variables[operand] ?: 0
        }
    }

}

