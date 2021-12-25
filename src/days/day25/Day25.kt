package days.day25

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day25 {

    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }.toMutableList()
        val (iSize, jSize) = items.gridSize()

        var step = 1
        while (true) {
            val eastFacing = items.indexesOf { it.isEastFacing() }
            val southFacing = items.indexesOf { it.isSouthFacing() }

            val movesEast = eastFacing.map {
                val nextJ = if (it.second == jSize) 0 else it.second + 1
                val nextI = it.first

                if (items.elementAt(nextI, nextJ).isEmptySpace()) {
                    Pair(Pair(it.first, it.second), Pair(nextI, nextJ))
                } else {
                    Pair(Pair(-1, -1), Pair(-1, -1))
                }
            }.filter { it.first.first != -1 }

            movesEast.forEach {
                items[it.first.first] = items[it.first.first].replaceAt(".", it.first.second)
                items[it.second.first] = items[it.second.first].replaceAt(">", it.second.second)
            }

            val movesSouth = southFacing.map {
                val nextI = if (it.first == iSize) 0 else it.first + 1
                val nextJ = it.second

                if (items.elementAt(nextI, nextJ).isEmptySpace()) {
                    Pair(Pair(it.first, it.second), Pair(nextI, nextJ))
                } else {
                    Pair(Pair(-1, -1), Pair(-1, -1))
                }
            }.filter { it.first.first != -1 }

            movesSouth.forEach {
                items[it.first.first] = items[it.first.first].replaceAt(".", it.first.second)
                items[it.second.first] = items[it.second.first].replaceAt("v", it.second.second)
            }

            if ((movesEast.isEmpty() && movesSouth.isEmpty())) {
                break
            }
            step++
        }
        println(step)
    }

    private fun String.replaceAt(value: String, index: Int): String {
        val charArray = this.toCharArray()
        charArray[index] = value.single()
        return charArray.concatToString()
    }
    private fun List<String>.elementAt(i: Int, j: Int) = this[i][j].toString()
    private fun List<String>.gridSize() = Pair(this.indices.last, this[0].indices.last)
    private fun List<String>.indexesOf(predicate: (element: String) -> Boolean): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (i in this.indices) {
            for (j in this[i].indices) {
                if (predicate(this[i][j].toString())) {
                    result.add(Pair(i, j))
                }
            }
        }
        return result
    }

    private fun String.isEastFacing() = this == ">"
    private fun String.isSouthFacing() = this == "v"
    private fun String.isEmptySpace() = this == "."
}