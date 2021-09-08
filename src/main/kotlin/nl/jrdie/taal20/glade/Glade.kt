package nl.jrdie.taal20.glade

import nl.jrdie.taal20.glade.models.Point

class Glade(val size: Int, val matrix: List<List<Tile>>) {

    fun findFirstTileLoc(tile: Tile): Point {
        for ((x, row) in matrix.withIndex()) {
            for ((y, col) in row.withIndex()) {
                if (col == tile) {
                    return Point(x, y)
                }
            }
        }
        throw RuntimeException("Cannot find tile $tile in $matrix")
    }

    inline fun <reified T : Tile> findFirstTileOfType(): Pair<T, Point> {
        for ((x, row) in matrix.withIndex()) {
            for ((y, col) in row.withIndex()) {
                if (col is T) {
                    return col to Point(x, y)
                }
            }
        }
        throw RuntimeException("Cannot find of type ${T::class.java.name} in $matrix")
    }

    inline fun <reified T : Tile> findTilesOfType(): List<Pair<T, Point>> {
        val result = mutableListOf<Pair<T, Point>>()
        for ((x, row) in matrix.withIndex()) {
            for ((y, col) in row.withIndex()) {
                if (col is T) {
                    result.add(col to Point(x, y))
                }
            }
        }
        return result
    }

    fun tileAt(point: Point): Tile {
        return matrix[point.x][point.y]
    }

    override fun toString(): String {
//        return "Glade(matrix=${Arrays.deepToString(matrix)})"
        return "Glade(matrix=${matrix})"
    }
}
