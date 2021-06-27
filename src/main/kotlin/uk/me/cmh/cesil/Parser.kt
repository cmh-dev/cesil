package uk.me.cmh.cesil

class Parser {

    fun parse(program: String): ParserResult {
        return ParserError("NOT IMPLEMENTED")
    }

}

sealed class ParserResult
class ParserError(val errorMessage: String) : ParserResult()
