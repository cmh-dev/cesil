package uk.me.cmh.cesil.interpreter.parser

import uk.me.cmh.cesil.interpreter.*
import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParsingResult {

       var parserState = ParserState()

        if (sourceCode.isBlank()) return ParsingErrors(listOf("NO SOURCE CODE"))

        sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }
            .forEach { line ->
                parserState = when {
                    line.startsWith("%") -> parserState.terminateInstructionSet()
                    line.startsWith("*") -> {
                        parserState = parserState.terminateDataSet()
                        return@forEach
                    }
                    else -> {
                        val parsedLine =
                            when(parserState.isInstructionSetTerminated) {
                                true -> parseDataLine(line)
                                false -> parseInstructionLine(line)
                            }
                        when (parsedLine) {
                            is DataLine -> parserState.addData(parsedLine.dataItems)
                            is InstructionLine -> {
                                val instruction = parsedLine.instruction
                                parserState.addInstruction(instruction)
                            }
                            is ErrorLine -> parserState.addError(parsedLine.error)
                        }
                    }
                }
            }

        if (!parserState.isInstructionSetTerminated) parserState = parserState.addError("MISSING INSTRUCTION SET TERMINATOR")
        if (!parserState.isDataSetTerminated) parserState = parserState.addError("MISSING DATA SET TERMINATOR")
        if (parserState.instructions.none { it.operator == Operator.HALT }) parserState = parserState.addError("MISSING HALT INSTRUCTION")

        return when {
            parserState.errors.isEmpty() -> ParsedProgram(Program(parserState.instructions, parserState.data))
            else -> ParsingErrors(parserState.errors)
        }

    }

    internal fun parseInstructionLine(line: String): ParsedLine {

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

    internal fun parseDataLine(line: String): ParsedLine = try {
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
