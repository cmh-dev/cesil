package uk.me.cmh.cesil.interpreter.parser

import uk.me.cmh.cesil.interpreter.*
import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParsingResult {
        if (sourceCode.isBlank()) return ParsingErrors(listOf("NO SOURCE CODE"))
        val parserState = parseSourceCode(sourceCode)
        return when {
            parserState.errors.isEmpty() -> ParsedProgram(Program(parserState.instructions, parserState.data))
            else -> ParsingErrors(parserState.errors)
        }
    }

    private fun parseSourceCode(sourceCode: String): ParsingState {
        var parsingState = ParsingState()
        extractNonBlankAndCommentLines(sourceCode).forEach { line ->
                parsingState = parseLine(parsingState, line)
                if (parsingState.isDataSetTerminated) return@forEach
            }
        if (!parsingState.isInstructionSetTerminated) parsingState =
            parsingState.addError("MISSING INSTRUCTION SET TERMINATOR")
        if (!parsingState.isDataSetTerminated) parsingState = parsingState.addError("MISSING DATA SET TERMINATOR")
        if (parsingState.instructions.none { it.operator == Operator.HALT }) parsingState =
            parsingState.addError("MISSING HALT INSTRUCTION")
        return parsingState
    }

    private fun extractNonBlankAndCommentLines(sourceCode: String): List<String> = sourceCode.lines()
        .map { line -> line.trim() }
        .filterNot { line -> line.startsWith("(") || line.isBlank() }

    private fun parseLine(parsingState: ParsingState, line: String): ParsingState {
        return when {
            line.startsWith("%") -> parsingState.terminateInstructionSet()
            line.startsWith("*") -> {
                parsingState.terminateDataSet()
            }
            else -> {
                val parsedLine =
                    when (parsingState.isInstructionSetTerminated) {
                        true -> parseDataLine(line)
                        false -> parseInstructionLine(line)
                    }
                when (parsedLine) {
                    is DataLine -> parsingState.addData(parsedLine.dataItems)
                    is InstructionLine -> {
                        val instruction = parsedLine.instruction
                        parsingState.addInstruction(instruction)
                    }
                    is ErrorLine -> parsingState.addError(parsedLine.error)
                }
            }
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
