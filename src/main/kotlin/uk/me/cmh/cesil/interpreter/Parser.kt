package uk.me.cmh.cesil.interpreter

import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParserResult {

        var instructions = listOf<Instruction>()
        var labeledInstructionIndexes = mapOf<String, Int>()
        var data = listOf<Int>()
        var inData = false
        var parseErrors = listOf<String>()

        var dataTermination = false


        sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }.forEach { line ->
                try {
                    when {
                        line.startsWith("%") -> inData = true
                        line.startsWith("*") -> {
                            dataTermination = true
                            return@forEach
                        }
                        inData -> data = data + parseDataLine(line)
                        else -> {
                            val instruction = parseInstructionLine(line)
                            instructions = instructions + instruction
                            if (instruction.label != "") {
                                labeledInstructionIndexes = labeledInstructionIndexes + (instruction.label to instructions.lastIndex)
                            }
                        }
                    }
                } catch (parserException: ParserException) {
                    parseErrors = parseErrors + (parserException.message ?: "")
                }
            }

        if (!inData) {
            parseErrors = parseErrors + "NO INSTRUCTION SET TERMINATION"
        }
        if (!dataTermination) {
            parseErrors = parseErrors + "NO DATA TERMINATION"
        }

        return when {
            parseErrors.isEmpty() -> ParsedProgram(Program(instructions, labeledInstructionIndexes, data))
            else -> ParserErrors(parseErrors)
        }

    }

    fun parseInstructionLine(line: String): Instruction {

        val elements = line.split(Regex("\\s")).filterNot { it == "" }

        val operator = Operator.findOperator(elements[0])
        val indexedOperator = when {
            operator == Operator.INVALID_OPERATOR && elements.size > 1 -> IndexedOperator(Operator.findOperator(elements[1]), 1)
            else -> IndexedOperator(operator, 0)
        }

        if (indexedOperator.operator == Operator.INVALID_OPERATOR) throw ParserException("INSTRUCTION LINE INVALID [$line]")

        val label = if (indexedOperator.index == 0) "" else elements[0]
        val operand = elements.filterIndexed { index, _ -> index > indexedOperator.index }.joinToString(separator = " ")
        return Instruction(label, indexedOperator.operator, operand)

    }

    private fun parseDataLine(line: String): List<Int> = try {
        line.split(Regex("\\s")).filterNot { it == "" }.map { it.toInt() }
    } catch (e: NumberFormatException) {
        throw ParserException("DATA LINE INVALID [$line]")
    }

    private data class IndexedOperator(val operator: Operator, val index: Int)
    class ParserException(message: String) : Exception(message)

}

sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
