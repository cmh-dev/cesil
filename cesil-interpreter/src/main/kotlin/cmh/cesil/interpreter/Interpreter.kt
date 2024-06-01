package cmh.cesil.interpreter

import cmh.cesil.interpreter.executor.ExecutionFailure
import cmh.cesil.interpreter.executor.ExecutionResult
import cmh.cesil.interpreter.executor.ExecutionSuccess
import cmh.cesil.interpreter.executor.Executor
import cmh.cesil.interpreter.parser.ParsedProgram
import cmh.cesil.interpreter.parser.Parser
import cmh.cesil.interpreter.parser.ParsingErrors

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