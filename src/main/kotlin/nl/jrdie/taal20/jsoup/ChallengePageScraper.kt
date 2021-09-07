package nl.jrdie.taal20.jsoup

import nl.jrdie.taal20.data.Kostenkaart
import nl.jrdie.taal20.data.KostenkaartField
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ChallengePageScraper(
    private val laravelCookie: String,
    private val challengeName: String,
    private val url: String,
) {

    companion object {
        private val LOAD_MAZE_REGEX = """.*loadMaze\("(.*?)"\).*""".toRegex(RegexOption.DOT_MATCHES_ALL)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun fetchAssignmentData(): ChallengePageScrapeResult {
        val doc = Jsoup.connect(url)
            .cookie("laravel_session", laravelCookie)
            .get()

        val scripts = doc.select("script")

        val loadMaze = scripts
            .mapNotNull { LOAD_MAZE_REGEX.matchEntire(it.data()) }
            .first()
            .groupValues[1]

        val kostenkaart: Kostenkaart = buildMap {
            put(KostenkaartField.HARDWARE_KOMPAS, doc.findInputValueByName("compassHardware"))
            put(KostenkaartField.HARDWARE_ZW_OOG, doc.findInputValueByName("bwEyeHardware"))
            put(KostenkaartField.HARDWARE_KLEUR_OOG, doc.findInputValueByName("colorEyeHardware"))
            put(KostenkaartField.HARDWARE_VARIABLE, doc.findInputValueByName("variableAzHardware"))
            put(KostenkaartField.VERBRUIK_STAP_VOORUIT, doc.findInputValueByName("stepForwardUse"))
            put(KostenkaartField.VERBRUIK_STAP_ACHTERUIT, doc.findInputValueByName("stepBackwardsUse"))
            put(KostenkaartField.VERBRUIK_DRAAI_LINKS, doc.findInputValueByName("turnLeftUse"))
            put(KostenkaartField.VERBRUIK_DRAAI_RECHTS, doc.findInputValueByName("turnRightUse"))
            put(KostenkaartField.VERBRUIK_ZW_OOG, doc.findInputValueByName("bwEyeUse"))
            put(KostenkaartField.VERBRUIK_KLEUR_OOG, doc.findInputValueByName("colorEyeUse"))
            put(KostenkaartField.VERBRUIK_KOMPAS, doc.findInputValueByName("compassUse"))
            put(KostenkaartField.VERBRUIK_DUW_OBSTAKEL_SCHADE, doc.findInputValueByName("pushObstacleUse"))
            put(KostenkaartField.VERBRUIK_TOEWIJZING_A_IS_1, doc.findInputValueByName("assignUse"))
            put(KostenkaartField.VERBRUIK_OPERATIE, doc.findInputValueByName("operationUse"))
            put(KostenkaartField.VERBRUIK_VERGELIJKING, doc.findInputValueByName("equalUse"))
            put(KostenkaartField.SOFTWARE_ZOLANG_LUS, doc.findInputValueByName("whileLoopSoftware"))
            put(KostenkaartField.SOFTWARE_ALS_KEUZE, doc.findInputValueByName("ifChoiceSoftware"))
            put(KostenkaartField.SOFTWARE_OPDRACHT, doc.findInputValueByName("assignmentSoftware"))
            put(KostenkaartField.SOFTWARE_TOEKENNING, doc.findInputValueByName("assignSoftware"))
            put(KostenkaartField.START_KAPITAAL, doc.findInputValueByName("startCredits"))
        }

        return ChallengePageScrapeResult(challengeName, url, kostenkaart, loadMaze)
    }

}

fun Document.findInputValueByName(name: String): Int {
    return this.selectFirst("[name=$name]")!!.attr("value").toInt()
}
