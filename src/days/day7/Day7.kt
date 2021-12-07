package days.day7

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day7 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input)[0].split(",").map { it.toInt() }

        solve(items, partOne = true)
        solve(items, false)
    }

    private fun solve(items: List<Int>, partOne: Boolean) {
        var minSum = 0
        for (item in 0..items.maxOrNull()!!) {
            var sum = 0
            items.forEach { sum += calculateDistance(item, it, partOne) }
            if (minSum == 0) {
                minSum = sum
            }
            if (sum < minSum) {
                minSum = sum
            }
        }

        println(minSum)
    }

    private fun calculateDistance(item: Int, target: Int, partOne: Boolean): Int {
        if (partOne) {
            return Math.abs(item - target)
        }

        val distance = Math.abs(item - target)
        var cost = 0
        for (i in 0..distance) {
            cost += i
        }
        return cost
    }


}