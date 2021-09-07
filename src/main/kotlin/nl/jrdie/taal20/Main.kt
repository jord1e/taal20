package nl.jrdie.taal20


import java_cup.runtime.Symbol
import nl.jrdie.taal20._parser.Taal20Parser
import nl.jrdie.taal20._parser.Taal20SymbolType
import nl.jrdie.taal20.lexer.Taal20Lexer
import java.io.File
import java.io.StringReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

//fun readFileLineByLIne(fileName: String) = File(fileName).readText().replace("(\n|(?:\r\n)|\r|)".toRegex(), "\n")
fun readFileLineByLIne(fileName: String) = File(fileName).readText().replace("\n|(?:\r\n)|\r".toRegex(), "\n")

fun main(args: Array<String>) {
    val codeFile = System.getProperty("user.dir") + "\\src\\main\\kotlin\\nl\\jrdie\\taal20\\code.txt"
    val test4 = readFileLineByLIne(codeFile).trimStart()

    val test1 = """
gebruik kleurOog
gebruik zwOog
gebruik kompas
""".trimStart()

    val lexer = Taal20Lexer(StringReader(test4))

    if (true) {
        var token: Symbol?
        do {
            token = lexer.next_token();
            println("" + token + " " + Taal20SymbolType.terminalNames[token.sym])
        } while (token?.sym != Taal20SymbolType.EOF)
    }

    val lexer2 = Taal20Lexer(StringReader(test4))
    try {
        val parser = Taal20Parser(lexer2)
//        parser.parse()
        parser.debug_parse()
        println("===\nGeen syntax error\n===")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}