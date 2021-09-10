package nl.jrdie.taal20.glade.interpreter

import nl.jrdie.taal20.glade.models.Direction
import nl.jrdie.taal20.glade.models.Point

data class Robot(val location: Point, private val directionOrdinal: Int) {

    constructor(location: Point, direction: Direction) : this(location, direction.ordinal)

    val direction: Direction
        get() = Direction.ofOrdinal(directionOrdinal)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Robot

        if (location != other.location) return false
        if (directionOrdinal != other.directionOrdinal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + directionOrdinal
        return result
    }

}
