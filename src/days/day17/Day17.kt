package days.day17

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Day17 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }[0]
        val numbers = "-?[1-9]\\d*|0".toRegex().findAll(items)
        val targetArea = numbers.toList().map { it.value.toInt() }

        val map = mutableMapOf<Velocity, Int?>()

        var maxHeight = 0
        for (x in IntRange(0, 500)) {
            for (y in IntRange(1, 500)) {
                var maxHeightThisThrow = 0
                val startingPosition = Position(0, 0)
                val startingVelocity = Velocity(x, y)
                var state = State(startingPosition, startingVelocity, startingVelocity)

                while (true) {
                    state = state.move()
                    if (maxHeightThisThrow < state.position.y) {
                        maxHeightThisThrow = state.position.y
                    }
                    if (state.isWithin(targetArea)) {
                        if (maxHeightThisThrow > maxHeight) {
                            println(maxHeightThisThrow)
                            maxHeight = maxHeightThisThrow
                        }
                        map[state.startingVelocity] = maxHeight
                        break
                    }
                    if (state.wontReach(targetArea)) {
                        map[state.startingVelocity] = null
                        break
                    }
                }
            }
        }

        println (map.filter { it.value == map.maxOf { it.value ?: 0 } })
    }

    private fun State.wontReach(values: List<Int>): Boolean {
        val yValues = values.subList(2, 4)

        return this.position.y <= yValues.minOfOrNull { it }!!
    }
    
    private fun State.isWithin(values: List<Int>): Boolean {
        val xValues = values.subList(0, 2)
        val yValues = values.subList(2, 4)
        
        return this.position.x >= xValues.minOfOrNull { it }!!
                && this.position.x <= xValues.maxOfOrNull { it }!!
                && this.position.y >= yValues.minOfOrNull { it }!!
                && this.position.y <= yValues.maxOfOrNull { it }!!
    }

    private fun State.move(): State {
        val newX = this.position.x + this.velocity.x
        val newY = this.position.y + this.velocity.y
        var newVelocityX = this.velocity.x

        if (newVelocityX < 0) {
            newVelocityX += 1
        } else if(newVelocityX > 0) {
            newVelocityX -= 1
        }

        var newVelocityY = this.velocity.y - 1

        return State(Position(newX, newY), this.startingVelocity, Velocity(newVelocityX, newVelocityY))
    }

    data class Position(val x: Int, val y: Int)
    data class Velocity(val x: Int, val y: Int)
    data class State(val position: Position, val startingVelocity: Velocity, val velocity: Velocity)
}