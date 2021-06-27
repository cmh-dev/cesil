package uk.me.cmh.cesil

class Parser {

    fun parse(program: String): ParserResult {
        TODO("NOT IMPLEMENTED")
    }

}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedInstructions(val instructions: List<Instruction>) : ParserResult()

