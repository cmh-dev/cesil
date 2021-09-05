package uk.me.cmh.cesil.interpreter

import java.lang.IllegalArgumentException

data class Program(
    val instructions: List<Instruction>,
    val labeledInstructionIndexes: Map<String, Int>,
    val data: List<Int>
) {

    fun addInstruction(instruction: Instruction): Program {
        val newInstructions = this.instructions + instruction
        return this.copy(
            instructions = this.instructions + instruction,
            labeledInstructionIndexes = if (instruction.label.isBlank())
                labeledInstructionIndexes
            else
                labeledInstructionIndexes + (instruction.label to newInstructions.lastIndex)
        )
    }

    fun addData(data: List<Int>): Program = this.copy(data = this.data + data)

}

data class Instruction(val label: String = "", val operator: Operator, val operand: String = "")

enum class Operator {

    IN,
    OUT,
    LOAD,
    STORE,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    JUMP,
    JIZERO,
    JINEG,
    LINE,
    PRINT,
    HALT,
    INVALID_OPERATOR;

    companion object {
        fun findOperator(value: String): Operator = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            INVALID_OPERATOR
        }
    }

}
