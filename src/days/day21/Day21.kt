package days.day21

import java.io.IOException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.math.min

object Day21 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        solvePartOne()
        solvePartTwo()
    }

    fun solvePartTwo() {
        val player1Position = 2
        val player2Position = 10

        val firstBoard = Board(player1Position, player2Position, 0, 0, true, BigInteger.ONE)

        val queue = ArrayDeque<Board>()
        queue.push(firstBoard)

        var player1Wins = BigInteger.ZERO
        var player2Wins = BigInteger.ZERO

        while (!queue.isEmpty()) {
            val board = queue.pop()

            val possibleRolls = variationsWithRepetitions()

            for (roll in possibleRolls) {
                val playerPosition = if (board.player1Turn) board.player1Position else board.player2Position
                val playerScore = if (board.player1Turn) board.player1Score else board.player2Score
                var nextPosition = playerPosition + roll.value
                if (nextPosition > 10) {
                    nextPosition %= 10
                }
                val newDistinctPaths = roll.count.toBigInteger().times(board.distinctPaths)
                val nextScore = playerScore + nextPosition
                if (nextScore > 20) {
                    if (board.player1Turn) {
                        player1Wins = player1Wins.plus(newDistinctPaths)
                    } else {
                        player2Wins = player2Wins.plus(newDistinctPaths)
                    }
                }
                else {
                    if (board.player1Turn) {
                        val nextBoard = Board(
                            nextPosition,
                            board.player2Position,
                            nextScore,
                            board.player2Score,
                            false,
                            newDistinctPaths
                        )
                        queue.push(nextBoard)
                    } else {
                        val nextBoard = Board(
                            board.player1Position,
                            nextPosition,
                            board.player1Score,
                            nextScore,
                            true,
                            newDistinctPaths
                        )
                        queue.push(nextBoard)
                    }
                }
            }
        }
        if (player1Wins > player2Wins) {
            println(player1Wins)
        } else {
            println(player2Wins)
        }
    }



    data class Board(
        val player1Position: Int,
        val player2Position: Int,
        val player1Score: Int,
        val player2Score: Int,
        val player1Turn: Boolean,
        val distinctPaths: BigInteger
    )

    data class Roll(val value: Int, val count: Int)

    fun variationsWithRepetitions(): List<Roll> {
        var result = mutableListOf<Int>()

        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    val sum = i + j + k
                    result.add(sum)
                }
            }
        }
        return result.groupingBy { it }.eachCount().map { Roll(it.key, it.value) }
    }

    private fun solvePartOne() {
        var player1Position = 2
        var player2Position = 10

        var player1Score = 0
        var player2Score = 0

        var player1Turn = true
        var moves = 0

        var dice = 1

        while (true) {
            var steps = 0

            for (i in 0 until 3) {
                if (dice == 101) {
                    dice = 1
                }
                steps += dice
                dice++
            }

            if (player1Turn) {
                player1Position += steps % 10
                if (player1Position > 10) {
                    player1Position %= 10
                }
                player1Score += player1Position
            } else {
                player2Position += steps % 10
                if (player2Position > 10) {
                    player2Position %= 10
                }
                player2Score += player2Position
            }

            moves += 3
            player1Turn = !player1Turn
            if (player1Score >= 1000 || player2Score >= 1000) {
                break
            }
        }

        println (moves * min(player1Score, player2Score))
    }
}