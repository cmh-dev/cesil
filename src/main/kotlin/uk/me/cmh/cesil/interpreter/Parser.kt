package uk.me.cmh.cesil.interpreter

import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParserResult {

        val instructions = mutableListOf<Instruction>()
        val labeledInstructionIndexes = mutableMapOf<String, Int>()
        val data = mutableListOf<Int>()
        var inData = false
        val parseErrors = mutableListOf<String>()
        var dataTermination = false

        if (sourceCode.isBlank()) return ParserErrors(listOf("NO SOURCE CODE"))

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
                        inData -> data.addAll(parseDataLine(line))
                        else -> {
                            val instruction = parseInstructionLine(line)
                            instructions.add(instruction)
                            if (instruction.label != "") {
                                labeledInstructionIndexes.put(instruction.label, instructions.lastIndex)
                            }
                        }
                    }
                } catch (parserException: ParserException) {
                    parseErrors.add(parserException.message ?: "")
                }
            }

        if (!inData) {
            parseErrors.add("MISSING INSTRUCTION SET TERMINATOR")
        }
        if (!dataTermination) {
            parseErrors.add("MISSING DATA SET TERMINATOR")
        }
        if (instructions.filter { it.operator == Operator.HALT }.count() == 0) {
            parseErrors.add("MISSING HALT INSTRUCTION")
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
            operator == Operator.INVALID_OPERATOR && elements.size > 1 -> IndexedOperator(
                Operator.findOperator(elements[1]),
                1
            )
            else -> IndexedOperator(operator, 0)
        }

        if (indexedOperator.operator == Operator.INVALID_OPERATOR) throw ParserException("INSTRUCTION LINE INVALID [$line]")

        val label = if (indexedOperator.index == 0) "" else elements[0]
        var operand = elements.filterIndexed { index, _ -> index > indexedOperator.index }.joinToString(separator = " ")
        if (indexedOperator.operator == Operator.PRINT) {
            if (!operand.matches(Regex("\".*\""))) {
                throw ParserException("INSTRUCTION LINE OPERAND INVALID [$line]")
            } else {
                operand = operand.removeSurrounding("\"")
            }
        }

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
