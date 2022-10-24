package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = LivechartDownloader(LivechartConfig)
    
    runBlocking {
        downloader.downloadSuspendable("9818").writeToFile(resourceFile("file_converter_tests/anime_season/season/fall.html"))
        downloader.downloadSuspendable("9649").writeToFile(resourceFile("file_converter_tests/anime_season/season/spring.html"))
        downloader.downloadSuspendable("9627").writeToFile(resourceFile("file_converter_tests/anime_season/season/summer.html"))
        downloader.downloadSuspendable("9579").writeToFile(resourceFile("file_converter_tests/anime_season/season/undefined.html"))
        downloader.downloadSuspendable("8230").writeToFile(resourceFile("file_converter_tests/anime_season/season/winter.html"))
    
        downloader.downloadSuspendable("9762").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/exact_date.html"))
        downloader.downloadSuspendable("9855").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/link_is_tba_but_text_contains_year.html"))
        downloader.downloadSuspendable("10510").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/season.html"))
        downloader.downloadSuspendable("10850").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/undefined.html"))
    
        downloader.downloadSuspendable("2026").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
        downloader.downloadSuspendable("5937").writeToFile(resourceFile("file_converter_tests/duration/1_hour_11_min.html"))
        downloader.downloadSuspendable("986").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
        downloader.downloadSuspendable("5926").writeToFile(resourceFile("file_converter_tests/duration/2_hours_15_minutes.html"))
        downloader.downloadSuspendable("6767").writeToFile(resourceFile("file_converter_tests/duration/10_min.html"))
        downloader.downloadSuspendable("10429").writeToFile(resourceFile("file_converter_tests/duration/30_sec.html"))
        downloader.downloadSuspendable("10368").writeToFile(resourceFile("file_converter_tests/duration/unknown.html"))
    
        downloader.downloadSuspendable("1855").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
        downloader.downloadSuspendable("6239").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
        downloader.downloadSuspendable("8817").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
        downloader.downloadSuspendable("321").writeToFile(resourceFile("file_converter_tests/episodes/currently_running.html"))
        downloader.downloadSuspendable("10474").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))
    
        downloader.downloadSuspendable("10877").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
        downloader.downloadSuspendable("3437").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))
    
        downloader.downloadSuspendable("3366").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_no_scrolling.html"))
        downloader.downloadSuspendable("3607").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_with_scrolling.html"))
        downloader.downloadSuspendable("9741").writeToFile(resourceFile("file_converter_tests/related_anime/no_relations.html"))
        downloader.downloadSuspendable("3437").writeToFile(resourceFile("file_converter_tests/related_anime/one_relation.html"))
    
        downloader.downloadSuspendable("3437").writeToFile(resourceFile("file_converter_tests/sources/3437.html"))
    
        downloader.downloadSuspendable("9818").writeToFile(resourceFile("file_converter_tests/status/finished.html"))
        downloader.downloadSuspendable("321").writeToFile(resourceFile("file_converter_tests/status/ongoing.html"))
        downloader.downloadSuspendable("10487").writeToFile(resourceFile("file_converter_tests/status/upcoming_known_date.html"))
        downloader.downloadSuspendable("11018").writeToFile(resourceFile("file_converter_tests/status/upcoming_unknown_date.html"))
    
        downloader.downloadSuspendable("8081").writeToFile(resourceFile("file_converter_tests/synonyms/encoded_special_chars.html"))
        downloader.downloadSuspendable("2805").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
        downloader.downloadSuspendable("10484").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
        downloader.downloadSuspendable("9665").writeToFile(resourceFile("file_converter_tests/synonyms/one_synonym.html"))
    
        downloader.downloadSuspendable("10450").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags.html"))
        downloader.downloadSuspendable("10959").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
        downloader.downloadSuspendable("2388").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))
    
        downloader.downloadSuspendable("7907").writeToFile(resourceFile("file_converter_tests/title/encoded_special_char.html"))
        downloader.downloadSuspendable("10186").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))
    
        downloader.downloadSuspendable("1296").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
        downloader.downloadSuspendable("3796").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
        downloader.downloadSuspendable("9548").writeToFile(resourceFile("file_converter_tests/type/special.html"))
        downloader.downloadSuspendable("3437").writeToFile(resourceFile("file_converter_tests/type/tv.html"))
        downloader.downloadSuspendable("10429").writeToFile(resourceFile("file_converter_tests/type/tv_short.html"))
        downloader.downloadSuspendable("10941").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
        downloader.downloadSuspendable("8110").writeToFile(resourceFile("file_converter_tests/type/web.html"))
        downloader.downloadSuspendable("8695").writeToFile(resourceFile("file_converter_tests/type/web_short.html"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}