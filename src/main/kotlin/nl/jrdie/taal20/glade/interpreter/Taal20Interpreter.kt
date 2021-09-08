package nl.jrdie.taal20.glade.interpreter

import nl.jrdie.taal20.ast.*
import nl.jrdie.taal20.cost.CostAnalyzer
import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.data.KostenkaartField.*
import nl.jrdie.taal20.glade.*
import nl.jrdie.taal20.glade.models.Direction
import nl.jrdie.taal20.glade.models.Point
import java.util.LinkedList

class Taal20Interpreter(
    startGlade: Glade,
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
    var finished = false;
    var bonusTilesVisited = mutableSetOf<Point>()
    var doelen = mutableMapOf<Point, Boolean>()
    var second = 0
    var glade = startGlade
    var bommen = mutableMapOf<Point, Pair<Int, ObstakelTile>>() // Seconde dat die boem gaat en in obstakel veranderd

    private fun cost(field: KostenkaartField, message: String) {
        val cost = kostenkaart[field]!!
        message(message.replace("{cost}", cost.toString()))
        totalCost += cost
        val remainingBudget = kostenkaart[START_KAPITAAL]!! - totalCost
        if (remainingBudget < 0) {
            error("budget op: $remainingBudget")
        }
    }

    // Return value is of game moet doorgaan
    private fun check(): Boolean {
        if (finished || error) {
            return false;
        }
        return true
    }

    private fun nextSecond() {
        second++
    }

    @Throws(RuntimeException::class)
    private fun error(message: String) {
        throw RuntimeException(
            "\n" + messages.joinToString("\n") + "\n" +
                    "=== GLADE INTERPRETER EXCEPTION ===\n" +
                    "$message\n" +
                    "=== --------------------------- ==="
        )
        error = true;
    }

    private fun message(message: String) {
        messages.add(message)
    }

    private fun debug(message: String) {
//        println(message)
    }

    fun interpret() {
        val start = glade.findFirstTileOfType<StartTile>()
        position = start.second
        direction = start.first.startDirection()
        debug("start at ${position.bracketNotation()} heading ${direction.name}")

        val startTiles = glade.findTilesOfType<DoelTile>()
        if (startTiles.size != startTiles.distinctBy { it.first.value }.size) {
            error("Dubbele start tiles van 1 soort $startTiles")
            return
        }
        doelen = startTiles
            .sortedBy { it.first.value }
            .associate { it.second to false }
            .toMutableMap()
        println(doelen)

        message("start kapitaal: " + kostenkaart[START_KAPITAAL])
        val astCost = CostAnalyzer().analyzeAst(programma)
        totalCost += astCost.zolangCount * kostenkaart[SOFTWARE_ZOLANG_LUS]!!
        totalCost += astCost.alsCount * kostenkaart[SOFTWARE_ALS_KEUZE]!!
        totalCost += astCost.opdrachtCount * kostenkaart[SOFTWARE_OPDRACHT]!!
        totalCost += astCost.toekenningCount * kostenkaart[SOFTWARE_TOEKENNING]!!
        message("kosten zolang: ${astCost.zolangCount} * ${kostenkaart[SOFTWARE_ZOLANG_LUS]}")
        message("kosten als: ${astCost.alsCount} * ${kostenkaart[SOFTWARE_ALS_KEUZE]}")
        message("kosten opdracht: ${astCost.opdrachtCount} * ${kostenkaart[SOFTWARE_OPDRACHT]}")
        message("kosten toekenning: ${astCost.toekenningCount} * ${kostenkaart[SOFTWARE_TOEKENNING]}")

        programma
            .initBlok
            .statements
            .forEach { initStatement(it) }

        programmaBlok(programma.programmaBlok)


        val eindKapitaal = kostenkaart[START_KAPITAAL]!! - totalCost
        val last = messages.removeLast()
        messages.addLast("eind kapitaal :$eindKapitaal")
        messages.add(last)
        messages
            .forEach { println(it) }
    }

    private fun programmaBlok(programmaBlok: ProgrammaBlok) {
        if (!check()) {
            return
        }
        programmaBlok
            .statements
            .forEach { programmaStatement(it) }
    }

    private fun programmaStatement(stmt: ProgrammaStatement) {
        if (!check()) {
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
                return true to {
                    val steps = newTile.value
                    val newDirection = when (newTile.value) {
                        0 -> Direction.randomDirection()
                        else -> direction.right(steps)
                    }
                    direction = newDirection
                    message("nieuwe richting na draaien = ${newDirection.ordinal}")
                    debug("> gedraaid naar ${newDirection.name}")
                }
            }
            is DoelTile -> {
                return true to {
//                    doelen.cont { it.second == newPoint }
                    var isReached = true
                    for (entry in doelen) {
                        debug("doel test: $entry")
//                        debug("doel behaald S${newTile.value} at ${newPoint.bracketNotation()}")
                        if (entry.key == newPoint) {
                            if (isReached) {
                                if (doelen[newPoint] == false) {
                                    doelen[newPoint] = true
                                    message("doel [${newPoint.x}, ${newPoint.y}] bereikt.")
                                }
                                if (!doelen.values.contains(false)) {
                                    message("Doel bereikt binnen budget")
                                    finished = true
                                }
                                break
                            }
                        } else {
                            isReached = entry.value
                            if (!isReached) {
                                // Doel hiervoor was nog niet bereikt (als je op D2 stapt en D1 nog niet hebt gehad)
                                break
                            }
                        }
                    }
                }
            }
            is BomTile -> {
                return true to {
                    if (!bommen.containsKey(newPoint)) {
                        bommen[newPoint] = (second + newTile.value) to ObstakelTile(0)
                        message("bom geactiveerd. Tijd = ${newTile.value} sec.")
                    }
                }
            }
            else -> {
                return true to {}
            }
        }
    }

    private fun opdrachtStatement(stmt: OpdrachtStatement) {
//                message(direction.toString() + " " + position.bracketNotation())
        if (!check()) {
            return
        }
        nextSecond()
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
                    cost(VERBRUIK_STAP_VOORUIT, "kosten voor stap vooruit: {cost} naar: ${newPoint.bracketNotation()}")
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
                        "kosten voor stap achteruit: {cost} naar: ${newPoint.bracketNotation()}"
                    )
                    handleNewPoint.second()
                }
            }
            OpdrachtType.DRAAI_LINKS -> {
                direction = direction.left()
                debug("> gedraaid naar ${direction.name} (links)")
                cost(VERBRUIK_DRAAI_LINKS, "kosten voor het draaien naar links: {cost}")
            }
            OpdrachtType.DRAAI_RECHTS -> {
                direction = direction.right()
                debug("> gedraaid naar ${direction.name} (rechts)")
                cost(VERBRUIK_DRAAI_RECHTS, "kosten voor het draaien naar rechts: {cost}")
            }
        }

        if (bommen.isNotEmpty()) {
            for (bom in bommen) {
                if (second >= bom.value.first) {
                    message("boem op :[${bom.key.x}, ${bom.key.y}]")
                    if (position == bom.key) {
                        // Speler dood
                        totalCost = -234352 // TODO Uitrekenen
                        message("Opgeblazen")
                    }
                    bommen.remove(bom.key)
                    glade.matrix[bom.key.x][bom.key.y] = bom.value.second
                }
            }
        }
    }

    private fun zolangStatement(stmt: ZolangStatement) {
        if (!check()) {
            return
        }
        while (checkEquality(stmt.equalityExpression)) {
            if (!check()) {
                return
            }
            programmaBlok(stmt.code)
        }
    }

    private fun alsStatement(stmt: AlsStatement) {
        if (!check()) {
            return
        }
        if (checkEquality(stmt.equalityExpression)) {
            programmaBlok(stmt.als)
        }
    }

    private fun alsAndersStatement(stmt: AlsAndersStatement) {
        if (!check()) {
            return
        }
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
        if (!check()) {
            return false
        }
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
        if (!check()) {
            return
        }
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
