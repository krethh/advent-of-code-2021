import java.math.BigInteger

fun String.hexToBinary() = this.map { it.toString() }
    .joinToString(separator = "") { Integer.toBinaryString(Integer.parseInt(it, 16)).padStart(4, '0') }

fun String.binaryToInt(): Int = Integer.parseInt(this, 2)

fun String.binaryToBigInt() = BigInteger(this, 2)

fun String.intAt(index: Int) = this[index].toString().toInt()
