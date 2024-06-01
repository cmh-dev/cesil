package cmh.cesil.interpreter.parser

import cmh.cesil.interpreter.Instruction
import cmh.cesil.interpreter.Operator
import cmh.cesil.interpreter.Program
import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParsingResult {
        if (sourceCode.isBlank()) return ParsingErrors(listOf("NO SOURCE CODE"))
        val parsingState = parseSourceCode(sourceCode)
        return when {
            parsingState.errors.isNotEmpty() -> ParsingErrors(parsingState.errors)
            else -> ParsedProgram(Program(parsingState.instructions, parsingState.data))
        }
    }

    private fun parseSourceCode(sourceCode: String): ParsingState {
        var parsingState = ParsingState()
        val validLines = extractNonBlankAndCommentLines(sourceCode)
        validLines.forEach { line ->
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
            line.startsWith("*") -> parsingState.terminateDataSet()
            else -> parseInstructionOrDataLine(parsingState, line)
        }
    }

    private fun parseInstructionOrDataLine(parsingState: ParsingState, line: String): ParsingState {
        val parsedLine =
            when (parsingState.isInstructionSetTerminated) {
                true -> parseDataLine(line)
                false -> parseInstructionLine(line)
            }
        return when (parsedLine) {
            is DataLine -> parsingState.addData(parsedLine.dataItems)
            is InstructionLine -> parsingState.addInstruction(parsedLine.instruction)
            is ErrorLine -> parsingState.addError(parsedLine.error)
        }
    }

    internal fun parseDataLine(line: String): ParsedLine = try {
        DataLine(line.split(Regex("\\s")).filterNot { it == "" }.map { it.toInt() })
    } catch (e: NumberFormatException) {
        ErrorLine("DATA LINE INVALID [$line]")
    }

    internal fun parseInstructionLine(line: String): ParsedLine {
        val elements = line.split(Regex("\\s")).filterNot { it == "" }
        val operator = extractOperator(elements)
        if (operator.operator == Operator.INVALID_OPERATOR) return ErrorLine("INSTRUCTION LINE INVALID [$line]")
        val label = when (operator.index) {
            0 -> ""
            else -> elements[0]
        }
        var operand = elements.filterIndexed { index, _ -> index > operator.index }.joinToString(separator = " ")
        if (operator.operator == Operator.PRINT) {
            if (operand.matches(Regex("\".*\""))) {
                operand = operand.removeSurrounding("\"")
            } else {
                return ErrorLine("INSTRUCTION LINE OPERAND INVALID [$line]")
            }
        }
        return InstructionLine(Instruction(label, operator.operator, operand))
    }

    private fun extractOperator(elements: List<String>): IndexedOperator {
        val candidateOperator = Operator.findOperator(elements[0])
        val operator = when {
            candidateOperator == Operator.INVALID_OPERATOR && elements.size > 1 -> IndexedOperator(
                Operator.findOperator(elements[1]),
                1
            )
            else -> IndexedOperator(candidateOperator, 0)
        }
        return operator
    }

    private data class IndexedOperator(val operator: Operator, val index: Int)

    sealed class ParsedLine
    class InstructionLine(val instruction: Instruction) : ParsedLine()
    class ErrorLine(val error: String) : ParsedLine()
    class DataLine(val dataItems: List<Int>) : ParsedLine()

}
