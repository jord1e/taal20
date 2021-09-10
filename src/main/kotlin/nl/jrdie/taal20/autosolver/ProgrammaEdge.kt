package nl.jrdie.taal20.autosolver

import nl.jrdie.taal20.data.Kostenkaart

interface ProgrammaEdge {
    fun generateCode(): String
    fun getPrice(kostenkaart: Kostenkaart): Int
}
