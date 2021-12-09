package days.day9

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day9 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val lowPointIndexes = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until items.size) {
            for (j in 0 until items[0].length) {
                val possibleIndexes = getPossibleNeighborIndexes(i, j)

                val smallest = possibleIndexes.filter { isWithin(items, it) }.minOf { pair ->
                    items[pair.first][pair.second].toString()
                }.toInt()

                if (items[i].intAt(j) < smallest) {
                    lowPointIndexes.add(Pair(i, j))
                }
            }
        }

        println(lowPointIndexes.map { items[it.first].intAt(it.second) }.sumOf { it + 1 })

        val basins = lowPointIndexes.map {
                var pointsInBasin = mutableSetOf<Pair<Int, Int>>()
                searchForBasin(pointsInBasin, it.first, it.second, items)
                pointsInBasin
            }.sortedByDescending { it.size }

        println(basins[0].size * basins[1].size * basins[2].size)
    }

    private fun searchForBasin(pointsInBasin: MutableSet<Pair<Int, Int>>, i: Int, j: Int, items: List<String>) {
        pointsInBasin.add(Pair(i, j))
        val possibleIndexes = getPossibleNeighborIndexes(i, j)

        possibleIndexes
            .filter { isWithin(items, it) }
            .filter { items[it.first].intAt(it.second) > items[i].intAt(j) && items[it.first].intAt(it.second) < 9 }
            .forEach { searchForBasin(pointsInBasin, it.first, it.second, items) }
    }

    private fun String.intAt(index: Int) = this[index].toString().toInt()

    private fun isWithin(items: List<String>, point: Pair<Int, Int>) = point.first >= 0 && point.first < items.size
            && point.second >= 0 && point.second < items[0].length

    private fun getPossibleNeighborIndexes(i: Int, j: Int) =
        listOf(Pair(i, j - 1), Pair(i, j + 1), Pair(i + 1, j), Pair(i - 1, j))

}