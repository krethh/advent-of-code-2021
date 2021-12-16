package days.day16

import java.io.IOException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path

object Day16 {

    @JvmStatic
    @Throws(IOException::class)
    fun solve(input: Path?) {
        val items = Files.readAllLines(input).map { it.toString() }

        val value = items[0]
        var result = ""
        value.forEach {
            val int = Integer.parseInt(it.toString(), 16)
            val binary = Integer.toBinaryString(int).padStart(4, '0')
            result += binary
        }

        val packet = decodePacket(result)
        println (packet.versionSum())
        println (packet.totalValue())
    }

    private fun String.value(): Int = Integer.parseInt(this, 2)

    private fun String.bigInt() = BigInteger(this, 2)

    private fun decodePacket(result: String): Packet {
        var currentIndex = 7

        val packetVersion = result.substring(0 until 3).value()
        val packetTypeId = result.substring(3 until 6).value()

        if (packetTypeId == 4) {
            return decodeLiteralValuePacket(result)
        }

        // operator packet
        val lengthTypeId = result.substring(6, 7).value()

        var lengthOfSubpackets = 0
        var numberOfSubpackets = 0
        if (lengthTypeId == 0) {
            lengthOfSubpackets = result.substring(currentIndex, currentIndex + 15).value()
            currentIndex += 15
        } else {
            numberOfSubpackets = result.substring(currentIndex, currentIndex + 11).value()
            currentIndex += 11
        }

        val headerLength = if (lengthTypeId == 0) 15 else 11

        val decodedPackets = mutableListOf<Packet>()
        while (true) {
            val packet = decodePacket(result.substring(currentIndex))
            decodedPackets.add(packet)
            currentIndex += packet.totalLengthInBits
            if (lengthTypeId == 0 && decodedPackets.sumOf { it.totalLengthInBits } >= lengthOfSubpackets) {
                break
            }
            if (lengthTypeId == 1 && numberOfSubpackets == decodedPackets.size) {
                break
            }
        }
        return Packet(packetVersion, packetTypeId, null, true, null, 7 + headerLength + decodedPackets.sumOf { it.totalLengthInBits }, decodedPackets)
    }

    private fun decodeLiteralValuePacket(result: String): Packet {
        var currentIndex = 0
        val subpacketVersion = result.substring(currentIndex, currentIndex + 3).value()
        currentIndex += 3
        val subpacketTypeId = result.substring(currentIndex, currentIndex + 3).value()
        currentIndex += 3

        var valueBits = ""
        var bitsRead = 0
        while (true) {
            valueBits += result.substring(currentIndex + 1, currentIndex + 5)
            bitsRead++
            currentIndex += 5
            if (result[currentIndex - 5] == '0') {
                break
            }
        }
        return Packet(
            subpacketVersion,
            subpacketTypeId,
            valueBits.bigInt(),
            false,
            null,
            bitsRead * 5 + 6,
            mutableListOf()
        )
    }

    private fun Packet.versionSum(): Int {
        return versionSumUtil(0)
    }

    private fun Packet.versionSumUtil(sum: Int): Int {
        return if (!this.operator) {
            sum + version
        } else {
            version + children.sumOf { it.versionSum() }
        }
    }


    private fun Packet.totalValue(): BigInteger {
        return when(this.typeId) {
            0 -> children.sumOf { it.totalValue() }
            1 -> children.map { it.totalValue() }.reduce{ acc, value -> acc.times(value)}
            2 -> children.minOfOrNull { it.totalValue() }!!
            3 -> children.maxOfOrNull { it.totalValue() }!!
            4 -> this.value!!
            5 -> if (this.children[0].totalValue() > this.children[1].totalValue()) BigInteger.ONE else BigInteger.ZERO
            6 -> if (this.children[1].totalValue() > this.children[0].totalValue()) BigInteger.ONE else BigInteger.ZERO
            else -> if (this.children[1].totalValue() == this.children[0].totalValue()) BigInteger.ONE else BigInteger.ZERO
        }
    }

    data class Packet(
        val version: Int,
        val typeId: Int,
        val value: BigInteger?,
        val operator: Boolean,
        val payload: String?,
        val totalLengthInBits: Int,
        val children: MutableList<Packet>
    )
}