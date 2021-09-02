package uk.me.cmh.cesil.interpreter

import uk.me.cmh.cesil.interpreter.executor.ExecutionFailure
import uk.me.cmh.cesil.interpreter.executor.ExecutionResult
import uk.me.cmh.cesil.interpreter.executor.ExecutionSuccess
import uk.me.cmh.cesil.interpreter.executor.Executor
import uk.me.cmh.cesil.interpreter.parser.Parser

class Interpreter(private val parser: Parser = Parser(), private val executor: Executor = Executor()) {

    fun executeProgram(program: String): ExecutionResult = when (val parserResult = parser.parse(program)) {
        is ParsingErrors -> ExecutionFailure(parserResult.errorMessages.formatErrorMessages())
        is ParsedProgram ->
            when (val executionResult = executor.execute(parserResult.program)) {
                is ExecutionFailure -> ExecutionFailure(executionResult.output.formatErrorMessages())
                is ExecutionSuccess -> executionResult
            }
    }

    private fun List<String>.formatErrorMessages() = this.map { "*** $it ***" }

}

