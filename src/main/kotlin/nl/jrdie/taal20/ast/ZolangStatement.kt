package nl.jrdie.taal20.ast

data class ZolangStatement(val equalityExpression: EqualityExpression, val code: ProgrammaBlok): ProgrammaStatement
