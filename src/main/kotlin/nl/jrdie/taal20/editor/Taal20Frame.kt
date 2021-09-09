package nl.jrdie.taal20.editor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.jrdie.taal20._parser.Taal20Parser
import nl.jrdie.taal20.ast.Programma
import nl.jrdie.taal20.data.KostenkaartField
import nl.jrdie.taal20.glade.Glade
import nl.jrdie.taal20.glade.import.GladeLoader
import nl.jrdie.taal20.glade.interpreter.Taal20Interpreter
import nl.jrdie.taal20.glade.models.Direction
import nl.jrdie.taal20.jsoup.ChallengePageScrapeResult
import nl.jrdie.taal20.jsoup.ChallengePageScraper
import nl.jrdie.taal20.jsoup.ChallengeScraper
import nl.jrdie.taal20.lexer.Taal20Lexer
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.io.StringReader
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import javax.swing.*
import javax.swing.text.DefaultCaret

class Taal20Frame : JFrame() {

    var taal20GladePanel: Taal20GladePanel
    var statusLabel: JLabel
    var levelMenu: JMenu
    var levels: List<ChallengePageScrapeResult>
    var currentLevel: ChallengePageScrapeResult? = null
    var currentGlade: Glade? = null
    var laravelCookie: String? = null
    var currentInterpreter: Taal20Interpreter? = null;

    init {
        title = "Taal20 IDE"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        size = Dimension(1400, 750)
        layout = BorderLayout()
        levelMenu = JMenu("Levels (0)")

        levels = emptyList()

        val innerTest = JPanel(FlowLayout())
        innerTest.apply {
            preferredSize = Dimension(660, 660)
            maximumSize = Dimension(660, 660)
            size = Dimension(660, 660)
            minimumSize = Dimension(660, 660)
        }
        taal20GladePanel = Taal20GladePanel()
        innerTest.add(taal20GladePanel, BorderLayout.CENTER)
//        taal20GladePanel.render()
        val codePanel = JPanel(BorderLayout()).apply {
            preferredSize = Dimension(360, 640)
        }

        Taal20SyntaxAreaUtil.register()
        val codeEditorArea = RSyntaxTextArea(20, 20)
        codeEditorArea.isCodeFoldingEnabled = true
        codeEditorArea.text = "stapVooruit\n"
        codeEditorArea.syntaxEditingStyle = "text/taal20"
        AutoCompletion(Taal20SyntaxAreaUtil.createCompletionProvider()).install(codeEditorArea)

        statusLabel = JLabel("Type je code!")

        val messages = mutableListOf<String>()
        val console = JTextArea()
        console.isEditable = false
        (console.caret as DefaultCaret).updatePolicy = DefaultCaret.ALWAYS_UPDATE
        val consolePane = JScrollPane(console)
        consolePane.preferredSize = Dimension(300, 750)

        val runButton = JButton("Compile & Run")
        runButton.mnemonic = KeyEvent.VK_F5
        runButton.addActionListener {
            val lexer = Taal20Lexer(StringReader(codeEditorArea.text))

            val astStructure: Programma
            try {
                @Suppress("DEPRECATION")
                val parser = Taal20Parser(lexer)
                astStructure = parser.parse().value as Programma
            } catch (e: Exception) {
                statusLabel.text =
                LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + e.message
                e.printStackTrace()
                JOptionPane.showMessageDialog(this, "Compilatiefout: ${e.message}", "Foutmelding", JOptionPane.ERROR_MESSAGE)

                return@addActionListener
            }
            if (currentLevel == null || currentGlade == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Geen level (en/of glade) geselecteerd",
                    "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
                return@addActionListener
            }
            if (currentInterpreter?.finished == false) {
                JOptionPane.showMessageDialog(
                    this,
                    "Interpreter nog niet voltooid",
                    "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
                return@addActionListener
            }
            messages.clear()
            console.text = ""
            Thread() {
                currentInterpreter = Taal20Interpreter(currentGlade!!, astStructure, currentLevel!!.kostenkaart, {
                    messages.add(it)
                    console.text = messages.joinToString("\n")
                }) {
                    SwingUtilities.invokeAndWait {
                        renderInterpreterState(it)
                        Thread.sleep(250)
                    }
                }
                currentInterpreter!!.interpret()
            }.start()
        }

        val verifyButton = JButton("Verify")
        verifyButton.mnemonic = KeyEvent.VK_F3
        verifyButton.addActionListener {
            try {
                val lexer = Taal20Lexer(StringReader(codeEditorArea.text))

                @Suppress("DEPRECATION")
                val parser = Taal20Parser(lexer)
                parser.parse()
            } catch (e: Exception) {
                statusLabel.text =
                    LocalTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME) + " " + e.message
                e.printStackTrace()
                JOptionPane.showMessageDialog(this, "Compilatiefout: ${e.message}", "Foutmelding", JOptionPane.ERROR_MESSAGE)
            }
        }

        val importItem = JMenuItem("Import JSON")
        importItem.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.isMultiSelectionEnabled = false
            val fileState = fileChooser.showOpenDialog(this)
            if (fileState == JFileChooser.APPROVE_OPTION) {
                val jsonFile = fileChooser.selectedFile.reader()
                val data = Gson().fromJson<List<ChallengePageScrapeResult>>(
                    jsonFile,
                    object : TypeToken<List<ChallengePageScrapeResult>>() {}.type
                )
                levels = (levels.filter { level -> !data.any { it.challengeName == level.challengeName } } + data)
                    .distinctBy { it.challengeName }
                levelMenu.text = "Levels (${levels.size})"
            }
        }

        val selectLevelItem = JMenuItem("Selecteer level")
        selectLevelItem.addActionListener {
            if (levels.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Er zijn nog geen levels geïmporteerd",
                    "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
                return@addActionListener
            }
            val levelName: String = JOptionPane.showInputDialog(
                this, "Kies een level", "Level kiezen (${levels.size} levels)",
                JOptionPane.QUESTION_MESSAGE, null, levels.map { it.challengeName }.toTypedArray(), null
            ) as String? ?: return@addActionListener
            val level = levels.find { it.challengeName == levelName }!!
            currentLevel = level
            try {
                currentGlade = GladeLoader.loadMaze(level.maze)
                taal20GladePanel.renderGrid(currentGlade!!)
                title = "Taal20 IDE (${levelName})"
            } catch (e: Exception) {
                e.printStackTrace()
                JOptionPane.showMessageDialog(
                    this, "Kon glade niet laden: ${e.message}", "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }

        val laravelCookieMenu = JMenuItem("Laravel Cookie")
        laravelCookieMenu.addActionListener {
            val cookie: String? = JOptionPane.showInputDialog(
                this,
                "Kopieer de laravel_session cookie",
                "Laravel cookie",
                JOptionPane.QUESTION_MESSAGE
            )
            laravelCookie = cookie
        }

        val scrapeAssignments = JMenuItem("Scrape assignments")
        scrapeAssignments.addActionListener {
            if (laravelCookie == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Ongeldige Laravel cookie",
                    "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
                return@addActionListener
            }
            CompletableFuture.supplyAsync {
                val challenges = ChallengeScraper(laravelCookie!!).scrapeChallenges()
                challenges.map {
                    ChallengePageScraper(laravelCookie!!, it.groupName, it.assignmentUrl)
                        .fetchAssignmentData()
                }
            }.thenApplyAsync({
                JOptionPane.showMessageDialog(
                    this,
                    "Er zijn ${it.size} challenges gescrapet.\nSelecteer een opslaglocatie",
                    "Scrape resultaat",
                    JOptionPane.INFORMATION_MESSAGE
                )
                val fileChooser = JFileChooser()
                val saveResult = fileChooser.showSaveDialog(this)
                if (saveResult != JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Opslaan geannuleerd",
                        "Waarschuwing",
                        JOptionPane.WARNING_MESSAGE
                    )
                    return@thenApplyAsync
                }
                if (!fileChooser.selectedFile.exists()) {
                    fileChooser.selectedFile.createNewFile()
                }
                val json = Gson().toJson(it)
                fileChooser.selectedFile.writeText(json)
                JOptionPane.showMessageDialog(
                    this,
                    "De ${it.size} challenges zijn opgeslagen in\n${fileChooser.selectedFile.absolutePath}",
                    "Opslaan voltooid",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }, SwingUtilities::invokeAndWait).exceptionallyAsync({
                it.printStackTrace()
                JOptionPane.showMessageDialog(
                    this,
                    "Fout tijdens het scrapen:\n${it.message}\nMogelijk is de Laravel cookie ongeldig",
                    "Foutmelding",
                    JOptionPane.ERROR_MESSAGE
                )
            }, SwingUtilities::invokeAndWait)
        }

        val kostenKaartBtn = JButton("Kostenkaart")
        kostenKaartBtn.addActionListener {
            if (currentLevel == null) {
                JOptionPane.showMessageDialog(this, "Geen level geselecteerd", "Foutmelding", JOptionPane.ERROR_MESSAGE)
                return@addActionListener
            }
            val dialog = JDialog(this, "Kostenkaart (${currentLevel!!.challengeName})", true)
            dialog.add(KostenkaartPanel(currentLevel!!.kostenkaart))
            dialog.apply {
                size = Dimension(625, 450)
                preferredSize = Dimension(625, 450)
                isResizable = false
            }
            dialog.isVisible = true
        }

        val stopButton = JButton("Stop")
        stopButton.addActionListener {
            currentInterpreter?.stop("Manual stop via button")
        }

        val scrapeMenu = JMenu("Scraper")
        scrapeMenu.add(laravelCookieMenu)
        scrapeMenu.add(scrapeAssignments)

        levelMenu.add(importItem)
        levelMenu.add(JSeparator())
        levelMenu.add(selectLevelItem)
        val menuBar = JMenuBar()
        menuBar.add(levelMenu)
        menuBar.add(scrapeMenu)
        jMenuBar = menuBar

        val buttonPanel = JPanel(GridLayout(3, 2))
        buttonPanel.add(verifyButton)
        buttonPanel.add(runButton)
        buttonPanel.add(kostenKaartBtn)
        buttonPanel.add(stopButton)
        codePanel.add(statusLabel, BorderLayout.NORTH)
        codePanel.add(RTextScrollPane(codeEditorArea), BorderLayout.CENTER)
        codePanel.add(buttonPanel, BorderLayout.SOUTH)
        add(codePanel, BorderLayout.EAST)
        add(innerTest, BorderLayout.CENTER)
        add(consolePane, BorderLayout.WEST)
    }

    fun renderInterpreterState(interpreter: Taal20Interpreter) {
        fun directionToString(direction: Direction): String {
            return when (direction) {
                Direction.NOORD -> "Noord (↑)"
                Direction.OOST -> "Oost (→)"
                Direction.ZUID -> "Zuid (↓)"
                Direction.WEST -> "West (←)"
            }
        }

        taal20GladePanel.renderInterpreterState(interpreter)
        statusLabel.text =
            "Kapitaal: ${interpreter.kostenkaart[KostenkaartField.START_KAPITAAL]!! - interpreter.totalCost}, Richting: ${
                directionToString(interpreter.direction)
            }"
    }

}
