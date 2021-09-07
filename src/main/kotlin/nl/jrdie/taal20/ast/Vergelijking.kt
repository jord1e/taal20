package nl.jrdie.taal20.ast

data class Vergelijking(val left: Expressie, val type: VergelijkingType, val right: Expressie)

enum class VergelijkingType {
    EQEQ,
    NOTEQ,
    LT,
    GT
}
