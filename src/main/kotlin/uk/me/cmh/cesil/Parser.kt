package uk.me.cmh.cesil

class Parser {

    fun parse(program: String): ParserResult {
        TODO("NOT IMPLEMENTED")
    }

}

sealed class ParserResult
class Errors(val errorMessages: List<String>) : ParserResult()
class Instructions(val instructions: List<Instruction>) : ParserResult()

