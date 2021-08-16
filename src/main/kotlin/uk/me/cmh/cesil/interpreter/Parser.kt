package uk.me.cmh.cesil.interpreter

import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParserResult {

        val instructions = mutableListOf<Instruction>()
        val labeledInstructionIndexes = mutableMapOf<String, Int>()
        val data = mutableListOf<Int>()
        var inData = false
        val parseErrors = mutableListOf<String>()
        var dataTerminated = false

        if (sourceCode.isBlank()) return ParserErrors(listOf("NO SOURCE CODE"))

        sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }
            .forEach { line ->
                when {
                    line.startsWith("%") -> inData = true
                    line.startsWith("*") -> {
                        dataTerminated = true
                        return@forEach
                    }
                    else -> {
                        val parsedLine =
                            when(inData) {
                                true -> parseDataLine(line)
                                false -> parseInstructionLine(line)
                            }
                        when (parsedLine) {
                            is DataLine -> data.addAll(parsedLine.dataItems)
                            is InstructionLine -> {
                                val instruction = parsedLine.instruction
                                instructions.add(instruction)
                                if (instruction.label != "") {
                                    labeledInstructionIndexes[instruction.label] = instructions.lastIndex
                                }
                            }
                            is ErrorLine -> parseErrors.add(parsedLine.error)
                        }
                    }
                }
            }

        if (!inData) {
            parseErrors.add("MISSING INSTRUCTION SET TERMINATOR")
        }
        if (!dataTerminated) {
            parseErrors.add("MISSING DATA SET TERMINATOR")
        }
        if (instructions.none { it.operator == Operator.HALT }) {
            parseErrors.add("MISSING HALT INSTRUCTION")
        }

        return when {
            parseErrors.isEmpty() -> ParsedProgram(Program(instructions, labeledInstructionIndexes, data))
            else -> ParserErrors(parseErrors)
        }

    }

    fun parseInstructionLine(line: String): ParsedLine {

        val elements = line.split(Regex("\\s")).filterNot { it == "" }

        val operator = Operator.findOperator(elements[0])
        val indexedOperator = when {
            operator == Operator.INVALID_OPERATOR && elements.size > 1 -> IndexedOperator(
                Operator.findOperator(elements[1]),
                1
            )
            else -> IndexedOperator(operator, 0)
        }

        if (indexedOperator.operator == Operator.INVALID_OPERATOR) return ErrorLine("INSTRUCTION LINE INVALID [$line]")

        val label = when (indexedOperator.index) {
            0 -> ""
            else -> elements[0]
        }

        var operand = elements.filterIndexed { index, _ -> index > indexedOperator.index }.joinToString(separator = " ")
        if (indexedOperator.operator == Operator.PRINT) {
            if (operand.matches(Regex("\".*\""))) {
                operand = operand.removeSurrounding("\"")
            } else {
                return ErrorLine("INSTRUCTION LINE OPERAND INVALID [$line]")
            }
        }

        return InstructionLine(Instruction(label, indexedOperator.operator, operand))

    }

    fun parseDataLine(line: String): ParsedLine = try {
        DataLine(line.split(Regex("\\s")).filterNot { it == "" }.map { it.toInt() })
    } catch (e: NumberFormatException) {
        ErrorLine("DATA LINE INVALID [$line]")
    }

    private data class IndexedOperator(val operator: Operator, val index: Int)

    sealed class ParsedLine
    class InstructionLine(val instruction: Instruction) : ParsedLine()
    class ErrorLine(val error: String) : ParsedLine()
    class DataLine(val dataItems: List<Int>) : ParsedLine()

}


sealed class ParserResult
class ParserErrors(val errorMessages: List<String>) : ParserResult()
class ParsedProgram(val program: Program) : ParserResult()
