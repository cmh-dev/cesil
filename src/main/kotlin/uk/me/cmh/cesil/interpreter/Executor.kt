package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {

        val outputBuffer = StringBuilder()
        val errors = mutableListOf<String>()
        var accumulator = 0
        val variables = mutableMapOf<String, Int>()
        val data = program.data.toMutableList()

        var instructionIndex = 0
        while (instructionIndex <= program.instructions.lastIndex) {
            val instruction = program.instructions[instructionIndex]
            instructionIndex++
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
                        errors.add("DIVISION BY ZERO")
                        break
                    } else {
                        accumulator /= divisor
                    }
                }
                Operator.STORE -> variables[instruction.operand] = accumulator
                Operator.LOAD -> accumulator = variables[instruction.operand] ?: 0
                Operator.JUMP -> instructionIndex = program.labeledInstructionIndexes[instruction.operand] ?: 0
                Operator.JIZERO ->
                    if (accumulator == 0) {
                        instructionIndex = program.labeledInstructionIndexes[instruction.operand] ?: 0
                    }
                Operator.JINEG ->
                    if (accumulator < 0) {
                        instructionIndex = program.labeledInstructionIndexes[instruction.operand] ?: 0
                    }
                Operator.IN -> accumulator = data.removeFirst()
                Operator.HALT -> break
            }
        }

        return when {
            errors.isEmpty() -> ExecutionSuccess(outputBuffer.lines())
            else -> ExecutionFailure(errors)
        }

    }

    private fun getValue(operand: String, variables: Map<String, Int>) : Int {
        return when {
            operand.matches(Regex("^\\d*\$")) -> operand.toInt()
            else -> variables[operand] ?: 0
        }
    }

}

