package days.day14

import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

class Day14 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        solve(input, 10)
        solve(input, 40)
    }

    fun solve(input: Path?, iterations: Int) {
        val items = Files.readAllLines(input).map { it.toString() }

        val startingTemplate = items.filter { !it.contains("->") }[0]
        val map = items.filter { it.contains("->") }.associate {
            val parts = it.split("->")
            parts[0].trim() to parts[1].trim()
        }

        var counts = startingTemplate.windowed(2)
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toBigDecimal() }
            .toMutableMap()

        for (i in 0 until iterations) {
            val currentCounts = mutableMapOf<String, BigDecimal>()
            // apparently all pairs have a match, because this works
            counts.forEach { (pair, count) ->
                val insertion = map[pair]
                currentCounts[pair[0] + insertion!!] = currentCounts.getOrDefault(pair[0] + insertion, BigDecimal.ZERO).plus(count)
                currentCounts[insertion + pair[1]] = currentCounts.getOrDefault(insertion + pair[1], BigDecimal.ZERO).plus(count)
            }
            counts = currentCounts
        }

        val elementCounts = mutableMapOf<String, BigDecimal>()

        counts.forEach { (pair, count) ->
            val firstLetter = pair[0].toString()
            elementCounts[firstLetter] = elementCounts.getOrDefault(firstLetter, BigDecimal.ZERO).plus(count)
        }

        // last letter is missing so add one
        val lastLetter = startingTemplate.last().toString()
        elementCounts[lastLetter] = elementCounts[lastLetter]!!.plus(1.toBigDecimal())

        println (elementCounts.values.maxOrNull()!!.minus(elementCounts.values.minOrNull()!!))
    }

}