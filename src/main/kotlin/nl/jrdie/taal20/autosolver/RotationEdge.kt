package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.glade.models.Direction

abstract class RotationEdge(protected val from: Direction, protected val to: Direction) : ProgrammaEdge {

    companion object {
        fun getBestRotation(from: Direction, to: Direction, kostenkaart: Kostenkaart): RotationEdge {
            return SimpleDraaiEdge(from, to)
        }

        fun getBestRotationWithTurn(from: Direction, to: Direction, turn: Int, kostenkaart: Kostenkaart): RotationEdge {
            return RotationEdgeWithStepEdge(from, to, turn)
        }
    }

}
