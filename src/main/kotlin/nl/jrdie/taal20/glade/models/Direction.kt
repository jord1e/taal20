package nl.jrdie.taal20.glade.models

enum class Direction {
    NOORD,
    OOST,
    ZUID,
    WEST;

    companion object {
        private val compass = values()
        private val size = compass.size

        fun randomDirection(): Direction {
            return compass.random()
        }

        fun ofOrdinal(ordinal: Int): Direction {
            return compass[ordinal % size]
        }

        fun allDirections(): List<Direction> {
            return listOf(*compass)
        }
    }

    fun inverse(): Direction {
        return when (this) {
            NOORD -> ZUID
            OOST -> WEST
            ZUID -> NOORD
            WEST -> OOST
        }
    }

    fun left(steps: Int = 1): Direction {
        check(steps > 0)
        return compass[(size + ordinal - steps) % size]
    }

    fun right(steps: Int = 1): Direction {
        check(steps > 0)
        return compass[(size + ordinal + steps) % size]
    }

    fun stepsTo(to: Direction): Int {
        return this.ordinal - to.ordinal
    }

}
