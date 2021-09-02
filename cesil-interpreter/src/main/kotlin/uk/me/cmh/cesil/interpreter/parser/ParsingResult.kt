package uk.me.cmh.cesil.interpreter

sealed class ParsingResult
class ParsingErrors(val errorMessages: List<String>) : ParsingResult()
class ParsedProgram(val program: Program) : ParsingResult()