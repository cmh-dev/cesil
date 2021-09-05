package uk.me.cmh.cesil.interpreter.parser

import uk.me.cmh.cesil.interpreter.Program

sealed class ParsingResult
class ParsingErrors(val errorMessages: List<String>) : ParsingResult()
class ParsedProgram(val program: Program) : ParsingResult()