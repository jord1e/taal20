package nl.jrdie.taal20


import java_cup.runtime.Symbol
import nl.jrdie.taal20._parser.Taal20Parser
import nl.jrdie.taal20._parser.Taal20SymbolType
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
    val test = """
gebruik a
gebruik zwOog
gebruik kleurOog

stapVooruit
stapVooruit

zolang zwOog == 1 {
    stapVooruit
}

draaiLinks

a = 0

zolang a < 4 {
    a = a + 1
    stapVooruit
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
        val parseValue = result.value
        println(parseValue)
        println(parseValue.javaClass.name)
//        parser.debug_parse()
        println("===\nGeen syntax error\n===")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}