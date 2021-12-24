package days.day6

import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class Day6 {
    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val lines = Files.readAllLines(input)
        val numbers = lines[0].split(",").map { it.toBigDecimal() }

        solvePartOne(numbers.map { it.toInt() })
        solvePartTwo(numbers)
    }

    private fun solvePartOne(numbers: List<Int>) {
        var current = numbers.toList()
        for (i in 0 until 80) {
            val next = mutableListOf<Int>()
            current.forEach {
                if (it == 0) {
                    next.add(6)
                    next.add(8)
                } else {
                    next.add(it -1)
                }
            }
            current = next
        }
        println(current.size)
    }

    private fun solvePartTwo(numbers: List<BigDecimal>) {
        var fishAtDayX = mutableMapOf<Int, BigDecimal>()

        for (i in 0..8) {
            fishAtDayX[i] = BigDecimal.ZERO
        }

        numbers.forEach {
            fishAtDayX[it.toInt()] = fishAtDayX[it.toInt()]!!.plus(BigDecimal.ONE)
        }

        for (i in 0 until 256) {
            val map = mutableMapOf<Int, BigDecimal>()

            for (i in 0..8) {
                map[i] = BigDecimal.ZERO
            }
            fishAtDayX.forEach { entry ->
                if (entry.key > 0) {
                    map[entry.key - 1] = map[entry.key - 1]!!.plus(entry.value)
                } else {
                    map[6] = map[6]!!.plus(entry.value)
                    map[8] = map[8]!!.plus(entry.value)
                }
            }
            fishAtDayX = map
        }
        println (fishAtDayX.values.reduce(BigDecimal::add))
    }
}