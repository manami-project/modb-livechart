package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.SECONDS
import io.github.manamiproject.modb.core.parseHtml
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import org.jsoup.nodes.Document
import java.net.URI

/**
 * Converts raw data to an [Anime].
 * Requires raw HTML. -> test
 * @since 1.0.0
 * @param config Configuration for converting data.
 */
public class LivechartConverter(
    private val config: MetaDataProviderConfig = LivechartConfig,
): AnimeConverter {

    @Deprecated("Use coroutines", ReplaceWith(EMPTY))
    override fun convert(rawContent: String): Anime = runBlocking{
        convertSuspendable(rawContent)
    }

    override suspend fun convertSuspendable(rawContent: String): Anime = withContext(LIMITED_CPU) {
        val htmlDocument = parseHtml(rawContent)
        val rawJson = htmlDocument.select("script[type=application/ld+json]").first()!!.data().trim()
        val jsonData = LivechartData(Json.parseJsonSuspendable<LivechartParsedData>(rawJson)!!)

        val picture = extractPicture(jsonData, htmlDocument)
        val sources = extractSourcesEntry(jsonData, htmlDocument)

        return@withContext Anime(
            _title = extractTitle(jsonData, htmlDocument),
            episodes = extractEpisodes(jsonData, htmlDocument),
            type = extractType(htmlDocument),
            picture = picture,
            thumbnail = findThumbnail(picture),
            status = extractStatus(htmlDocument),
            duration = extractDuration(htmlDocument),
            animeSeason = extractAnimeSeason(htmlDocument)
        ).apply {
            addSources(sources)
            addSynonyms(extractSynonyms(jsonData))
            addRelations(extractRelatedAnime(htmlDocument))
            addTags(extractTags(jsonData, htmlDocument))
        }
    }

    private fun extractTitle(jsonData: LivechartData, document: Document): Title {
        val extractedTitle = jsonData.name.ifBlank {
            document.select("meta[property=og:title]").attr("content").trim()
        }

        return StringEscapeUtils.unescapeHtml4(extractedTitle)
    }

    private fun extractEpisodes(jsonData: LivechartData, document: Document): Episodes {
        // JSON data
        var episodes = jsonData.numberOfEpisodes
        var isTableValueUnknown = false

        // regular value in data table
        if (episodes == 0) {
            val episodesValue = document.select("div[class=info-bar anime-meta-bar]")
                .select("div:matchesOwn(Episodes)")
                .next()
                .text()
                .trim()

            if (episodesValue != "?") {
                episodes = episodesValue.toIntOrNull() ?: 0
            } else {
                isTableValueUnknown = true
            }
        }

        // current episode from table if the anime is ongoing
        if (episodes == 0) {
            episodes = document.select("div[class=callout info-bar countdown-bar inactive]")
                .select("div[class=info-bar-cell info-bar-label]")
                .text()
                .replace("EP", EMPTY)
                .trim()
                .toIntOrNull()
                ?: 0
        }

        return when {
            episodes == 0 && isTableValueUnknown -> 0
            episodes == 0 && !isTableValueUnknown -> 1
            else -> episodes
        }
    }

    private fun extractType(document: Document): Anime.Type {
        val value = document.select("div[class=info-bar anime-meta-bar]")
            .select("div:matchesOwn(Format)")
            .next()
            .text()
            .trim()

        return when(value) {
            "Movie" -> MOVIE
            "OVA" -> OVA
            "Special" -> SPECIAL
            "TV" -> TV
            "TV Short" -> TV
            "TV Special" -> SPECIAL
            "Web" -> ONA
            "Web Short" -> ONA
            "?" -> UNKNOWN
            "Unknown" -> UNKNOWN
            else -> throw IllegalStateException("Unknown type [$value]")
        }
    }

    private fun extractPicture(jsonData: LivechartData, document: Document): URI {
        var value = jsonData.image.ifBlank {
            document.select("meta[property=og:image]").attr("content").trim()
        }

        if (!value.contains(LARGE_PICTURE_INDICATOR)) {
            value = NO_PIC
        }

        return URI(value)
    }

    private fun findThumbnail(uri: URI): URI {
        val value = uri.toString().replace(LARGE_PICTURE_INDICATOR, SMALL_PICTURE_INDICATOR)

        return URI(value)
    }

    private fun extractStatus(document: Document): Anime.Status {
        val episodesFromCountdownBox = document.select("div[class=callout info-bar countdown-bar inactive]")
            .select("div[class=info-bar-cell info-bar-label]")
            .text()
            .replace("EP", EMPTY)
            .trim()
            .toIntOrNull()
            ?: 0

        val isPremiereTba = document.select("div[class=section-heading]:matchesOwn(Premiere)")
            .next()
            .select("a")
            .attr("href")
            .contains("tba")

        return when {
            isPremiereTba && episodesFromCountdownBox == 0 -> UPCOMING
            episodesFromCountdownBox == 1 -> UPCOMING
            episodesFromCountdownBox > 1 -> ONGOING
            !isPremiereTba && episodesFromCountdownBox == 0 -> FINISHED
            else -> Anime.Status.UNKNOWN
        }
    }

    private fun extractDuration(document: Document): Duration {
        val value = document.select("div[class=info-bar anime-meta-bar]")
            .select("div:matchesOwn(Run time)")
            .next()
            .text()
            .trim()

        var seconds = 0
        Regex("([0-9]+ [aA-zZ]+)+")
            .findAll(value)
            .map { it.value }
            .map { it.split(' ') }
            .map { it[0].trim().toInt() to it[1].trim() }
            .forEach {
                seconds += when(it.second) {
                    "hr" -> it.first * 3600
                    "min" -> it.first * 60
                    "sec" -> it.first
                    else -> throw IllegalStateException("Unknown unit [${it.second}]")
                }
            }

        return Duration(seconds, SECONDS)
    }

    private fun extractAnimeSeason(document: Document): AnimeSeason {
        val linkContainingPremiere = document.select("div[class=section-heading]:matchesOwn(Premiere)")
            .next()
            .select("a")

        val identifier = Regex("[aA-zZ]+-[0-9]{4}")
        val premiere = linkContainingPremiere.attr("href")
            .split('/')
            .find { identifier.matches(it) }
            ?.split('-')
            ?.let { it[0].lowercase() to it[1] }
            ?: (EMPTY to EMPTY)

        val season = when(premiere.first) {
            "winter" -> WINTER
            "spring" -> SPRING
            "summer" -> SUMMER
            "fall" -> FALL
            else -> UNDEFINED
        }

        var year = premiere.second.ifBlank { "0" }.toInt()

        if (year == 0) {
            val premiereText = linkContainingPremiere.text().trim()
            val isYear = premiereText.matches(Regex("[0-9]{4}"))

            if(isYear) {
                year =  premiereText.toInt()
            }
        }

        return AnimeSeason(
            season = season,
            year = year,
        )
    }

    private fun extractSourcesEntry(jsonData: LivechartData, document: Document): Collection<URI> {
        val id = document.select("div[data-anime-details-id]").attr("data-anime-details-id").trim()
        var source = config.buildAnimeLink(id)

        if (id.isBlank()) {
            val link = jsonData.url.replace("www.", EMPTY).ifBlank {
                document.select("meta[property=og:url]").attr("content").trim().replace("www.", EMPTY)
            }
            source = URI(link)
        }

        return setOf(source)
    }

    private fun extractSynonyms(jsonData: LivechartData): Collection<Title> {
        return jsonData.alternateName.map { StringEscapeUtils.unescapeHtml4(it) }.toSet()
    }

    private fun extractRelatedAnime(document: Document): Collection<URI> {
        return document.select("article[class=compact-anime-card] > a")
            .map { it.attr("href") }
            .map { it.replace("/anime/", EMPTY) }
            .map { config.buildAnimeLink(it) }
    }

    private fun extractTags(jsonData: LivechartData, document: Document): Collection<Tag> {
        val tags: MutableSet<String> = jsonData.genre.toMutableSet().ifEmpty { mutableSetOf() }

        if (tags.isEmpty()) {
            document.select("div[class=section-heading]")
                .select("div:matchesOwn(Tags)")
                .next()
                .select("li > a")
                .textNodes()
                .map { it.text() }
                .forEach {
                    tags.add(it)
                }
        }

        return tags
    }

    private companion object {
        private const val LARGE_PICTURE_INDICATOR = "style=large"
        private const val SMALL_PICTURE_INDICATOR = "style=small"
        private const val NO_PIC = "https://cdn.myanimelist.net/images/qm_50.gif"
    }
}

private data class LivechartParsedData(
    val url: String? = null,
    val genre: List<String>? = null,
    val name: String? = null,
    val image: String? = null,
    val numberOfEpisodes: Int? = null,
    val datePublished: String? = null,
    val alternateName: List<String>? = null,
)

private data class LivechartData(
    val livechartParsedData: LivechartParsedData
) {
    val url: String
        get() = livechartParsedData.url?.trim() ?: EMPTY

    val genre: Set<String>
        get() = livechartParsedData.genre?.map { it.trim() }?.toSet() ?: emptySet()

    val name: String
        get() = livechartParsedData.name?.trim() ?: EMPTY

    val image: String
        get() = livechartParsedData.image?.trim() ?: EMPTY

    val numberOfEpisodes: Int
        get() = livechartParsedData.numberOfEpisodes ?: 0

    val datePublished: String
        get() = livechartParsedData.datePublished?.trim() ?: EMPTY

    val alternateName: Set<String>
        get() = livechartParsedData.alternateName?.map { it.trim() }?.toSet() ?: emptySet()
}