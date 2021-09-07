package nl.jrdie.taal20.ast.printer

import nl.jrdie.taal20.ast.*

class Taal20AstPrinter {

    fun printProgramma(programma: Programma): String {
        return printInitBlok(programma.initBlok) + printProgrammaBlok(programma.programmaBlok)
    }

    fun printInitBlok(initBlok: InitBlok): String {
        return initBlok.statements.joinToString("") {
            printInitStatement(it)
        }
    }

    fun printInitStatement(initStatement: InitStatement): String {
        return when (initStatement) {
            is GebruikStatement -> "gebruik " + printGebruiktType(initStatement.type) + "\n"
        }
    }

    fun printGebruiktType(type: InitConstantType): String {
        return when (type) {
            is Taal20Int -> type.value.toString()
            KleurOogConstant -> "kleurOog"
            KompasConstant -> "kompas"
            is Taal20VarName -> type.value
            ZwOogConstant -> "zwOog"
        }
    }

    fun printProgrammaBlok(programmaBlok: ProgrammaBlok): String {
        return programmaBlok.statements.joinToString("") {
            printProgrammaStatement(it)
        }
    }

    fun printProgrammaStatement(stmt: ProgrammaStatement): String {
        return when (stmt) {
            is AlsAndersStatement -> printAlsAndersStatement(stmt)
            is AlsStatement -> printAlsStatement(stmt)
            is OpdrachtStatement -> printOpdrachtStatement(stmt)
            is ToekenningStatement -> printToekenningStatement(stmt)
            is ZolangStatement -> printZolangStatement(stmt)
        }
    }

    fun printZolangStatement(stmt: ZolangStatement): String {
        return "zolang " + printVergelijking(stmt.equalityExpression) + " {\n" + printProgrammaBlok(stmt.code).indent(2) + "}\n"
    }

    fun printToekenningStatement(stmt: ToekenningStatement): String {
        return stmt.varName.value + " = " + printExpressie(stmt.expressie) + "\n"
    }

    fun printAlsStatement(stmt: AlsStatement): String {
        return "als " + printVergelijking(stmt.equalityExpression) + " {\n" + printProgrammaBlok(stmt.als).indent(2) + "}\n"
    }

    fun printAlsAndersStatement(stmt: AlsAndersStatement): String {
        return "als " + printVergelijking(stmt.equalityExpression) + " {\n" + printProgrammaBlok(stmt.als).indent(2) +
                "} anders {\n" + printProgrammaBlok(stmt.anders).indent(2) + "}\n"
    }

    fun printOpdrachtStatement(opdrachtType: OpdrachtStatement): String {
        return when (opdrachtType.type) {
            OpdrachtType.STAP_VOORUIT -> "stapVooruit"
            OpdrachtType.STAP_ACHTERUIT -> "stapAchteruit"
            OpdrachtType.DRAAI_LINKS -> "draaiLinks"
            OpdrachtType.DRAAI_RECHTS -> "draaiRechts"
        } + "\n"
    }

    fun printVergelijking(equalityExpression: EqualityExpression): String {
        return printExpressie(equalityExpression.left) + " " +
                printEqualityType(equalityExpression.type) + " " +
                printExpressie(equalityExpression.right)
    }

    fun printEqualityType(type: VergelijkingType): String {
        return when (type) {
            VergelijkingType.EQEQ -> "=="
            VergelijkingType.NOTEQ -> "!="
            VergelijkingType.LT -> ">"
            VergelijkingType.GT -> ">"
        }
    }

    fun printCalcOperator(type: CalcExpressieType): String {
        return when (type) {
            CalcExpressieType.PLUS -> "+"
            CalcExpressieType.MIN -> "-"
            CalcExpressieType.TIMES -> "*"
            CalcExpressieType.SLASH -> "/"
            CalcExpressieType.PERCENT -> "%"
        }
    }

    fun printExpressie(expressie: Expressie): String {
        return when (expressie) {
            is CalcExpressie -> printExpressie(expressie.left) + " " +
                    printCalcOperator(expressie.operation) + " " +
                    printExpressie(expressie.right)
            KleurOogExpressie -> "kleurOog"
            KompasExpressie -> "kompas"
            is NumberExpressie -> expressie.number.value.toString()
            is VarNameExpressie -> expressie.varName.value
            ZwOogExpressie -> "zwOog"
        }
    }

}
