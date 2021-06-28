package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {
        val elements = sourceCode.trim().split(Regex("\\s")).filterNot { it == "" }
        val label = ""
        val operator = Operator.findOperator(elements[0])
        val operand = elements.filterIndexed { index, s -> index > 0 }.joinToString(separator = " ")
        val instruction = Instruction(label, operator, operand)
        val program = Program(listOf(instruction), listOf())
        return ParsedProgram(program)
    }


}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
