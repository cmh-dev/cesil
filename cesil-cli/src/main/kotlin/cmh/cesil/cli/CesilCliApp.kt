package cmh.cesil.cli

import cmh.cesil.interpreter.Interpreter
import java.io.File

fun main(args: Array<String>) =
    if (args.isEmpty()) println("*** MISSING SOURCE CODE FILE ARGUMENT ***") else processSourceCodeFile(args[0])

private fun processSourceCodeFile(sourceCodeFilePath: String) {
    val sourceCodeFile = File(sourceCodeFilePath)
    when {
        sourceCodeFile.exists() -> executeProgram(sourceCodeFile)
        else -> println("*** SOURCE CODE FILE NOT FOUND ***")
    }
}

private fun executeProgram(sourceCodeFile: File) {
    val interpreter = Interpreter()
    val executionResult = interpreter.executeProgram(sourceCodeFile.readText())
    executionResult.output.forEach { println(it) }
}