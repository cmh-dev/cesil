package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {

        val outputBuffer = StringBuilder()
        var accumulator = 0
        var variables = mutableMapOf<String, Int>()

        program.instructions.forEach {
            when(it.operator) {
                Operator.PRINT -> outputBuffer.append(it.operand)
                Operator.LINE -> outputBuffer.appendLine()
                Operator.OUT -> outputBuffer.append(accumulator)
                Operator.ADD -> accumulator += getValue(it.operand, variables)
                Operator.SUBTRACT -> accumulator -= getValue(it.operand, variables)
                Operator.MULTIPLY -> accumulator *= getValue(it.operand, variables)
                Operator.DIVIDE -> accumulator /= getValue(it.operand, variables)
                Operator.STORE -> variables[it.operand] = accumulator
                Operator.LOAD -> accumulator = variables[it.operand] ?: 0
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

