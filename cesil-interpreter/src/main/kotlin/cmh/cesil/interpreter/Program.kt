package cmh.cesil.interpreter

class Program(val instructions: List<Instruction>, val data: List<Int>) {

    val labeledInstructionIndexes: Map<String, Int> = instructions
        .mapIndexed { index, instruction ->  instruction.label to index }
        .filterNot { it.first.isBlank() }
        .toMap()

}

