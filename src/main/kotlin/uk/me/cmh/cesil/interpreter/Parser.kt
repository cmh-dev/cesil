package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {
        TODO("NOT IMPLEMENTED")
    }

}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()

