package nl.jrdie.taal20.jsoup

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets

fun main(args: Array<String>) {

    val laravelCookie =
        "eyJpdiI6InZmRkVcL1d0MXhIcjBodTM0WERISlNnPT0iLCJ2YWx1ZSI6IiswMlwvNloxU2ZJVW5FY3lLQnV2UXkrQVd5bmhVWjdnSlJkdHRqQnNGWElteXNGNEdWXC9DQVlaZXNxQzhNaHhwR1ZuVlMzaXhlcjZUdFgyV3J5Z1hjS2c9PSIsIm1hYyI6Ijg5OGM3YjZkYWNlZWRiNjk1OTY3ODJhMjE2OGJkYzM2MWFjMzA4MGE2NDdmYjNlYzU5ZmM4YjAwNWNmYTIwNTUifQ%3D%3D"
    val challengeScraper = ChallengeScraper(laravelCookie);
    val challenges = challengeScraper.scrapeChallenges()
//    challenges.forEach { println(it) }

//    val zandbak01Url = "https://amazing.hbo-ict.org/assignments/create/1"
//    val challengePageScraper = ChallengePageScraper(laravelCookie, zandbak01Url)
//    val challengePageScrapeResult = challengePageScraper.fetchAssignmentData()
//    println(challengePageScrapeResult)

//    val challengePages = listOf(challengePageScrapeResult)

    val challengePages = challenges
        .map { ChallengePageScraper(laravelCookie, it.groupName, it.assignmentUrl) }
        .map { it.fetchAssignmentData() }
        .toList()

    val gson = Gson()

    val json = gson.toJson(challengePages)

    println(json)

    File("zandbox_challenges2.json").writeText(json, StandardCharsets.UTF_8)


//    val zandbak01 =
}
