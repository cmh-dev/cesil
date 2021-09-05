package uk.me.cmh.cesil.interpreter

class Program(instructions: List<Instruction>, data: List<Int>) {

    val instructions: List<Instruction> = instructions
    val labeledInstructionIndexes: Map<String, Int> = instructions
        .mapIndexed { index, instruction ->  instruction.label to index }
        .filterNot { it.first.isBlank() }
        .toMap()
    val data: List<Int> = data

}

