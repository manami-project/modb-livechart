package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = LivechartDownloader(LivechartConfig)

    downloader.download("9818").writeToFile(resourceFile("file_converter_tests/anime_season/season/fall.html"))
    downloader.download("9649").writeToFile(resourceFile("file_converter_tests/anime_season/season/spring.html"))
    downloader.download("9627").writeToFile(resourceFile("file_converter_tests/anime_season/season/summer.html"))
    downloader.download("9579").writeToFile(resourceFile("file_converter_tests/anime_season/season/undefined.html"))
    downloader.download("8230").writeToFile(resourceFile("file_converter_tests/anime_season/season/winter.html"))

    downloader.download("9762").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/exact_date.html"))
    downloader.download("10420").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/link_is_tba_but_text_contains_year.html"))
    downloader.download("10510").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/season.html"))
    downloader.download("10450").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/undefined.html"))

    downloader.download("2026").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
    downloader.download("5937").writeToFile(resourceFile("file_converter_tests/duration/1_hour_11_min.html"))
    downloader.download("986").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
    downloader.download("5926").writeToFile(resourceFile("file_converter_tests/duration/2_hours_15_minutes.html"))
    downloader.download("6767").writeToFile(resourceFile("file_converter_tests/duration/10_min.html"))
    downloader.download("10429").writeToFile(resourceFile("file_converter_tests/duration/30_sec.html"))
    downloader.download("10368").writeToFile(resourceFile("file_converter_tests/duration/unknown.html"))

    downloader.download("1855").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
    downloader.download("6239").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
    downloader.download("8817").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
    downloader.download("321").writeToFile(resourceFile("file_converter_tests/episodes/currently_running.html"))
    downloader.download("10474").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))

    downloader.download("3023").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
    downloader.download("3437").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))

    downloader.download("3581").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_no_scrolling.html"))
    downloader.download("3607").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_with_scrolling.html"))
    downloader.download("9741").writeToFile(resourceFile("file_converter_tests/related_anime/no_relations.html"))
    downloader.download("3437").writeToFile(resourceFile("file_converter_tests/related_anime/one_relation.html"))

    downloader.download("3437").writeToFile(resourceFile("file_converter_tests/sources/3437.html"))

    downloader.download("9818").writeToFile(resourceFile("file_converter_tests/status/finished.html"))
    downloader.download("9762").writeToFile(resourceFile("file_converter_tests/status/ongoing.html"))
    downloader.download("10391").writeToFile(resourceFile("file_converter_tests/status/upcoming_known_date.html"))
    downloader.download("10420").writeToFile(resourceFile("file_converter_tests/status/upcoming_unknown_date.html"))

    downloader.download("8081").writeToFile(resourceFile("file_converter_tests/synonyms/encoded_special_chars.html"))
    downloader.download("2805").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
    downloader.download("10209").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
    downloader.download("9665").writeToFile(resourceFile("file_converter_tests/synonyms/one_synonym.html"))

    downloader.download("10450").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags.html"))
    downloader.download("10596").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
    downloader.download("2388").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))

    downloader.download("7907").writeToFile(resourceFile("file_converter_tests/title/encoded_special_char.html"))
    downloader.download("10186").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))

    downloader.download("1296").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
    downloader.download("3796").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
    downloader.download("9548").writeToFile(resourceFile("file_converter_tests/type/special.html"))
    downloader.download("3437").writeToFile(resourceFile("file_converter_tests/type/tv.html"))
    downloader.download("10429").writeToFile(resourceFile("file_converter_tests/type/tv_short.html"))
    downloader.download("10151").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
    downloader.download("8110").writeToFile(resourceFile("file_converter_tests/type/web.html"))
    downloader.download("8695").writeToFile(resourceFile("file_converter_tests/type/web_short.html"))
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}