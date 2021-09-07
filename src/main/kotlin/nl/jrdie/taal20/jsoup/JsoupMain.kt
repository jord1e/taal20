package nl.jrdie.taal20.jsoup

fun main(args: Array<String>) {

    val laravelCookie = "eyJpdiI6InZmRkVcL1d0MXhIcjBodTM0WERISlNnPT0iLCJ2YWx1ZSI6IiswMlwvNloxU2ZJVW5FY3lLQnV2UXkrQVd5bmhVWjdnSlJkdHRqQnNGWElteXNGNEdWXC9DQVlaZXNxQzhNaHhwR1ZuVlMzaXhlcjZUdFgyV3J5Z1hjS2c9PSIsIm1hYyI6Ijg5OGM3YjZkYWNlZWRiNjk1OTY3ODJhMjE2OGJkYzM2MWFjMzA4MGE2NDdmYjNlYzU5ZmM4YjAwNWNmYTIwNTUifQ%3D%3D"
    val challengeScraper = ChallengeScraper(laravelCookie);
    val challenges = challengeScraper.scrapeChallenges()
    challenges.forEach { println(it) }

    val zandbak01Url = "https://amazing.hbo-ict.org/assignments/create/1"
    val challengePageScraper = ChallengePageScraper(laravelCookie, zandbak01Url)
    challengePageScraper.fetchAssignmentData()
//    val zandbak01 =
}
