package nl.jrdie.taal20


import java_cup.runtime.Symbol
import nl.jrdie.taal20._parser.Taal20Parser
import nl.jrdie.taal20._parser.Taal20SymbolType
import nl.jrdie.taal20.ast.Programma
import nl.jrdie.taal20.ast.printer.Taal20AstPrinter
import nl.jrdie.taal20.lexer.Taal20Lexer
import java.io.StringReader

fun main(args: Array<String>) {

    val test1 = """
gebruik kleurOog
gebruik zwOog
gebruik kompas
""".trimStart()
    val test2 = """
stapVooruit
stapVooruit
stapVooruit
stapVooruit
draaiRechts
stapVooruit
stapVooruit
stapVooruit
stapVooruit
draaiLinks
stapVooruit
stapVooruit
stapVooruit
stapVooruit
stapVooruit
draaiLinks
stapVooruit
stapVooruit
stapVooruit
stapVooruit
stapVooruit
draaiRechts
als kleurOog == 5 {
   draaiRechts
}
stapVooruit
gebruik zwOog
""".trimStart()
    val test3 = """
als zwOog {
   draaiRechts
stapVooruit
}
""".trimStart()
//    val test6 = """
//gebruik zwOog
//gebruik kleurOog
//gebruik zwOog
//gebruik kompas
//
//a = 1
//als kleurOog == 3 {
//stapVooruit
//stapVooruit
//
//
//
//stapVooruit
//draaiRechts
//}
//
//stapVooruit
//stapVooruit
//draaiRechts
//zolang kompas % 2 == 1 {
//
//draaiLinks
//}
//""".trimStart()
    val test6 = """
gebruik a
gebruik zwOog
gebruik kleurOog

stapVooruit
stapVooruit

zolang zwOog == 1 {
    stapVooruit
}
als kleurOog == 5 {
   draaiRechts
} anders {
 draaiLinks
}
draaiLinks

a = 0

zolang a < 4 {
    a = a + 1
    stapVooruit
}
""".trimStart()

    val test = """
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

    val lexer = Taal20Lexer(StringReader(test))

    if (true) {
        var token: Symbol?
        do {
            token = lexer.next_token();
            println("" + token + " " + Taal20SymbolType.terminalNames[token.sym])
        } while (token?.sym != Taal20SymbolType.EOF)
    }

    val lexer2 = Taal20Lexer(StringReader(test))
    try {
        val parser = Taal20Parser(lexer2)
        val result = parser.parse()
        val parseValue = result.value as Programma
        println(parseValue)
        println(parseValue.javaClass.name)
//        parser.debug_parse()
        println("===\n> Geen syntax errors gevonden :D\n")
        val printer = Taal20AstPrinter()
        val ast = printer.printProgramma(parseValue)
        println("=== Abstract Syntax Tree (reconstructed)")
        println(ast)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}