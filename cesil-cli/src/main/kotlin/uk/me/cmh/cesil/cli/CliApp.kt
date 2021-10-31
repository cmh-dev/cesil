package uk.me.cmh.cesil.cli

import uk.me.cmh.cesil.interpreter.Interpreter
import java.io.File

fun main(args: Array<String>) {
    val sourceCodeFile = File(args[0])
    val interpreter = Interpreter()
    val executionResult = interpreter.executeProgram(sourceCodeFile.readText())
    executionResult.output.forEach { println(it) }
}