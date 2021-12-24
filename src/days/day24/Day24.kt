package days.day24

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day24 {

    @Throws(IOException::class)
    fun solve(input: Path?) {
        val operations = Files.readAllLines(input).map { it.toString() }.chunked(18).map { it.toOperation() }

        findModelNumbers(0,  operations, mutableMapOf()).let {
            println(it.maxOrNull())
            println(it.minOrNull())
        }
    }

    // some sort of depth first search
    private fun findModelNumbers(startZ: Long, operations: List<Operation>, cache: MutableMap<Pair<Long, Int>, List<String>>): List<String> {
        val searchKey = Pair(startZ, operations.size)
        if (!cache.containsKey(searchKey)) {
            if (operations.isEmpty()) {
                return if (startZ == 0L) listOf("") else listOf()
            }
            cache[searchKey] = LongRange(1, 9).flatMap {
                val z = operations.first().performOperation(startZ, it)
                findModelNumbers(z, operations.drop(1), cache).map { part -> "$it$part" }
            }
        }

        return cache[searchKey]!!
    }

    private fun String.getOperand() = this.split("\\s+".toRegex())[2].toLong()

    private fun List<String>.toOperation(): Operation = Operation(listOf(4, 5, 15).map { this[it].getOperand() })

    // this is effectively what the 18-instruction operation does
    private fun Operation.performOperation(z: Long, number: Long): Long = if (operands[1] + z % 26 != number) {
        (z / operands[0]) * 26 + number + this.operands[2]
    } else {
        z / operands[0]
    }

    data class Operation(val operands: List<Long>)
}