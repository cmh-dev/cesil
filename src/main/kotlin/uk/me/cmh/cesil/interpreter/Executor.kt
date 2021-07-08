package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {

        val outputBuffer = StringBuilder()
        var accumulator = 0
        val variables = mutableMapOf<String, Int>()

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
                Operator.DIVIDE -> accumulator /= getValue(instruction.operand, variables)
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
            }
        }

        return ExecutionSuccess(outputBuffer.lines())

    }

    private fun getValue(operand: String, variables: Map<String, Int>) : Int {
        return when {
            operand.matches(Regex("^\\d*\$")) -> operand.toInt()
            else -> variables[operand] ?: 0
        }
    }

}

