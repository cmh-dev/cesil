package cmh.cesil.interpreter

import java.lang.IllegalArgumentException

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