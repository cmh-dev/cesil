package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {

        var instructions = listOf<Instruction>()
        var data = listOf<Int>()
        var inData = false

        val lines = sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }

        lines.forEach {
            line ->
                when {
                    line.startsWith("%") -> inData = true
                    line.startsWith("*") -> return@forEach
                    inData -> data = data + parseData(line)
                    else -> instructions = instructions + parseInstruction(line)
                }
        }

        val program = Program(instructions, data)

        return ParsedProgram(program)

    }

    private fun parseInstruction(line: String): Instruction {

        val elements = line.split(Regex("\\s")).filterNot { it == "" }

        val indexedOperator = when (val operator = Operator.findOperator(elements[0])) {
            Operator.INVALID_OPERATOR -> IndexedOperator(Operator.findOperator(elements[1]), 1)
            else -> IndexedOperator(operator, 0)
        }

        val label = if (indexedOperator.index == 0) "" else elements[0]
        val operand = elements.filterIndexed { index, s -> index > indexedOperator.index }.joinToString(separator = " ")
        return Instruction(label, indexedOperator.operator, operand)

    }

    private fun parseData(line: String): List<Int> = line.split(Regex("\\s")).filterNot { it == "" }.map { it.toInt() }

    private data class IndexedOperator(val operator: Operator, val index: Int)

}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
