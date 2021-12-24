package days.day20

import binaryToInt
import utils.Grid
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Day20 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }
        val mask = items[0]

        var lines = items.drop(2).toMutableList()

        // this takes an embarassingly long time but it gets there eventually
        for (i in 0..51) {
            if (i == 2 || i == 50) {
                println(lines.flatMap { it.toCharArray().toList() }.filter { it.toString() == "#" }.size)
            }
            val outsideDefault = if (i % 2 == 1) "#" else "."
            val grid = Grid.from(lines) { it }
            lines = mutableListOf()
            for (i in -2 until grid.iSize + 2) {
                var line = ""
                for (j in -2 until grid.jSize + 2) {
                    val minigrid = grid.get3By3Square(i, j, outsideDefault)
                    val asBinary = minigrid.joinToString("") { it.toBit() }
                    val index = asBinary.binaryToInt()
                    val maskResult = mask[index]
                    line += maskResult
                }
                lines.add(line)
            }
        }
    }

    private fun String.toBit(): String = if (this == ".") {
        "0"
    } else {
        "1"
    }
}