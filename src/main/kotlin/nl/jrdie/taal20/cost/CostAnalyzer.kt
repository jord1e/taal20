package nl.jrdie.taal20.cost

import nl.jrdie.taal20.ast.*

class CostAnalyzer {

    private var toekenningCount = 0
    private var opdrachtCount = 0
    private var alsCount = 0
    private var zolangCount = 0

    fun analyzeAst(programma: Programma): Taal20CostStatistics {
        // ! InitBlok verbruikt geen budget
        programmaBlok(programma.programmaBlok)

        return Taal20CostStatistics(toekenningCount, opdrachtCount, alsCount, zolangCount)
    }

    private fun programmaBlok(programmaBlok: ProgrammaBlok) {
        programmaBlok
            .statements
            .forEach { programmaStatement(it) }
    }

    private fun programmaStatement(stmt: ProgrammaStatement) {
        when (stmt) {
            is AlsAndersStatement -> {
                programmaBlok(stmt.als)
                programmaBlok(stmt.anders)
                alsCount++;
            }
            is AlsStatement -> {
                programmaBlok(stmt.als)
                alsCount++
            }
            is OpdrachtStatement -> opdrachtCount++
            is ToekenningStatement -> toekenningCount++
            is ZolangStatement -> {
                programmaBlok(stmt.code)
                zolangCount++
            }
        }
    }

}
