package days.day9

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day9 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        var lowPoints = mutableListOf<String>()
        val lowPointIndexes = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until items.size) {
            for (j in 0 until items[0].length) {

                val possibleIndexes = listOf(
                    Pair(i, j - 1),
                    Pair(i, j + 1),
                    Pair(i + 1, j),
                    Pair (i - 1, j)
                )

                var neighbors = mutableListOf<String>()
                possibleIndexes.forEach { pair ->
                    val (first, second) = pair
                    if (first >= 0 && first < items.size &&  second >= 0 && second <items[0].length) {
                        neighbors.add(items[pair.first][pair.second].toString())
                    }
                }
                val smallest = neighbors.minOrNull()!!.toInt()
                if (items[i][j].toString().toInt() < smallest) {
                    lowPoints.add(items[i][j].toString())
                    lowPointIndexes.add(Pair(i, j))
                }
            }
        }

        val basins =
        lowPointIndexes.map {
            var pointsInBasin = mutableSetOf<Pair<Int, Int>>()
            searchForBasin(pointsInBasin, it.first, it.second, items, 0)
            pointsInBasin
        }

        val sortedBySize = basins.sortedByDescending { it.size }
        println(sortedBySize[0].size * sortedBySize[1].size * sortedBySize[2].size)
    }

    private fun searchForBasin(pointsInBasin: MutableSet<Pair<Int, Int>>, i: Int, j: Int, items: List<String>, depth: Int) {
        pointsInBasin.add(Pair(i, j))
        val possibleIndexes = listOf(
            Pair(i, j - 1),
            Pair(i, j + 1),
            Pair(i + 1, j),
            Pair (i - 1, j)
        )

        var neighbors = mutableListOf<Pair<Int, Int>>()
        possibleIndexes.forEach { pair ->
            val (first, second) = pair
            if (first >= 0 && first < items.size &&  second >= 0 && second <items[0].length) {
                if (items[first][second].toString().toInt() > items[i][j].toString().toInt() && items[first][second].toString().toInt() < 9) {
                    neighbors.add(pair)
                }
            }
        }
        neighbors.forEach {
            searchForBasin(pointsInBasin, it.first, it.second, items, depth + 1)
        }
    }

}