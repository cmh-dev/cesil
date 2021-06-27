package uk.me.cmh.cesil

class Parser {

    fun parse(program: String): ParserResult {
        TODO("NOT IMPLEMENTED")
    }

}

sealed class ParserResult
class ParserError(val errorMessages: List<String>) : ParserResult()
