package nl.jrdie.taal20.ast

data class AlsStatement(val equalityExpression: EqualityExpression, val als: ProgrammaBlok): ProgrammaStatement, IAlsStatement
