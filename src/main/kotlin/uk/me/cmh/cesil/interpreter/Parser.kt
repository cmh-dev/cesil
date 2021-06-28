package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {
        return ParsedProgram(Program(listOf(Instruction("", Operator.PRINT, "Hello World")), listOf()))
    }

}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()

