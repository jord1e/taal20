package nl.jrdie.taal20.glade.interpreter

import nl.jrdie.taal20.ast.*
import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.KostenkaartField.*
import nl.jrdie.taal20.glade.*
import nl.jrdie.taal20.glade.models.Direction
import nl.jrdie.taal20.glade.models.Point
import java.util.LinkedList

class Taal20Interpreter(
    val glade: Glade,
    val programma: Programma,
    val kostenkaart: Kostenkaart,
) {

    val context = Taal20Context(
        mutableListOf()
    )

    var totalCost = 0
    var variables = HashMap<String, Int?>()
    var messages = LinkedList<String>()
    var position: Point = Point(0, 0) // TODO bepalen
    var direction: Direction = Direction.NOORD // TODO bepalen
    var error = false
    var bonusTilesVisited = mutableSetOf<Point>()

    var zolangCount = 0
    var alsCount = 0
    var opdrachtCount = 0
    var toekenningCount = 0

    private fun cost(field: KostenkaartField, message: String) {
        val cost = kostenkaart[field]!!
        messages.add(message.replace("{cost}", cost.toString()))
        totalCost += cost
    }

    @Throws(RuntimeException::class)
    private fun error(message: String) {
        throw RuntimeException(messages.joinToString("\n") + "\n" +
            "=== GLADE INTERPRETER EXCEPTION ===\n" +
                    "$message\n" +
                    "=== --------------------------- ==="
        )
        error = true;
    }

    private fun message(message: String) {
        messages.add(message)
    }

    fun interpret() {
        val start = glade.findFirstTileOfType<StartTile>()
        position = start.second
        direction = start.first.startDirection()

        programma
            .initBlok
            .statements
            .forEach { initStatement(it) }

        programmaBlok(programma.programmaBlok)

        messages.addFirst("kosten toekenning: $toekenningCount * ${kostenkaart[SOFTWARE_TOEKENNING]}")
        messages.addFirst("kosten opdracht: $opdrachtCount * ${kostenkaart[SOFTWARE_OPDRACHT]}")
        messages.addFirst("kosten als: $alsCount * ${kostenkaart[SOFTWARE_ALS_KEUZE]}")
        messages.addFirst("kosten zolang: $zolangCount * ${kostenkaart[SOFTWARE_ZOLANG_LUS]}")
        messages.addFirst("start kapitaal: " + kostenkaart[START_KAPITAAL])

        val eindKapitaal = kostenkaart[START_KAPITAAL]!! - totalCost
        message("eind kapitaal :$eindKapitaal")
//         TODO > of >=
//        if (eindKapitaal >= 0 && doelGehaald) {
//            message("Doel bereikt binnen budget")
//        }

        messages
            .forEach { println(it) }
    }

    private fun programmaBlok(programmaBlok: ProgrammaBlok) {
        if (error) {
            return
        }
        programmaBlok
            .statements
            .forEach { programmaStatement(it) }
    }

    private fun programmaStatement(stmt: ProgrammaStatement) {
        if (error) {
            return
        }
        when (stmt) {
            is AlsStatement -> alsStatement(stmt)
            is AlsAndersStatement -> alsAndersStatement(stmt)
            is OpdrachtStatement -> opdrachtStatement(stmt)
            is ToekenningStatement -> toekenningStatement(stmt)
            is ZolangStatement -> zolangStatement(stmt)
        }
    }

    private fun handleNewPoint(newPoint: Point): Pair<Boolean, () -> Unit> {
        when (val newTile = glade.tileAt(newPoint)) {
            is ObstakelTile -> {
                cost(VERBRUIK_DUW_OBSTAKEL_SCHADE, "kosten voor botsen / duwen: {cost}")
                return false to {}
            }
            is BonusTile -> {
                return true to {
                    if (!bonusTilesVisited.contains(newPoint)) {
                        bonusTilesVisited.add(newPoint)
                        message("bonus van ${newTile.getBonus()} gepakt.")
                        totalCost -= newTile.getBonus()
                    }
                }
            }
            is DraaiTile -> {
                return true to {}
            }
            is DoelTile -> {
                error("Doel behaald")
                return true to {}
            }
            is BomTile -> {
                return true to {}
            }
            else -> {
                return true to {}
            }
        }
    }

    private fun opdrachtStatement(stmt: OpdrachtStatement) {
//                message(direction.toString() + " " + position.bracketNotation())

        opdrachtCount++
        when (stmt.type) {
            OpdrachtType.STAP_VOORUIT -> {
                val newPoint = position.incInDirection(direction)
                if ((newPoint.x < 0 || newPoint.x >= glade.size) || (newPoint.y < 0 || newPoint.y >= glade.size)) {
                    error("uitzondering: Uit the Glade gelopen! ${newPoint.bracketNotation()}")
                    return
                }
                val handleNewPoint = handleNewPoint(newPoint)
                if (handleNewPoint.first) {
                    position = newPoint
                    cost(VERBRUIK_STAP_VOORUIT, "kosten voor stap vooruit: {cost} naar ${newPoint.bracketNotation()}")
                    handleNewPoint.second()
                }
            }
            OpdrachtType.STAP_ACHTERUIT -> {
                val newPoint = position.incInDirection(direction.inverse())
                if ((newPoint.x < 0 || newPoint.x >= glade.size) || (newPoint.y < 0 || newPoint.y >= glade.size)) {
                    error("uitzondering: Uit the Glade gelopen! ${newPoint.bracketNotation()}")
                    return
                }
                val handleNewPoint = handleNewPoint(newPoint)
                if (handleNewPoint.first) {
                    position = newPoint
                    cost(
                        VERBRUIK_STAP_ACHTERUIT,
                        "kosten voor stap achteruit: {cost} naar ${newPoint.bracketNotation()}"
                    )
                    handleNewPoint.second()
                }
            }
            OpdrachtType.DRAAI_LINKS -> {
                direction = direction.left()
                cost(VERBRUIK_DRAAI_LINKS, "kosten voor het draaien naar links: {cost}")
            }
            OpdrachtType.DRAAI_RECHTS -> {
                direction = direction.right()
                cost(VERBRUIK_DRAAI_RECHTS, "kosten voor het draaien naar rechts: {cost}")
            }
        }
    }

    private fun zolangStatement(stmt: ZolangStatement) {
        while (checkEquality(stmt.equalityExpression)) {
            programmaBlok(stmt.code)
        }
    }

    private fun alsStatement(stmt: AlsStatement) {
        if (checkEquality(stmt.equalityExpression)) {
            programmaBlok(stmt.als)
        }
    }

    private fun alsAndersStatement(stmt: AlsAndersStatement) {
        if (checkEquality(stmt.equalityExpression)) {
            programmaBlok(stmt.als)
        } else {
            programmaBlok(stmt.anders)
        }
    }

    private fun toekenningStatement(stmt: ToekenningStatement) {
        val variable = stmt.varName.value
        if (!variables.containsKey(variable)) {
            error("Variable not defined: $variable")
        } else {
            val reduced = reduceExpressie(stmt.expressie)
            variables[variable] = reduced
        }
    }

    private fun checkEquality(expression: EqualityExpression): Boolean {
        val left: Int = reduceExpressie(expression.left)
        val right: Int = reduceExpressie(expression.right)
        cost(VERBRUIK_VERGELIJKING, "kosten voor het vergelijken: {cost}")
        return when (expression.type) {
            VergelijkingType.EQEQ -> left == right
            VergelijkingType.NOTEQ -> left != right
            VergelijkingType.LT -> left < right
            VergelijkingType.GT -> left > right
        }
    }

    private fun reduceExpressie(expressie: Expressie): Int {
        return when (expressie) {
            is NumberExpressie -> expressie.number.value
            is VarNameExpressie -> {
                val name = expressie.varName.value
                if (!variables.containsKey(name)) {
                    error("Variable $name was not declared")
                    return -1;
                } else {
                    return variables[name]!!
                }
            }
            is CalcExpressie -> {
                val operation: (Int, Int) -> Int = when (expressie.operation) {
                    CalcExpressieType.PLUS -> Int::plus
                    CalcExpressieType.MIN -> Int::minus
                    CalcExpressieType.TIMES -> Int::times
                    CalcExpressieType.SLASH -> Int::floorDiv
                    CalcExpressieType.PERCENT -> Int::mod
                }
                return operation(
                    reduceExpressie(expressie.left),
                    reduceExpressie(expressie.right)
                )
            }
            ZwOogExpressie -> {
                if (!context.hardware.contains(Taal20Hardware.ZWOOG)) {
                    error("Geen zwOog")
                    return -1
                }
                cost(VERBRUIK_ZW_OOG, "kosten voor gebruik zwart-wit-oog: {cost}")
                val currentTile = glade.tileAt(position)
                return if (currentTile.tileColor!! == TileColor.ZWART) 0 else 1
            }
            KompasExpressie -> {
                if (!context.hardware.contains(Taal20Hardware.KOMPAS)) {
                    error("Geen kompas")
                    return -1
                }
//                println("A:" +direction.ordinal)
                cost(VERBRUIK_KOMPAS, "kosten voor gebruik kompas: {cost}")
                return when (direction) {
                    Direction.NOORD -> 0
                    Direction.OOST -> 1
                    Direction.ZUID -> 2
                    Direction.WEST -> 3
                }
            }
            KleurOogExpressie -> {
                if (!context.hardware.contains(Taal20Hardware.KLEUROOG)) {
                    error("Geen kleuroog")
                    return -1
                }
                cost(VERBRUIK_KLEUR_OOG, "kosten voor gebruik kleur-oog: {cost}")
                val currentTile = glade.tileAt(position)
                return when (currentTile.tileColor!!) {
                    TileColor.WIT -> 8
                    TileColor.GRIJS -> 7
                    TileColor.ROOD -> 6
                    TileColor.ORANJE -> 5
                    TileColor.GEEL -> 4
                    TileColor.GROEN -> 3
                    TileColor.BLAUW -> 2
                    TileColor.PAARS -> 1
                    TileColor.ZWART -> 0
                }
            }
        }
    }

    private fun initStatement(stmt: InitStatement) {
        if (error) {
            return
        }
        when (stmt) {
            is GebruikStatement -> gebruikStatement(stmt)
        }
    }

    private fun gebruikStatement(stmt: GebruikStatement) {
        when (stmt.type) {
            is Taal20VarName -> {
                val varName = stmt.type.value
                variables[varName] = null
                cost(HARDWARE_VARIABLE, "kosten voor declaratie variabele '$varName': {cost}")
            }
            is KleurOogConstant -> {
                context.hardware.add(Taal20Hardware.KLEUROOG)
                cost(HARDWARE_KLEUR_OOG, "kosten voor declaratie 'kleurOog': {cost}")
            }
            is KompasConstant -> {
                context.hardware.add(Taal20Hardware.KOMPAS)
                cost(HARDWARE_KOMPAS, "kosten voor declaratie 'kompas': {cost}")
            }
            is ZwOogConstant -> {
                context.hardware.add(Taal20Hardware.ZWOOG)
                cost(HARDWARE_ZW_OOG, "kosten voor declaratie 'zwOog': {cost}")
            }
            else -> throw RuntimeException("Moet niet gebeuren $stmt")
        }
    }

}
