package uk.me.cmh.cesil.interpreter

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): ExecutionResult = when (val parserResult = parser.parse(program)) {
        is ParsingErrors -> ExecutionFailure(parserResult.errorMessages.formatErrorMessages())
        is ParsedProgram ->
            when (val executionResult = executor.execute(parserResult.program)) {
                is ExecutionFailure -> ExecutionFailure(executionResult.output.formatErrorMessages())
                is ExecutionSuccess -> executionResult
            }
    }

    private fun List<String>.formatErrorMessages() = this.map { "*** $it ***" }

}

