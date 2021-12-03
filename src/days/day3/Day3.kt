package days.day3

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day3 {
    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        solvePartOne(items)
        solvePartTwo(items)

    }

    private fun solvePartTwo(items: List<String>) {
        val numberOfBits = items[0].length

        var oxygenGeneratorRating = ""
        var co2ScrubberRating = ""

        var oxygenItems = items.toList()
        var co2Items = items.toList()

        for (i in 0 until numberOfBits) {
            val bitsAtThisPosition = oxygenItems.map { it[i] }

            val numberOfOnes = bitsAtThisPosition.filter { it == '1' }.size
            val numberOfZeros = bitsAtThisPosition.filter { it == '0' }.size

            if (numberOfOnes >= numberOfZeros) {
                oxygenItems = oxygenItems.filter { it[i] == '1' }
            } else {
                oxygenItems = oxygenItems.filter { it[i] == '0' }
            }

            if (oxygenItems.size == 1) {
                break
            }
        }

        for (i in 0 until numberOfBits) {
            val bitsAtThisPosition = co2Items.map { it[i] }

            val numberOfOnes = bitsAtThisPosition.filter { it == '1' }.size
            val numberOfZeros = bitsAtThisPosition.filter { it == '0' }.size

            if (numberOfZeros <= numberOfOnes) {
                co2Items = co2Items.filter { it[i] == '0' }
            } else {
                co2Items = co2Items.filter { it[i] == '1' }
            }

            if (co2Items.size == 1) {
                break
            }
        }

        println (oxygenItems)
        println(co2Items)

        val oxygenInt = Integer.parseInt(oxygenItems[0], 2);
        val co2Int = Integer.parseInt(co2Items[0], 2);

        println (oxygenInt * co2Int)

    }

    private fun solvePartOne(items: List<String>) {
        val numberOfBits = items[0].length

        var gammaRate = "";
        var epsilonRate = "";

        for (i in 0 until numberOfBits) {
            val bitsAtThisPosition = items.map { it[i] }

            val numberOfOnes = bitsAtThisPosition.filter { it == '1' }.size
            val numberOfZeros = bitsAtThisPosition.filter { it == '0' }.size

            if (numberOfZeros > numberOfOnes) {
                epsilonRate += "1"
                gammaRate += "0"
            } else {
                epsilonRate += "0"
                gammaRate += "1"
            }
        }

        val gammaRateInt = Integer.parseInt(gammaRate, 2);
        val epsilonRateInt = Integer.parseInt(epsilonRate, 2);

        println (gammaRateInt * epsilonRateInt)
    }


}