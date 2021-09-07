package nl.jrdie.taal20.ast

sealed interface InitConstantType

sealed interface ExpressieConstantType : InitConstantType

data class Taal20Int(val value: Int) : ExpressieConstantType
data class Taal20VarName(val value: String) : InitConstantType
object KleurOogConstant : InitConstantType
object ZwOogConstant : InitConstantType
object KompasConstant : InitConstantType

