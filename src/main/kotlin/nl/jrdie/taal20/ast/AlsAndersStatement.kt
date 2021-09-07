package nl.jrdie.taal20.ast

data class AlsAndersStatement(val equalityExpression: EqualityExpression, val als: ProgrammaBlok, val anders: ProgrammaBlok): ProgrammaStatement, IAlsStatement
