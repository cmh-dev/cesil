package uk.me.cmh.cesil.cli

import uk.me.cmh.cesil.interpreter.Interpreter
import java.io.File

fun main(args: Array<String>) {
    if (args.size == 0) {
        println("*** MISSING SOURCE CODE FILE ARGUMENT ***")
    } else {
        val sourceCodeFile = File(args[0])
        if (sourceCodeFile.exists()) {
            val interpreter = Interpreter()
            val executionResult = interpreter.executeProgram(sourceCodeFile.readText())
            executionResult.output.forEach { println(it) }
        } else {
            println("*** SOURCE CODE FILE NOT FOUND ***")
        }
    }
}