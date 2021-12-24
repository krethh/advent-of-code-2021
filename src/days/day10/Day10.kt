package days.day10

import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Day10 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val matches = mapOf(")" to "(", "]" to "[", "}" to "{", ">" to "<")
        val incompleteScores = mapOf("(" to 1.toBigDecimal(), "[" to 2.toBigDecimal(), "{" to 3.toBigDecimal(), "<" to 4.toBigDecimal())
        val corruptedPoints = mapOf(")" to 3, "]" to 57, "}" to 1197, ">" to 25137)

        val corruptedChars = mutableListOf<String>()
        val scores = mutableListOf<BigDecimal>()
        items.forEach { line ->
            val stack = Stack<String>()

            val corruptedChar = line.let {
                it.map { it.toString() }.forEach { current ->
                    if (current in matches.values) {
                        stack.add(current)
                    } else {
                        if (stack.peek() == matches[current]!!) {
                            stack.pop()
                        } else {
                            return@let current
                        }
                    }
                }
                return@let null
            }

            if (corruptedChar != null) {
                corruptedChars.add(corruptedChar)
            }
            else {
                val score = stack.reversed().map { incompleteScores[it]!! }.reduce { acc, score -> acc.times(5.toBigDecimal()).plus(score) }
                scores.add(score)
            }
        }

        println (corruptedChars.sumOf { corruptedPoints[it]!! })
        println (scores.sorted()[scores.size / 2])
    }
}