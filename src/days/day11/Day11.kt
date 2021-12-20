package days.day11

import utils.Grid
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day11 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        var grid = Grid.from(items) { it.toInt() }

        var flashes = 0
        for (step in 0 until 1000) {
            var flashedInThisStep = mutableSetOf<Pair<Int, Int>>()
            for (i in 0 until grid.iSize) {
                for (j in 0 until grid.jSize) {
                    grid.at(i, j)!!.value = grid.valueAt(i, j, 0) + 1
                    if (grid.valueAt(i, j, 0) > 9 && !flashedInThisStep.contains(Pair(i, j))) {
                        flashedInThisStep.add(Pair(i, j))
                        flashNeighbors(grid, i, j, flashedInThisStep)
                    }
                }
            }
            flashedInThisStep.forEach { (i, j) ->
                grid.at(i, j)!!.value = 0
                flashes += 1
            }

            if (step == 99) {
                println(flashes)
            }

            if (flashedInThisStep.size == 100) {
                println (step + 1)
                break
            }
        }
    }

    private fun flashNeighbors(grid: Grid<Int>, i: Int, j: Int, flashedInThisStep: MutableSet<Pair<Int, Int>>) {
        val neighbors = grid.neighborsOf(i, j)
        neighbors.forEach {
            it.value += 1
            if (it.value > 9) {
                if (!flashedInThisStep.contains(Pair(it.i, it.j))) {
                    flashedInThisStep.add(Pair(it.i, it.j))
                    flashNeighbors(grid, it.i, it.j, flashedInThisStep)
                }
            }
        }
    }

}