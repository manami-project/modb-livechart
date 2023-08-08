package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.delay
import java.nio.file.Path
import java.nio.file.Paths

internal fun main() {
    val downloader = LivechartDownloader(LivechartConfig)
    
    runCoroutine {
        downloader.download("9818").writeToFile(resourceFile("file_converter_tests/anime_season/season/fall.html"))
        delay(4000)
        downloader.download("9649").writeToFile(resourceFile("file_converter_tests/anime_season/season/spring.html"))
        delay(4000)
        downloader.download("9627").writeToFile(resourceFile("file_converter_tests/anime_season/season/summer.html"))
        delay(4000)
        downloader.download("10449").writeToFile(resourceFile("file_converter_tests/anime_season/season/undefined.html"))
        delay(4000)
        downloader.download("8230").writeToFile(resourceFile("file_converter_tests/anime_season/season/winter.html"))

        delay(8000)
        downloader.download("11084").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/season_set.html"))
        delay(4000)
        downloader.download("11988").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/season_tba_premiere_not_set.html"))
        delay(4000)
        downloader.download("11758").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/season_tba_premiere_set.html"))

        delay(8000)
        downloader.download("2026").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
        delay(4000)
        downloader.download("5937").writeToFile(resourceFile("file_converter_tests/duration/1_hour_11_min.html"))
        delay(4000)
        downloader.download("986").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
        delay(4000)
        downloader.download("5926").writeToFile(resourceFile("file_converter_tests/duration/2_hours_15_minutes.html"))
        delay(4000)
        downloader.download("6767").writeToFile(resourceFile("file_converter_tests/duration/10_min.html"))
        delay(4000)
        downloader.download("10429").writeToFile(resourceFile("file_converter_tests/duration/30_sec.html"))
        delay(4000)
        downloader.download("11982").writeToFile(resourceFile("file_converter_tests/duration/unknown.html"))

        delay(8000)
        downloader.download("1855").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
        delay(4000)
        downloader.download("6239").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
        delay(4000)
        downloader.download("8817").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
        delay(4000)
        downloader.download("11115").writeToFile(resourceFile("file_converter_tests/episodes/number_of_episodes_known_and_running.html"))
        delay(4000)
        downloader.download("319").writeToFile(resourceFile("file_converter_tests/episodes/number_of_episodes_unknown_but_currently_running.html"))
        delay(4000)
        downloader.download("11873").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))

        delay(8000)
        downloader.download("2301").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
        delay(4000)
        downloader.download("3437").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))

        delay(8000)
        downloader.download("3366").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_no_scrolling.html"))
        delay(4000)
        downloader.download("3607").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_relations_with_scrolling.html"))
        delay(4000)
        downloader.download("9741").writeToFile(resourceFile("file_converter_tests/related_anime/no_relations.html"))
        delay(4000)
        downloader.download("3437").writeToFile(resourceFile("file_converter_tests/related_anime/one_relation.html"))

        delay(8000)
        downloader.download("3437").writeToFile(resourceFile("file_converter_tests/sources/3437.html"))

        delay(4000)
        downloader.download("9818").writeToFile(resourceFile("file_converter_tests/status/finished.html"))
        delay(4000)
        downloader.download("10384").writeToFile(resourceFile("file_converter_tests/status/no_yet_released.html"))
        delay(4000)
        downloader.download("321").writeToFile(resourceFile("file_converter_tests/status/ongoing.html"))

        delay(8000)
        downloader.download("8081").writeToFile(resourceFile("file_converter_tests/synonyms/encoded_special_chars.html"))
        delay(4000)
        downloader.download("2805").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
        delay(4000)
        downloader.download("10484").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
        delay(4000)
        downloader.download("12289").writeToFile(resourceFile("file_converter_tests/synonyms/one_synonym.html"))

        delay(8000)
        downloader.download("10450").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags.html"))
        delay(4000)
        downloader.download("10959").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
        delay(4000)
        downloader.download("2388").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))

        delay(8000)
        downloader.download("7907").writeToFile(resourceFile("file_converter_tests/title/encoded_special_char.html"))
        delay(4000)
        downloader.download("10186").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))

        delay(8000)
        downloader.download("1296").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
        delay(4000)
        downloader.download("3796").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
        delay(4000)
        downloader.download("9548").writeToFile(resourceFile("file_converter_tests/type/special.html"))
        delay(4000)
        downloader.download("3437").writeToFile(resourceFile("file_converter_tests/type/tv.html"))
        delay(4000)
        downloader.download("10429").writeToFile(resourceFile("file_converter_tests/type/tv_short.html"))
        delay(4000)
        downloader.download("11982").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
        delay(4000)
        downloader.download("8110").writeToFile(resourceFile("file_converter_tests/type/web.html"))
        delay(4000)
        downloader.download("8695").writeToFile(resourceFile("file_converter_tests/type/web_short.html"))
    }

    println("done")
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}