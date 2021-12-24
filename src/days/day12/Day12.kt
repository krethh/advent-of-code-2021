package days.day12

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day12 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val adjacencyMatrix: MutableList<Pair<String, String>> = mutableListOf()

        items.forEach {
            val parts = it.split("-")
            adjacencyMatrix.add(Pair(parts[0], parts[1]))
        }

        solvePart(adjacencyMatrix, partTwo = false)
        solvePart(adjacencyMatrix, partTwo = true)
    }

    private fun solvePart(adjacencyMatrix: MutableList<Pair<String, String>>, partTwo: Boolean) {
        val paths = mutableListOf<List<String>>()
        val currentPath = listOf<String>()
        countPaths("start", "end", paths, adjacencyMatrix, currentPath, partTwo)
        println(paths.size)
    }


    private fun countPaths(start: String, end:String, paths: MutableList<List<String>>,
                           matrix: MutableList<Pair<String, String>>,
                           currentPath: List<String>, partTwo: Boolean) {
        val thisPath = currentPath.plus(start)
        if (start == end) {
            paths.add(thisPath)
            return
        }
        val neighbors = matrix.filter { it.first == start }
            .map { it.second }
            .plus(matrix.filter { it.second == start }.map { it.first })
        neighbors.filter { it != "start" }.forEach {
            if (it.isSmallCave()) {
                if (!partTwo || containsAnySmallCaveMoreThanOnce(thisPath)) {
                    if (!thisPath.contains(it)) {
                        countPaths(it, end, paths, matrix, thisPath, partTwo)
                    }
                }
                else {
                    countPaths(it, end, paths, matrix, thisPath, partTwo)
                }
            } else {
                countPaths(it, end, paths, matrix, thisPath, partTwo)
            }
        }

    }

    private fun containsAnySmallCaveMoreThanOnce(path: List<String>) = path.filter { it.isSmallCave() }.groupingBy { it }.eachCount().values.any { it > 1 }

    private fun String.isSmallCave() = this.lowercase() == this && this != "start" && this != "end"

}