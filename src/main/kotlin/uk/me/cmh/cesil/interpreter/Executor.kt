package uk.me.cmh.cesil.interpreter

class Executor {

    fun execute(program: Program): ExecutionResult {
        var output = listOf<String>()
        program.instructions.forEach {
            if (it.operator == Operator.PRINT) {
                output = output + it.operand
            }
        }
        return ExecutionSuccess(output)
    }

}

