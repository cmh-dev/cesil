package uk.me.cmh.cesil.interpreter.parser

import uk.me.cmh.cesil.interpreter.Instruction
import uk.me.cmh.cesil.interpreter.Operator

data class ParserState(
    val instructions: List<Instruction> = listOf(),
    val data: List<Int> = listOf(),
    val isInstructionSetTerminated: Boolean = false,
    val isDataSetTerminated: Boolean = false,
    val isHaltStatementPresent: Boolean = false,
    val errors: List<String> = listOf()
) {

    fun addInstruction(instruction: Instruction) =
        if (instruction.operator == Operator.HALT)
            this.copy(instructions = this.instructions + instruction, isHaltStatementPresent = true)
        else
            this.copy(instructions = this.instructions + instruction)

    fun addData(data: List<Int>) = this.copy(data = this.data + data)

    fun terminateInstructionSet() = this.copy(isInstructionSetTerminated = true)

    fun terminateDataSet() = this.copy(isDataSetTerminated = true)

    fun addError(error: String) = this.copy(errors = this.errors + error)

}