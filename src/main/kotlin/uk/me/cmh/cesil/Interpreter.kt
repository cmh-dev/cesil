package uk.me.cmh.cesil

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): List<String> = when(val parserResult = parser.parse(program)) {
        is ParserErrors -> parserResult.errorMessages
        is ParsedInstructions -> executor.execute(parserResult.instructions)
    }

}