package uk.me.cmh.cesil

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): List<String> {

        val parserResult = parser.parse(program)

        if (parserResult is ParserError) {
            return listOf(parserResult.errorMessage)
        }

        return emptyList()

    }

}