package days.day18

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.ceil
import kotlin.math.floor

object Day18 {

    var assignedIds = mutableListOf(0)

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val lines = Files.readAllLines(input).map { it.toString() }
        val fishes = lines.map { it.toFish() }

        val current = fishes.reduce { current, next -> Fish(current, next).reduceFish() }
        println(current.getMagnitude())

        var maxMagnitude = 0
        for (line1 in lines) {
            for (line2 in lines.filter { it != line1 }) {
                val fish = Fish(line1.toFish(), line2.toFish())
                maxMagnitude = maxOf(maxMagnitude, fish.reduceFish().getMagnitude())
            }
        }

        println(maxMagnitude)
    }

    private fun Fish.reduceFish(): Fish {
        while (true) {
            this.populateParentMap(mutableMapOf())
            val operation = this.findNextOperation()
            if (operation != null) {
                if (operation.explode) {
                    this.explode(operation.fish)
                } else {
                    this.split(operation.fish)
                }
            } else {
                break
            }
        }
        return this
    }

    private fun Fish.getMagnitude(): Int {
        if (this.isRegularNumber()) {
            return this.value!!
        }
        if (this.isSimplePair()) {
            return this.left!!.value!! * 3 + this.right!!.value!! * 2
        }
        return this.left!!.getMagnitude() * 3 + this.right!!.getMagnitude() * 2
    }

    private fun Fish.findNextOperation(): Operation? {
        val list = mutableListOf<Operation>()
        findNextOperationsUtil(list)
        return if (list.any { it.explode }) {
            list.filter { it.explode }[0]
        } else {
            list.getOrNull(0)
        }
    }

    private fun Fish.findNextOperationsUtil(list: MutableList<Operation>) {
        if (this.isSimplePair()) {
            if (this.getParentChain(this.id).size > 3) {
                list.add(Operation(this, explode = true))
            }
        } else if (this.isRegularNumber()) {
            if (this.value!! > 9) {
                list.add(Operation(this, explode = false))
            }
        }
        this.left?.findNextOperationsUtil(list)
        this.right?.findNextOperationsUtil(list)
    }

    private fun Fish.explode(fish: Fish) {
        val leftNeighbor = this.getFirstLeftNeighborOf(fish, fish.id)
        val rightNeighbor = this.getFirstRightNeighborOf(fish, fish.id)
        val parent = this.getById(this.parentMap!![fish.id]!!)

        // this fish dissapears
        if (leftNeighbor != null) {
            leftNeighbor.value = leftNeighbor.value?.plus(fish.left?.value!!)
        }
        if (rightNeighbor != null) {
            rightNeighbor.value = rightNeighbor.value?.plus(fish.right?.value!!)
        }

        if (parent?.left?.id == fish.id) {
            parent.left = Fish(null, null, 0)
        } else {
            parent!!.right = Fish(null, null, 0)
        }
    }

    private fun Fish.split(fish: Fish) {
        val leftFish = Fish(null, null, floor(fish.value!! / 2.0).toInt())
        val rightFish = Fish(null, null, ceil(fish.value!! / 2.0).toInt())
        val container = Fish(leftFish, rightFish, null)

        val parent = this.getById(this.parentMap!![fish.id]!!)
        if (parent?.left?.id == fish.id) {
            parent.left = container
        } else {
            parent!!.right = container
        }
    }

    private fun Fish.isSimplePair(): Boolean = this.left?.value != null && this.right?.value != null

    private fun Fish.isRegularNumber(): Boolean = this.left == null && this.right == null && this.value != null

    private fun Fish.getLeftmostValueOfTree(): Fish? {
        if (this.left == null && this.value == null) {
            return null
        }
        if (this.value != null) {
            return this
        }
        return this.left?.getLeftmostValueOfTree()
    }

    private fun Fish.getRightmostValueOfTree(): Fish? {
        if (this.right == null && this.value == null) {
            return null
        }
        if (this.value != null) {
            return this
        }
        return this.right?.getRightmostValueOfTree()
    }

    private fun Fish.getParentChain(id: Int): List<Int> {
        val chain = mutableListOf<Int>()
        var current = id
        while (true) {
            if (this.parentMap!![current] != null) {
                chain.add(current)
                current = this.parentMap!![current]!!
            } else {
                break
            }
        }
        return chain
    }

    private fun Fish.getFirstLeftNeighborOf(fish: Fish, targetId: Int): Fish? {
        val parentId = this.parentMap!![fish.id] ?: return null

        val parent = this.getById(parentId)!!

        return if (fish.isRightChildOf(parent)) {
            parent.left!!.getRightmostValueOfTree()
        } else {
            this.getFirstLeftNeighborOf(parent, targetId)
        }
    }

    private fun Fish.getFirstRightNeighborOf(fish: Fish, targetId: Int): Fish? {
        val parentId = this.parentMap!![fish.id] ?: return null

        val parent = this.getById(parentId)!!

        return if (fish.isLeftChildOf(parent)) {
            parent.right!!.getLeftmostValueOfTree()
        } else {
            getFirstRightNeighborOf(parent, targetId)
        }
    }

    private fun Fish.isLeftChildOf(fish: Fish): Boolean {
        val chain = this.getParentChain(this.id)
        return fish.left!!.id in chain
    }

    private fun Fish.isRightChildOf(fish: Fish): Boolean {
        val chain = this.getParentChain(this.id)
        return fish.right!!.id in chain
    }

    private fun Fish.getById(id: Int): Fish? {
        if (this.id == id) {
            return this
        }
        if (this.left != null) {
            val fromLeft = this.left!!.getById(id)
            if (fromLeft != null) {
                return fromLeft
            }
        }
        if (this.right != null) {
            val fromRight = this.right!!.getById(id)
            if (fromRight != null) {
                return fromRight
            }
        }
        return null
    }

    private fun Fish.assignIds() {
        this.id = assignedIds.maxOrNull()!! + 1
        assignedIds.add(this.id)
        if (this.left != null) {
            this.left!!.assignIds()
        }
        if (this.right != null) {
            this.right!!.assignIds()
        }
    }

    private fun Fish.populateParentMap(parentMap: MutableMap<Int, Int>) {
        if (parentMap.isEmpty()) {
            assignedIds = mutableListOf(0)
            this.assignIds()
        }

        if (this.left != null) {
            parentMap[this.left!!.id] = this.id
            this.left!!.populateParentMap(parentMap)
            this.left!!.parentMap = parentMap
        }
        if (this.right != null) {
            parentMap[this.right!!.id] = this.id
            this.right!!.populateParentMap(parentMap)
            this.right!!.parentMap = parentMap
        }
        this.parentMap = parentMap
    }

    data class Fish(
        var left: Fish?,
        var right: Fish?,
        var value: Int? = null,
        var id: Int = 0,
        var parentMap: MutableMap<Int, Int>? = null
    )

    private fun String.toFish(): Fish {
        if (this.matches("\\[[0-9],[0-9]\\]".toRegex())) {
            val parts = this.split(",")
            val firstFish = Fish(null, null, parts[0].replace("[", "").toInt())
            val secondFish = Fish(null, null, parts[1].replace("]", "").toInt())
            return Fish(firstFish, secondFish, null)
        }
        if (this.matches("[0-9]".toRegex())) {
            return Fish(null, null, this.toInt())
        }
        val strippedOuterParentheses = this.substring(1, this.length - 1)

        val firstBalancedParentheses = strippedOuterParentheses.extractBalancedParentheses()
        val withoutFirstBalancedParentheses = strippedOuterParentheses.replaceFirst(firstBalancedParentheses, "")
        val secondBalancedParentheses = withoutFirstBalancedParentheses.extractBalancedParentheses()

        return Fish(firstBalancedParentheses.toFish(), secondBalancedParentheses.toFish(), null)
    }

    private fun String.extractBalancedParentheses(): String {
        var level = 0
        var result = ""
        var i = 0

        while (true) {
            val char = this[i].toString()
            if (char.matches("[0-9]".toRegex()) && level == 0) {
                return char
            }
            if (char == "[") {
                level++
            }
            if (char == "]") {
                level--
            }
            if (char == ",") {
                if (level != 0) {
                    result += char
                }
            } else {
                result += char
            }
            if (result.isNotBlank() && level == 0) {
                return result
            }
            i++
        }
    }

    data class Operation(val fish: Fish, val explode: Boolean)
}