package days.day23

import java.io.IOException
import java.nio.file.Path
import java.util.*

object Day23 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val nodes = listOf(
            Node("r10", "A"),
            Node("r11", "D"),
            Node("r12", "D"),
            Node("r13", "B"),
            Node("r20", "D"),
            Node("r21", "B"),
            Node("r22", "C"),
            Node("r23", "C"),
            Node("r30", "C"),
            Node("r31", "A"),
            Node("r32", "B"),
            Node("r33", "B"),
            Node("r40", "A"),
            Node("r41", "C"),
            Node("r42", "A"),
            Node("r43", "D"),
            Node("h1", "."),
            Node("h2", "."),
            Node("h3", "."),
            Node("h4", "."),
            Node("h5", "."),
            Node("h6", "."),
            Node("h7", ".")
        )

        val edges = listOf(
            Edge("r10" , "r11", 1),
            Edge("r11" , "r12", 1),
            Edge("r12" , "r13", 1),
            Edge("r20" , "r21", 1),
            Edge("r21" , "r22", 1),
            Edge("r22" , "r23", 1),
            Edge("r30" , "r31", 1),
            Edge("r31" , "r32", 1),
            Edge("r32" , "r33", 1),
            Edge("r40" , "r41", 1),
            Edge("r41" , "r42", 1),
            Edge("r42" , "r43", 1),
            Edge("h1", "h2", 1),
            Edge("h2", "h3", 2),
            Edge("h3", "h4", 2),
            Edge("h4", "h5", 2),
            Edge("h5", "h6", 2),
            Edge("h6", "h7", 1),
            Edge("r13", "h2", 2),
            Edge("r13", "h3", 2),
            Edge("r23", "h3", 2),
            Edge("r23", "h4", 2),
            Edge("r33", "h4", 2),
            Edge("r33", "h5", 2),
            Edge("r43", "h5", 2),
            Edge("r43", "h6", 2),
        )

        val graph = Graph(nodes, edges)
        val state = State(graph, 0)
        val comparator = compareBy<State> { it.energy }
        val queue = PriorityQueue(comparator)
        queue.add(state)
        val visited = mutableMapOf<String, Int>()
        val results = mutableSetOf<Int>()

        val checkpoints = mutableListOf<Int>()
        while (queue.isNotEmpty()) {
            val current = queue.remove()

            if ((visited[graph.serialized()] ?: Int.MAX_VALUE) < current.energy) {
                continue
            }

            if (current.energy % 1000 == 0) {
                if (current.energy !in checkpoints) {
                    println(current.energy)
                    checkpoints.add(current.energy)
                }
            }

            if (current.graph.isTarget()) {
                results.add(current.energy)
                break
            }

            val nodesWithAmphipods = current.graph.nodes.filter { it.hasAmphipod() }
            nodesWithAmphipods.forEach {
                val nextMoves = current.getPossibleNextMoves(it, visited, !it.isHallway())
                queue.addAll(nextMoves)
            }
        }

        println(results.minOrNull())
    }

    fun String.getEnergyMultiplier() = when (this) {
        "A" -> 1
        "B" -> 10
        "C" -> 100
        else -> 1000
    }

    private fun Node.isHallway() = this.id.startsWith("h")

    fun Graph.copyGraph(): Graph  {
        val nodes = this.nodes.map { it.copy() }
        val edges = this.edges.map { it.copy() }
        return Graph(nodes, edges)
    }

    private fun Graph.getNeighbors(node: Node) =
        edges.filter { it.first == node.id }.map { e -> nodes.first { e.second == it.id } }
            .plus(edges.filter { it.second == node.id }.map { e -> nodes.first { e.first == it.id } })

    private fun Graph.costBetween(first: Node, second: Node) = this.edges.firstOrNull {
        (it.first == first.id && it.second == second.id)
                || (it.first == second.id && it.second == first.id)
    }?.cost ?: Int.MAX_VALUE

    private fun Graph.canMoveIntoARoom(start: Node, room: Node): Boolean {
        val roomPrefix = room.id.substring(0..1)
        val allRoomNodes = this.nodes.filter { it.id.startsWith(roomPrefix) }
        val currentOccupantsOfRoom = allRoomNodes.filter { it.hasAmphipod() }.map { it.occupant }
        val alreadyInRoom = start in allRoomNodes

        if (alreadyInRoom) {
            return true
        }

        if (currentOccupantsOfRoom.any { it != roomPrefix.properRoomOccupant() }) {
            return false
        }

        if (start.occupant != roomPrefix.properRoomOccupant()) {
            return false
        }

        return true
    }

    fun Graph.isLegalNextMove(start: Node, end: Node): Boolean {
        if (end.hasAmphipod()) {
            return false
        }

        if (!end.isHallway() && !this.canMoveIntoARoom(start, end)) {
            return false
        }

        if (this.alreadyInGoodRoom(start)) {
            return false
        }

        return true;
    }


    private fun Graph.alreadyInGoodRoom(node: Node): Boolean {
        if (!node.id.startsWith("r")) {
            return false
        }
        val roomPrefix = node.id.substring(0..1)
        if (node.occupant != roomPrefix.properRoomOccupant()) {
            return false
        }

        val indexInRoom = node.id.replace(roomPrefix, "").toInt()
        var allPresent = true
        for (i in 0 until indexInRoom) {
            val id = roomPrefix + i
            val belowNode = this.nodes.first { it.id == id }
            if (belowNode.occupant != roomPrefix.properRoomOccupant()) {
                allPresent = false
            }
        }

        return allPresent
    }

    private fun String.properRoomOccupant() = when (this) {
        "r1" -> "A"
        "r2" -> "B"
        "r3" -> "C"
        else -> "D"
    }

    fun State.getPossibleNextMoves(start: Node, visited: MutableMap<String, Int>, canEndInHallway: Boolean): List<State> {
        if (!start.hasAmphipod()) {
            return listOf()
        }

        val neighbors = this.graph.getNeighbors(start)

        val nextStates = mutableListOf<State>()

        neighbors.forEach { neighbor ->
            val legal = this.graph.isLegalNextMove(start, neighbor)

            if (legal) {
                val newGraph = this.graph.copyGraph()
                val currentOccupant = start.occupant
                newGraph.nodes.first { it.id == start.id }.occupant = "."
                newGraph.nodes.first { it.id == neighbor.id }.occupant = currentOccupant
                val serialized = newGraph.serialized()
                val newCost = this.graph.costBetween(start, neighbor) * currentOccupant.getEnergyMultiplier() + this.energy
                val newState = State(newGraph, newCost)
                if (serialized !in visited.keys || (visited[serialized] ?: Int.MAX_VALUE) > newCost) {
                    visited[serialized] = newCost
                    if (!(neighbor.isHallway() && !canEndInHallway)) {
                        nextStates.add(newState)
                    }
                    nextStates.addAll(newState.getPossibleNextMoves(newGraph.nodes.first { it.id == neighbor.id }, visited, canEndInHallway))
                }
            }

        }

        return nextStates
    }

    private fun Node.hasAmphipod() = this.occupant in listOf("A", "B", "C", "D")

    private fun Graph.isTarget(): Boolean {
        return nodes.filter { it.id.startsWith("r1") }.all { it.occupant == "A" } &&
                nodes.filter { it.id.startsWith("r2") }.all { it.occupant == "B" } &&
                nodes.filter { it.id.startsWith("r3") }.all { it.occupant == "C" } &&
                nodes.filter { it.id.startsWith("r4") }.all { it.occupant == "D" }
    }

    private fun Graph.serialized() = nodes.sortedBy { it.id }.joinToString("") { "${it.id}-${it.occupant} " }

    data class Node(val id: String, var occupant: String)
    data class Edge(val first: String, val second: String, val cost: Int)
    data class Graph(val nodes: List<Node>, val edges: List<Edge>)
    data class State(val graph: Graph, val energy: Int)

}