package days.day4

class Board(val numbers: MutableList<List<Int>> = mutableListOf(), val calledNumbers: List<Int> = listOf()) {

    fun isWinning(firstNumbers: Int): Int {
        return isWinningColumn(firstNumbers) + isWinningRow(firstNumbers)
    }

    private fun isWinningColumn(firstNumbers: Int): Int {
        for (i in 0..4) {
            var isWinning = true
            for (j in 0..4) {
                if (!calledNumbers.subList(0, firstNumbers).contains(numbers[i][j])) {
                    isWinning = false
                }
            }
            if (isWinning) {
                val flatList = numbers.flatten().filter { !calledNumbers.subList(0, firstNumbers).contains(it) }
                val lastCalledNumber = calledNumbers[firstNumbers - 1]
                return flatList.sum() * lastCalledNumber
            }
        }
        return 0
    }

    private fun isWinningRow(firstNumbers: Int): Int {
        for (i in 0..4) {
            var isWinning = true
            for (j in 0..4) {
                if (!calledNumbers.subList(0, firstNumbers).contains(numbers[j][i])) {
                    isWinning = false
                }
            }
            if (isWinning) {
                val flatList = numbers.flatten().filter { !calledNumbers.subList(0, firstNumbers).contains(it) }
                val lastCalledNumber = calledNumbers[firstNumbers - 1]
                return flatList.sum() * lastCalledNumber
            }
        }
        return 0
    }


}