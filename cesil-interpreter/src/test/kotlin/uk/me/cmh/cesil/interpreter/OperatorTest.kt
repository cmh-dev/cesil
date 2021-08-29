package uk.me.cmh.cesil.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OperatorTest {

    @Test
    fun `when a valid operator is searched for it is returned`() {
        val operator = Operator.findOperator("PRINT")
        assertEquals(Operator.PRINT, operator)
    }

    @Test
    fun `when an invalid operator is searched for the invalid operator is returned`() {
        val operator = Operator.findOperator("RUBBISH")
        assertEquals(Operator.INVALID_OPERATOR, operator)
    }

}