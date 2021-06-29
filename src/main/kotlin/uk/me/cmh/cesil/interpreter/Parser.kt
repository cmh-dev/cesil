package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {

        val trimmedSource = sourceCode.trim()
        if (trimmedSource.startsWith("(")) return ParsedProgram(Program(listOf(), listOf()))

        val elements = trimmedSource.split(Regex("\\s")).filterNot { it == "" }

        val indexedOperator = when (val operator = Operator.findOperator(elements[0])) {
            Operator.INVALID_OPERATOR -> IndexedOperator(Operator.findOperator(elements[1]), 1)
            else -> IndexedOperator(operator, 0)
        }

        val label = if (indexedOperator.index == 0) "" else elements[0]
        val operand = elements.filterIndexed { index, s -> index > indexedOperator.index }.joinToString(separator = " ")
        val instruction = Instruction(label, indexedOperator.operator, operand)
        val program = Program(listOf(instruction), listOf())

        return ParsedProgram(program)

    }

    private data class IndexedOperator(val operator: Operator, val index: Int)

}



sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
