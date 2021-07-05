package uk.me.cmh.cesil.interpreter

import java.lang.IllegalArgumentException

data class Program(
    val instructions: List<Instruction>,
    val labeledInstructionIndexes: Map<String, Int>,
    val data: List<Int>
)

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
            Operator.valueOf(value)
        } catch (e: IllegalArgumentException) {
            INVALID_OPERATOR
        }
    }

}
