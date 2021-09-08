package nl.jrdie.taal20.editor

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Token
import org.fife.ui.rsyntaxtextarea.folding.Fold
import org.fife.ui.rsyntaxtextarea.folding.FoldParser
import org.fife.ui.rsyntaxtextarea.folding.FoldType
import java.util.Stack
import javax.swing.text.BadLocationException

// See: https://github.com/bobbylight/RSyntaxTextArea/blob/master/RSyntaxTextArea/src/main/java/org/fife/ui/rsyntaxtextarea/folding/JsonFoldParser.java
class Taal20FoldParser : FoldParser {
    override fun getFolds(textArea: RSyntaxTextArea): List<Fold> {
        val blocks = Stack<Any>()
        val folds: MutableList<Fold> = ArrayList()
        var currentFold: Fold? = null
        val lineCount = textArea.lineCount
        try {
            for (line in 0 until lineCount) {
                var t: Token? = textArea.getTokenListForLine(line)
                while (t != null && t.isPaintable) {
                    if (t.isLeftCurly) {
                        if (currentFold == null) {
                            currentFold = Fold(FoldType.CODE, textArea, t.offset)
                            folds.add(currentFold)
                        } else {
                            currentFold = currentFold.createChild(FoldType.CODE, t.offset)
                        }
                        blocks.push(OBJECT_BLOCK)
                    } else if (t.isRightCurly && popOffTop(blocks, OBJECT_BLOCK)) {
                        if (currentFold != null) {
                            currentFold.endOffset = t.offset
                            val parentFold: Fold? = currentFold.parent
                            //System.out.println("... Adding regular fold at " + t.offset + ", parent==" + parentFold);
                            // Don't add fold markers for single-line blocks
                            if (currentFold.isOnSingleLine) {
                                if (!currentFold.removeFromParent()) {
                                    folds.removeAt(folds.size - 1)
                                }
                            }
                            currentFold = parentFold
                        }
                    }
                    t = t.nextToken
                }
            }
        } catch (ble: BadLocationException) { // Should never happen
            ble.printStackTrace()
        }
        return folds
    }

    companion object {
        private val OBJECT_BLOCK = Any()

        private fun popOffTop(stack: Stack<Any>, value: Any): Boolean {
            if (stack.size > 0 && stack.peek() === value) {
                stack.pop()
                return true
            }
            return false
        }
    }
}
