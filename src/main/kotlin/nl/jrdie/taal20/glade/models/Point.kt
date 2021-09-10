package nl.jrdie.taal20.glade.models

import java.util.function.ToIntBiFunction

data class Point(var x: Int, var y: Int) : Comparable<Point> {

    companion object {
        // TODO
        fun min(positions: List<Point>): Point {
            return reduce(positions) { a: Int, b: Int -> Math.min(a, b) }
        }

        fun min(vararg positions: Point): Point {
            return min(listOf(*positions))
        }

        fun max(positions: List<Point>): Point {
            return reduce(positions) { a: Int, b: Int -> Math.max(a, b) }
        }

        fun max(vararg positions: Point): Point {
            return max(listOf(*positions))
        }

        fun reduce(
            positions: List<Point>,
            func: ToIntBiFunction<Int, Int>
        ): Point {
            return positions.reduce { p, p2 ->
                val x = func.applyAsInt(p.x, p2.x)
                val y = func.applyAsInt(p.y, p2.y)
                Point(x, y)
            }
        }
    }

    fun incInDirection(direction: Direction): Point {
        return when (direction) {
            Direction.NOORD -> Point(x - 1, y) // Werkt
            Direction.OOST -> Point(x, y + 1) // Werkt
            Direction.ZUID -> Point(x + 1, y) // Werkt
            Direction.WEST -> Point(x, y - 1)
        }
    }

    fun bracketNotation(): String {
        return "[$x,$y]"
    }

    // TODO ||||

    fun distance(position: Point): Int {
        return Math.abs(x - position.x) + Math.abs(y - position.y)
    }

    fun add(x: Int, y: Int) {
        addX(x)
        addY(y)
    }

    fun setX(x: Int): Int {
        val old = this.x
        this.x = x
        return old
    }

    fun addX(x: Int): Int {
        return setX(x + x)
    }

    fun setY(y: Int): Int {
        val old = this.y
        this.y = y
        return old
    }

    fun addY(y: Int): Int {
        return setY(y + y)
    }

    operator fun set(x: Int, y: Int) {
        setX(x)
        setY(y)
    }

    fun add(position: Point) {
        add(position.x, position.y)
    }

    override fun compareTo(other: Point): Int {
        return compareValuesBy(this, other, { it.x }, { it.y })
    }

}
