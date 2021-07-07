package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {

        val outputBuffer = StringBuilder()
        var accumulator = 0

        program.instructions.forEach {
            when(it.operator) {
                Operator.PRINT -> outputBuffer.append(it.operand)
                Operator.LINE -> outputBuffer.appendLine()
                Operator.OUT -> outputBuffer.append(accumulator)
                Operator.IN -> accumulator = it.operand.toInt()
                Operator.ADD -> accumulator += it.operand.toInt()
                Operator.SUBTRACT -> accumulator -= it.operand.toInt()
                Operator.MULTIPLY -> accumulator *= it.operand.toInt()
                Operator.DIVIDE -> accumulator /= it.operand.toInt()
            }
        }

        return ExecutionSuccess(outputBuffer.lines())

    }

}

