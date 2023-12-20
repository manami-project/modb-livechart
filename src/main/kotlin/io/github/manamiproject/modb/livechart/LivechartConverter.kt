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

    override suspend fun convert(rawContent: String): Anime = withContext(LIMITED_CPU) {
        val htmlDocument = parseHtml(rawContent)
        val rawJson = htmlDocument.select("script[type=application/ld+json]").first()!!.data().trim()
        val jsonData = LivechartData(Json.parseJson<LivechartParsedData>(rawJson)!!)

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
            val episodesValue = document.select("div:matchesOwn(^Episodes\$)").takeIf { it.size > 0 }
                ?.parents()?.get(0)?.takeIf { it.childrenSize() > 1 }
                ?.child(1)
                ?.text()
                ?.trim()
                ?.split('/')
                ?.last()
                ?: "?"

            if (episodesValue != "?" && episodesValue != "-") {
                episodes = episodesValue.toIntOrNull() ?: 0
            } else {
                isTableValueUnknown = true
            }
        }

        // current episode from table if the anime is ongoing
        if (episodes == 0) {
            episodes = document.select("div[data-controller=countdown-bar]")
                .select("div:matchesOwn(EP\\d+)")
                .text()
                .replace("EP", EMPTY)
                .trim()
                .toIntOrNull()
                ?: 0

            if (episodes != 0 ) {
                episodes -= 1
            }
        }

        return when {
            episodes == 0 && isTableValueUnknown -> 1
            else -> episodes
        }
    }

    private fun extractType(document: Document): Anime.Type {
        val value = document.select("div:matchesOwn(^Format\$)")
            .parents()[0]
            .ownText()
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

        if (!value.endsWith(LARGE_PICTURE_INDICATOR)) {
            value = NO_PIC
        }

        return URI(value)
    }

    private fun findThumbnail(uri: URI): URI {
        return if (uri.toString() == NO_PIC) {
            URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic_thumbnail.png")
        } else {
            URI(uri.toString().replace(LARGE_PICTURE_INDICATOR, SMALL_PICTURE_INDICATOR))
        }
    }

    private fun extractStatus(document: Document): Anime.Status {
        val statusString = document.select("div:matchesOwn(^Status\$)")
            .parents()[0]
            .ownText()
            .trim()
            .lowercase()

        return when (statusString){
            "not yet released" -> UPCOMING
            "releasing" -> ONGOING
            "finished" -> FINISHED
            else -> Anime.Status.UNKNOWN
        }
    }

    private fun extractDuration(document: Document): Duration {
        val durationString = document.select("div:matchesOwn(^Run time\$)")
            .parents()[0]
            .ownText()
            .trim()

        val seconds = Regex("([0-9]+ ?[aA-zZ]+)+")
            .findAll(durationString)
            .map { it.value }
            .map {
                val value = (Regex("[0-9]+").find(it)?.value?.trim() ?: "0").ifBlank { "0" }.toIntOrNull() ?: 0
                val unit = Regex("[a-z]+").find(it)?.value?.trim()?.lowercase() ?: ""
                value to unit
            }
            .map {
                when(it.second) {
                    "hr", "h" -> it.first * 3600
                    "min", "m" -> it.first * 60
                    "sec", "s" -> it.first
                    else -> throw IllegalStateException("Unknown unit [${it.second}]")
                }
            }
            .sum()

        return Duration(seconds, SECONDS)
    }

    private fun extractAnimeSeason(document: Document): AnimeSeason {
        val splitSeasonString = document.select("div:matchesOwn(^Season\$)")
            .parents()[0]
            .select("a")
            .text()
            .split(' ')

        val seasonString = splitSeasonString.first().trim().lowercase()

        val season = when(seasonString) {
            "winter" -> WINTER
            "spring" -> SPRING
            "summer" -> SUMMER
            "fall" -> FALL
            else -> UNDEFINED
        }

        val year = if (splitSeasonString.size == 2) {
            splitSeasonString[1].trim().ifBlank { "0" }.toIntOrNull() ?: 0
        } else {
            val linkContainingPremiere = document.select("div[class=section-heading]:matchesOwn(Premiere)")
                .next()
                .text()
            (Regex("[0-9]{4}").find(linkContainingPremiere)?.value?.trim() ?: "0").ifBlank { "0" }.toIntOrNull() ?: 0
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
        return document.select("div[data-controller=carousel] > div > article > a")
            .map { it.attr("href") }
            .map { it.replace("/anime/", EMPTY) }
            .map { config.buildAnimeLink(it) }
    }

    private fun extractTags(jsonData: LivechartData, document: Document): Collection<Tag> {
        val tags: MutableSet<String> = jsonData.genre.toMutableSet().ifEmpty { mutableSetOf() }

        if (tags.isEmpty()) {
            document.select("div:matchesOwn(^Tags\$)")
                .next()
                .select("a[data-anime-details-target=tagChip]")
                .textNodes()
                .map { it.text().trim() }
                .forEach {
                    tags.add(it)
                }
        }

        return tags
    }

    private companion object {
        private const val LARGE_PICTURE_INDICATOR = "large.jpg"
        private const val SMALL_PICTURE_INDICATOR = "small.jpg"
        private const val NO_PIC = "https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic.png"
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