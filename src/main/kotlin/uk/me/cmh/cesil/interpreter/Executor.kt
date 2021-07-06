package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {
        val output = mutableListOf<String>()
        var outputBuffer = ""
        program.instructions.forEach {
            if (it.operator == Operator.PRINT) {
                outputBuffer += it.operand
            }
            if (it.operator == Operator.LINE) {
                output.add(outputBuffer)
                outputBuffer = ""
            }
        }
        output.add(outputBuffer)
        return ExecutionSuccess(output)
    }

}

