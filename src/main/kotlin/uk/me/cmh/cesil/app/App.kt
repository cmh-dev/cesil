package uk.me.cmh.cesil.app

import uk.me.cmh.cesil.interpreter.Interpreter
import java.io.File

fun main(args: Array<String>) {
    val sourceCode = File(args[0]).readText()
    val executionResult = Interpreter().executeProgram(sourceCode)
    executionResult.output.forEach { println(it) }
}