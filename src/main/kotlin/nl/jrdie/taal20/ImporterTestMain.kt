package nl.jrdie.taal20

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.jrdie.taal20._parser.Taal20Parser
import nl.jrdie.taal20.ast.Programma
import nl.jrdie.taal20.glade.import.GladeLoader
import nl.jrdie.taal20.glade.interpreter.Taal20Interpreter
import nl.jrdie.taal20.jsoup.ChallengePageScrapeResult
import nl.jrdie.taal20.lexer.Taal20Lexer
import java.io.File
import java.io.StringReader

object Program {
    val code = """
gebruik kleurOog
zolang 1 == 1 {
stapVooruit
stapVooruit
stapVooruit
stapVooruit
als kleurOog == 5 {
draaiRechts
}
als kleurOog == 1 {
draaiLinks
stapVooruit
}
}
""".trimStart()
}

fun main(args: Array<String>) {
    val gson = Gson()
    val reader = File("zandbox_challenges2.json").reader()
    val challenges: List<ChallengePageScrapeResult> =
        gson.fromJson(reader, object : TypeToken<List<ChallengePageScrapeResult>>() {}.type)

    val maze1 = challenges.first { it.challengeName == "zandbak_03" }

    val glade = GladeLoader.loadMaze(maze1.maze)
    println(glade)
//    println(GladeLoader.printGlade(glade))

    val parser = Taal20Parser(Taal20Lexer(StringReader(Program.code)))
    val result = parser.parse().value as Programma

    val interpreter = Taal20Interpreter(glade, result, maze1.kostenkaart)
    interpreter.interpret()
}
