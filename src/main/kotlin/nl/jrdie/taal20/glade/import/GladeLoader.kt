package nl.jrdie.taal20.glade.import

import nl.jrdie.taal20.glade.*
import kotlin.math.sqrt

object GladeLoader {

    fun loadMaze(matrix: String): Glade {
        val size = sqrt(matrix.count { it == ';' } + 1.0).toInt()
        println(size)
        val codes = matrix.split(";")
        val nested = (0..(size-1)).map { codes.subList(it * size, it * size + 20)
            .map { code -> codeToTile(code)
            }.toMutableList() }.toMutableList()
//        matrix.split()
        return Glade(size, nested)
//        return Glade(Array(size) { Array<Tile>(size) { ObstakelTile(1) } })
    }

    fun codeToTile(code: String): Tile {
        val letter = code[0]
        val value = code.substring(1).toInt()
        return when (letter) {
            'B' -> BomTile(value)
            'C' -> KleurTile(value)
            'D' -> DoelTile(value)
            'E' -> BonusTile(value)
            'O' -> ObstakelTile(value)
            'R' -> DraaiTile(value)
            'S' -> StartTile(value)
            else -> throw IllegalArgumentException("Invalid code: $code")
        }
    }

    fun printGlade(glade: Glade): String{
        return glade.matrix.joinToString("\n") { it.joinToString(", ") }
    }

}
