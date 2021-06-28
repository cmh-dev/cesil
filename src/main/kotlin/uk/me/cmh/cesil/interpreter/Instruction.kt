package uk.me.cmh.cesil.interpreter

data class Program(val instructions: List<Instruction>, val data: List<Int>)

data class Instruction(val label: String, val operator: Operator, val operand: String)

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
    JNEG,
    LINE,
    PRINT,
    HALT
}