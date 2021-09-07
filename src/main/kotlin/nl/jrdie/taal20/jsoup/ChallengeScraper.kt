package nl.jrdie.taal20.jsoup

import org.jsoup.Jsoup

class ChallengeScraper(
    private val laravelCookie: String,
) {

    fun scrapeChallenges(): List<ChallengeEntry> {
        val doc = Jsoup.connect("https://amazing.hbo-ict.org/assignments")
            .cookie("laravel_session", laravelCookie)
            .get()

        // Overzicht openstaande opdrachten
        val challenges = doc
            .selectFirst(".table.table-condensed.table-hover")!!
            .selectFirst("tbody")!!
            .select("tr")
            .asSequence()
            .drop(1) // <tr><th>Groep naam</th><th>Opties</th></tr>
            .map { // <td>zandbak_48</td><td><a href="...">Maak opdracht</a></td>
                val links = it.select("td").select("a[href]")
                ChallengeEntry(
                    it.child(0).text(),
                    links.first()!!.attr("href")
                )
            }
        return challenges.toList()
    }

}
