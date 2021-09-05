package uk.me.cmh.cesil.interpreter.parser

import uk.me.cmh.cesil.interpreter.*
import java.lang.NumberFormatException

class Parser {

    fun parse(sourceCode: String): ParsingResult {

        val instructions = mutableListOf<Instruction>()
        val labeledInstructionIndexes = mutableMapOf<String, Int>()
        val data = mutableListOf<Int>()
        var isInstructionSetTerminated = false
        val parsingErrors = mutableListOf<String>()
        var isDataTerminated = false

        if (sourceCode.isBlank()) return ParsingErrors(listOf("NO SOURCE CODE"))

        sourceCode.lines()
            .map { line -> line.trim() }
            .filterNot { line -> line.startsWith("(") || line.isBlank() }
            .forEach { line ->
                when {
                    line.startsWith("%") -> isInstructionSetTerminated = true
                    line.startsWith("*") -> {
                        isDataTerminated = true
                        return@forEach
                    }
                    else -> {
                        val parsedLine =
                            when(isInstructionSetTerminated) {
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
                            is ErrorLine -> parsingErrors.add(parsedLine.error)
                        }
                    }
                }
            }

        if (!isInstructionSetTerminated) parsingErrors.add("MISSING INSTRUCTION SET TERMINATOR")
        if (!isDataTerminated) parsingErrors.add("MISSING DATA SET TERMINATOR")
        if (instructions.none { it.operator == Operator.HALT }) parsingErrors.add("MISSING HALT INSTRUCTION")

        return when {
            parsingErrors.isEmpty() -> ParsedProgram(Program(instructions, data))
            else -> ParsingErrors(parsingErrors)
        }

    }

   /* private fun addParsedLineToProgram(program: Program, parsedLine: ParsedLine): Program {
       if (parsedLine is InstructionLine)
            program.addInstruction(parsedLine.instruction)
        else if (parsedLine is DataLine)
            program.addData(parsedLine.dataItems)
        else  Program()
    }
*/
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
