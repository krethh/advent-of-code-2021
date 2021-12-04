package days.day4

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day4 {
    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val boards = solvePartOne(items)
        solvePartTwo(boards)

    }

    private fun solvePartOne(items: List<String>): List<Board> {
        val firstLine = items[0]
        var calledNumbers = firstLine.split(",").map { it.toInt() }
        var boards = mutableListOf<Board>()

        var index = 2
        while (index < items.size) {
            val currentBoard = mutableListOf<List<Int>>()
            for (i in index..index + 4) {
                val line = items[i].split("\\s+".toRegex()).filter { it.isNotBlank() }.map { it.toInt() }
                currentBoard.add(line)
            }
            index += 6
            boards.add(Board(numbers = currentBoard, calledNumbers))
        }

        for (i in 5..100) {
            var found = false
            boards.forEach {
                val score = it.isWinning(i)
                if (score > 0) {
                    println(score)
                    found = true
                }
            }
            if (found) {
                break
            }
        }

       return boards
    }

    private fun solvePartTwo(startingBoards: List<Board>) {
        var boards = startingBoards.toMutableList()
        var lastBoard: Board? = null
        for (i in 5..100) {
            boards = boards.filter { it.isWinning(i) == 0 }.toMutableList()

            if (boards.size == 1) {
                lastBoard = boards[0]
                break
            }
        }
        for (i in 5..100) {
            if (lastBoard!!.isWinning(i) > 0) {
                println(lastBoard.isWinning(i))
                break
            }
        }
    }


}