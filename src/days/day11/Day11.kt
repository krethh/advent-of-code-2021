package days.day11

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day11 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        var currentGrid = items.map {
            it.map { it.toString().toInt() }.toMutableList()
        }.toMutableList()

        var flashes = 0
        for (step in 0 until 100) {
            var flashedInThisStep = mutableSetOf<Pair<Int, Int>>()
            var nextGrid = currentGrid.toMutableList()
            for (i in 0 until 10) {
                for (j in 0 until 10) {
                    nextGrid[i][j] = currentGrid[i][j] + 1

                    if (nextGrid[i][j] > 9 && !flashedInThisStep.contains(Pair(i, j))) {
                        flashedInThisStep.add(Pair(i, j))
                        flashNeighbors(nextGrid, i, j, flashedInThisStep)
                    }
                }
            }
            flashedInThisStep.forEach {
                nextGrid[it.first][it.second] = 0
                flashes += 1
            }

            if (flashedInThisStep.size == 100) {
                println (step)
                break
            }
//            println (flashedInThisStep.size)
            currentGrid = nextGrid.toMutableList()
        }
        println (flashes)
    }

    private fun flashNeighbors(grid: MutableList<MutableList<Int>>, i: Int, j: Int, flashedInThisStep: MutableSet<Pair<Int, Int>>) {
        val possibleIndexes = getPossibleNeighborIndexes(i, j).filter { it.first in 0..9 && it.second in 0..9 }
        possibleIndexes.forEach {
            grid[it.first][it.second] += 1
            if (grid[it.first][it.second] > 9) {
                if (!flashedInThisStep.contains(Pair(it.first, it.second))) {
                    flashedInThisStep.add(Pair(it.first, it.second))
                    flashNeighbors(grid, it.first, it.second, flashedInThisStep)
                }
            }
        }
    }

    private fun getPossibleNeighborIndexes(i: Int, j: Int) =
        listOf(Pair(i, j - 1), Pair(i, j + 1), Pair(i + 1, j), Pair(i - 1, j),
                Pair(i + 1, j + 1), Pair(i - 1, j - 1), Pair(i + 1, j - 1), Pair(i - 1, j + 1))

}