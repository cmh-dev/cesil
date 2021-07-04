package uk.me.cmh.cesil.interpreter

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): ExecutionResult = when(val parserResult = parser.parse(program)) {
        is ParserErrors -> ExecutionFailure(parserResult.errorMessages.map { "*** $it ***" })
        is ParsedProgram ->
            when(val executionResult = executor.execute(parserResult.program)) {
                is ExecutionFailure -> ExecutionFailure(executionResult.output.map { "*** $it ***" })
                is ExecutionSuccess -> executionResult
            }
    }

}

