package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {

        val output = mutableListOf<String>()
        val outputBuffer = StringBuilder()
        var accumulator = 0

        program.instructions.forEach {
            when(it.operator) {
                Operator.PRINT -> outputBuffer.append(it.operand)
                Operator.LINE -> {
                    output.add(outputBuffer.toString())
                    outputBuffer.clear()
                }
                Operator.OUT -> outputBuffer.append(accumulator)
                Operator.IN -> accumulator = it.operand.toInt()
            }
        }
        output.add(outputBuffer.toString())
        return ExecutionSuccess(output)
    }

}

