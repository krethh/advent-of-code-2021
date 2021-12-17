package days.day17

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

object Day17 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }[0]
        val numbers = "-?[1-9]\\d*|0".toRegex().findAll(items)
        val targetArea = numbers.toList().map { it.value.toInt() }

        var succesfulThrows = 0
        var maxHeight = 0
        for (x in IntRange(0, 500)) {
            for (y in IntRange(-500, 500)) {
                var maxHeightThisThrow = 0
                val startingPosition = Position(0, 0)
                val startingVelocity = Velocity(x, y)
                var state = State(startingPosition, startingVelocity, startingVelocity)

                while (true) {
                    state = state.move()
                    maxHeightThisThrow = max(maxHeightThisThrow, state.position.y)
                    if (state.isWithin(targetArea)) {
                        maxHeight = max(maxHeight, maxHeightThisThrow)
                        succesfulThrows++
                        break
                    }
                    if (state.wontReach(targetArea)) {
                        break
                    }
                }
            }
        }

        println(maxHeight)
        println(succesfulThrows)
    }

    private fun State.wontReach(targetArea: List<Int>): Boolean {
        val xValues = targetArea.subList(0, 2)
        val yValues = targetArea.subList(2, 4)

        return this.position.y <= yValues.minOrNull()!! || this.position.x >= xValues.maxOrNull()!!
    }

    private fun State.isWithin(targetArea: List<Int>): Boolean {
        val xValues = targetArea.subList(0, 2)
        val yValues = targetArea.subList(2, 4)

        return this.position.x >= xValues.minOrNull()!!
                && this.position.x <= xValues.maxOrNull()!!
                && this.position.y >= yValues.minOrNull()!!
                && this.position.y <= yValues.maxOrNull()!!
    }

    private fun State.move(): State {
        val newX = this.position.x + this.velocity.x
        val newY = this.position.y + this.velocity.y
        val newVelocityX = abs(this.velocity.x - 1) * this.velocity.x.sign
        val newVelocityY = this.velocity.y - 1

        return State(Position(newX, newY), this.startingVelocity, Velocity(newVelocityX, newVelocityY))
    }

    data class Position(val x: Int, val y: Int)
    data class Velocity(val x: Int, val y: Int)
    data class State(val position: Position, val startingVelocity: Velocity, val velocity: Velocity)
}