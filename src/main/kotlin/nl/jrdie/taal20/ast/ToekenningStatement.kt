package nl.jrdie.taal20.ast

data class ToekenningStatement(val varName: Taal20VarName, val expressie: Expressie): ProgrammaStatement
