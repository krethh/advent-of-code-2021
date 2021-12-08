package days.day8

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day8 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val inputUnits = items.map {
            val parts = it.split("|")
            val inputs = parts[0].split("\\s+".toRegex())
            val outputs = parts[1].split("\\s+".toRegex())

            val allowedChars = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g')
            val outputsSorted = outputs.map { it.toCharArray().sorted().filter { allowedChars.contains(it) }.joinToString("") }.filter { it.isNotBlank() }
            val inputsSorted = inputs.map { it.toCharArray().sorted().filter { allowedChars.contains(it) }.joinToString("") }.filter { it.isNotBlank() }

            InputUnit(inputsSorted, outputsSorted)
        }

        val uniqueLengths = listOf(2, 3, 4, 7)

        println(inputUnits.flatMap { it.outputs }.filter { uniqueLengths.contains(it.length) }.size)

        val sum = inputUnits.map {
            val inputs = it.inputs
            val seven = inputs.firstOrNull { it.length == 3 }!!
            val four = inputs.firstOrNull { it.length == 4 }!!
            val one = inputs.firstOrNull { it.length == 2 }!!
            val eight = inputs.firstOrNull { it.length == 7 }!!
            val six = inputs.filter { it.length == 6 }.firstOrNull { it.sizeOfIntersectionWith(one) != one.length }!!
            val nine = inputs.filter { it.length == 6 }.firstOrNull { it.sizeOfIntersectionWith(four) == four.length}!!
            val zero = inputs.firstOrNull { it.length == 6 && it != six && it != nine }
            val five = inputs.filter { it.length == 5 }.firstOrNull { it.sizeOfIntersectionWith(nine) == 5 && it.sizeOfIntersectionWith(one) == 1}!!
            val three = inputs.filter { it.length == 5 }.firstOrNull { it.sizeOfIntersectionWith(seven) == 3 }!!
            val two = inputs.firstOrNull { it.length == 5 && it != five && it != three }

            val map = mapOf(
                zero to 0,
                one to 1,
                two to 2,
                three to 3,
                four to 4,
                five to 5,
                six to 6,
                seven to 7,
                eight to 8,
                nine to 9
            )

            val output = "${map[it.outputs[0]]}${map[it.outputs[1]]}${map[it.outputs[2]]}${map[it.outputs[3]]}"
            output.toInt()
        }.sum()
        println(sum)
    }

    fun String.sizeOfIntersectionWith(another: String) = this.toCharArray().intersect(another.toCharArray().toSet()).size
}