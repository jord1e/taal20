package nl.jrdie.taal20.editor

import org.fife.ui.autocomplete.BasicCompletion
import org.fife.ui.autocomplete.CompletionProvider
import org.fife.ui.autocomplete.DefaultCompletionProvider
import org.fife.ui.autocomplete.VariableCompletion
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager

object Taal20SyntaxAreaUtil {

    // https://github.com/bobbylight/RSyntaxTextArea/wiki/Adding-Syntax-Highlighting-for-a-new-Language#plugging-your-tokenmaker-into-an-application¶
    fun register() {
        val atmf = TokenMakerFactory.getDefaultInstance() as AbstractTokenMakerFactory
        atmf.putMapping("text/taal20", "nl.jrdie.taal20.lexer.Taal20RSyntaxAreaLexer")
        FoldParserManager.get().addFoldParserMapping("text/taal20", CurlyFoldParser())
    }

    fun createCompletionProvider(): CompletionProvider {
        val provider = DefaultCompletionProvider()
        provider.addCompletion(BasicCompletion(provider, "stapVooruit"))
        provider.addCompletion(BasicCompletion(provider, "stapAchteruit"))
        provider.addCompletion(BasicCompletion(provider, "draaiLinks"))
        provider.addCompletion(BasicCompletion(provider, "draaiRechts"))
        provider.addCompletion(BasicCompletion(provider, "als"))
        provider.addCompletion(BasicCompletion(provider, "zolang"))
        provider.addCompletion(BasicCompletion(provider, "gebruik"))
        provider.addCompletion(BasicCompletion(provider, "anders"))

        provider.addCompletion(VariableCompletion(provider, "zwOog", "0 | 1"))
        provider.addCompletion(VariableCompletion(provider, "kleurOog", "0..8"))
        provider.addCompletion(VariableCompletion(provider, "kompsa", "0..4"))

        return provider
    }

}