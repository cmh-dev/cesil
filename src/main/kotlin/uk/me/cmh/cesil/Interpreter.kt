package uk.me.cmh.cesil

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): List<String> = when(val parserResult = parser.parse(program)) {
        is Errors -> parserResult.errorMessages
        is Instructions -> executor.execute(parserResult.instructions)
    }

}