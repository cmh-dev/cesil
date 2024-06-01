package cmh.cesil.interpreter.parser

import cmh.cesil.interpreter.Program

sealed class ParsingResult
class ParsingErrors(val errorMessages: List<String>) : ParsingResult()
class ParsedProgram(val program: Program) : ParsingResult()