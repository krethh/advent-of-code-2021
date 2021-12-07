package days.day7

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day7 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input)[0].split(",").map { it.toInt() }

        println(solve(items, partOne = true))
        println(solve(items, partOne = false))
    }

    private fun solve(items: List<Int>, partOne: Boolean): Int {
        return IntRange(0, items.maxOrNull()!!).minOfOrNull { target ->
            items.sumOf { innerItem -> calculateDistance(innerItem, target, partOne) }
        }!!
    }

    private fun calculateDistance(item: Int, target: Int, partOne: Boolean): Int {
        val distance = Math.abs(item - target)
        return if (partOne) distance else IntRange(0, distance).sum()
    }

}