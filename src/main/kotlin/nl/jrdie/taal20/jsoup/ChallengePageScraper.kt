package nl.jrdie.taal20.jsoup

import org.jsoup.Jsoup

class ChallengePageScraper(
    private val laravelCookie: String,
    private val url: String,
) {

    fun fetchAssignmentData() {
        val doc = Jsoup.connect(url)
            .cookie("laravel_session", laravelCookie)
            .get()
        println(doc)
    }

}
