package days.day16

import binaryToBigInt
import binaryToInt
import hexToBinary
import java.io.IOException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path

class Day16 {

    
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val payload = items[0].hexToBinary()

        val packet = decodePacket(payload)
        println(packet.versionSum())
        println(packet.totalValue())
    }

    private fun decodePacket(payload: String): Packet {
        var currentIndex = 7
        val (version, typeId) = payload.extractVersionAndTypeId()

        if (typeId == 4) {
            return decodeLiteralValuePacket(payload)
        }

        // operator packet
        val lengthTypeId = payload.substring(6, 7).binaryToInt()

        var lengthOfSubpackets = 0
        var numberOfSubpackets = 0
        if (lengthTypeId == 0) {
            lengthOfSubpackets = payload.substring(currentIndex, currentIndex + 15).binaryToInt()
            currentIndex += 15
        } else {
            numberOfSubpackets = payload.substring(currentIndex, currentIndex + 11).binaryToInt()
            currentIndex += 11
        }

        val headerLength = 7 + if (lengthTypeId == 0) 15 else 11

        val decodedPackets = mutableListOf<Packet>()
        while (true) {
            val packet = decodePacket(payload.substring(currentIndex))
            decodedPackets.add(packet)
            currentIndex += packet.totalLengthInBits
            if (lengthTypeId == 0 && decodedPackets.sumOf { it.totalLengthInBits } >= lengthOfSubpackets) {
                break
            }
            if (lengthTypeId == 1 && numberOfSubpackets == decodedPackets.size) {
                break
            }
        }
        return Packet(
            version,
            typeId,
            null,
            headerLength + decodedPackets.sumOf { it.totalLengthInBits },
            decodedPackets
        )
    }

    private fun decodeLiteralValuePacket(payload: String): Packet {
        var currentIndex = 6

        val (version, typeId) = payload.extractVersionAndTypeId()

        var valueBits = ""
        var bitsRead = 0
        while (true) {
            valueBits += payload.substring(currentIndex + 1, currentIndex + 5)
            bitsRead++
            currentIndex += 5
            if (payload[currentIndex - 5] == '0') {
                break
            }
        }
        return Packet(version, typeId, valueBits.binaryToBigInt(), bitsRead * 5 + 6, mutableListOf())
    }

    private fun String.extractVersionAndTypeId() = Pair(this.substring(0, 3).binaryToInt(), this.substring(3, 6).binaryToInt())

    private fun Packet.versionSum(): Int = versionSumUtil(0)

    private fun Packet.versionSumUtil(sum: Int): Int = if (!this.isOperator()) {
        sum + version
    } else {
        version + children.sumOf { it.versionSum() }
    }

    private fun Packet.totalValue(): BigInteger = when (this.typeId) {
        0 -> children.sumOf { it.totalValue() }
        1 -> children.map { it.totalValue() }.reduce { acc, value -> acc.times(value) }
        2 -> children.minOfOrNull { it.totalValue() }!!
        3 -> children.maxOfOrNull { it.totalValue() }!!
        4 -> this.value!!
        5 -> if (this.children[0].totalValue() > this.children[1].totalValue()) BigInteger.ONE else BigInteger.ZERO
        6 -> if (this.children[1].totalValue() > this.children[0].totalValue()) BigInteger.ONE else BigInteger.ZERO
        else -> if (this.children[1].totalValue() == this.children[0].totalValue()) BigInteger.ONE else BigInteger.ZERO
    }

    private fun Packet.isOperator() = this.typeId != 4

    data class Packet(
        val version: Int,
        val typeId: Int,
        val value: BigInteger?,
        val totalLengthInBits: Int,
        val children: MutableList<Packet>
    )
}