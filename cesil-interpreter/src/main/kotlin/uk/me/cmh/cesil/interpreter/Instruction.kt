package uk.me.cmh.cesil.interpreter

data class Instruction(val label: String = "", val operator: Operator, val operand: String = "")