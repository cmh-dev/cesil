package uk.me.cmh.cesil.interpreter

class Parser {

    fun parse(sourceCode: String): ParserResult {

        var instructions = listOf<Instruction>()
        var data = listOf<Int>()
        var inData = false
        var parseErrors = listOf<String>()

        val lines = sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }

        lines.forEach { line ->
            try {
                when {
                    line.startsWith("%") -> inData = true
                    line.startsWith("*") -> return@forEach
                    inData -> data = data + parseData(line)
                    else -> instructions = instructions + parseInstruction(line)
                }
            } catch (parserException: ParserException) {
                parseErrors = parseErrors + (parserException.message ?: "")
            }
        }

        return when {
            parseErrors.isEmpty() -> ParsedProgram(Program(instructions, data))
            else -> ParserErrors(parseErrors)
        }

    }

    private fun parseInstruction(line: String): Instruction {

        val elements = line.split(Regex("\\s")).filterNot { it == "" }

        val indexedOperator = when (val operator = Operator.findOperator(elements[0])) {
            Operator.INVALID_OPERATOR -> IndexedOperator(Operator.findOperator(elements[1]), 1)
            else -> IndexedOperator(operator, 0)
        }

        if (indexedOperator.operator == Operator.INVALID_OPERATOR) throw ParserException("INSTRUCTION LINE INVALID [$line]")

        val label = if (indexedOperator.index == 0) "" else elements[0]
        val operand = elements.filterIndexed { index, _ -> index > indexedOperator.index }.joinToString(separator = " ")
        return Instruction(label, indexedOperator.operator, operand)

    }

    private fun parseData(line: String): List<Int> = line.split(Regex("\\s")).filterNot { it == "" }.map { it.toInt() }

    private data class IndexedOperator(val operator: Operator, val index: Int)
    private class ParserException(message: String) : Exception(message)



}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
