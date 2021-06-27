package uk.me.cmh.cesil

sealed class ExecutionResult(val output: List<String>)
class ExecutionFailure(output: List<String>) : ExecutionResult(output)
class ExecutionSuccess(output: List<String>) : ExecutionResult(output)