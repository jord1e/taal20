package nl.jrdie.taal20.ast

sealed interface Expressie

data class NumberExpressie(val number: Taal20Int)

data class VarNameExpressie(val varName: Taal20VarName)

data class CalcExpressie(val left: Expressie, val operation: CalcExpressieType, val right: Expressie)

object KleurOogExpressie : Expressie
object ZwOogExpressie : Expressie
object KompasExpressie : Expressie

enum class CalcExpressieType {
    PLUS,
    MIN,
    TIMES,
    SLASH,
    PERCENT
}
