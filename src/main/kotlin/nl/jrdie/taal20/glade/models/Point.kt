package nl.jrdie.taal20.glade.models

data class Point(val x: Int, val y: Int) {

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

}
