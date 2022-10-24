package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

/**
 * Downloads anime data from livechart.me
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class LivechartDownloader(
    private val config: MetaDataProviderConfig = LivechartConfig,
    private val httpClient: HttpClient = DefaultHttpClient(isTestContext = config.isTestContext()),
): Downloader {

    @Deprecated("Use coroutines",
        ReplaceWith("runBlocking { downloadSuspendable(id, onDeadEntry) }", "kotlinx.coroutines.runBlocking")
    )
    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String = runBlocking {
        downloadSuspendable(id, onDeadEntry)
    }

    override suspend fun downloadSuspendable(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String = withContext(LIMITED_NETWORK) {
        log.debug { "Downloading [livechartId=$id]" }

        val response = httpClient.getSuspedable(
            url = config.buildDataDownloadLink(id).toURL(),
            headers = mapOf("host" to listOf("www.${config.hostname()}")),
        )

        check(response.body.isNotBlank()) { "Response body was blank for [livechartId=$id] with response code [${response.code}]" }

        val title = Jsoup.parse(response.body).select("title").text().trim()
        if (title.startsWith("Excluded from the LiveChart.me Database")) {
            onDeadEntry.invoke(id)
            return@withContext EMPTY
        }

        return@withContext when(response.code) {
            200 -> response.body
            404 -> {
                onDeadEntry.invoke(id)
                EMPTY
            }
            else -> throw IllegalStateException("Unable to determine the correct case for [livechartId=$id], [responseCode=${response.code}]")
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}