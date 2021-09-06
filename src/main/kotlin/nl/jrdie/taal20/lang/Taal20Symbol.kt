package nl.jrdie.taal20.lang

import java_cup.runtime.Symbol
import nl.jrdie.taal20._parser.Taal20SymbolType

data class Taal20Symbol(
    val type: Int,
    val line: Int,
    val column: Int,
    val value: Any? = null,
) : Symbol(type, line, column, value) {

    constructor(type: Int, line: Int, column: Int) : this(type, line, column, null)

    override fun toString(): String {
        return "Taal20Symbol(type=$type (${Taal20SymbolType.terminalNames[type]}), line=$line, column=$column, value=$value)"
    }

}
