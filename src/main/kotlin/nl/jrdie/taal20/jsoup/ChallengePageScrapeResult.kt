package nl.jrdie.taal20.jsoup

import nl.jrdie.taal20.data.Kostenkaart

data class ChallengePageScrapeResult(
    val challengeName: String,
    val url: String,
    val kostenkaart: Kostenkaart,
    val maze: String,
) {
}
