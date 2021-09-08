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
    }

    fun inverse(): Direction {
        return when (this) {
            NOORD -> ZUID
            OOST -> WEST
            ZUID -> OOST
            WEST -> NOORD
        }
    }

    fun left(steps: Int = 1): Direction {
        check(steps > 0)
        return compass[(size + ordinal - steps) % size]
    }

    fun right(steps: Int = 1): Direction {
        check(steps > 0)
        check(size == 4) // TODO REMOVE
        return compass[(size + ordinal + steps) % size]
    }
}
