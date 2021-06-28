package uk.me.cmh.cesil.interpreter

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): ExecutionResult = when(val parserResult = parser.parse(program)) {
        is ParserErrors -> ExecutionFailure(parserResult.errorMessages)
        is ParsedProgram -> executor.execute(parserResult.program)
    }

}

