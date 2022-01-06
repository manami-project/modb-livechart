package io.github.manamiproject.modb.livechart

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.*
import io.github.manamiproject.modb.test.loadTestResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI

internal class LivechartConverterTest {

    @Nested
    inner class TitleTests {

        @Test
        fun `title containing special chars`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/title/special_chars.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.title).isEqualTo("Alice Gear Aegis: Doki! Actress Darake no Mermaid Grand Prix ♡")
        }

        @Test
        fun `title containing encoded special chars`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/title/encoded_special_char.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.title).isEqualTo("Love Live! School Idol Project: μ's →NEXT LoveLive! 2014 - Endless Parade Encore Animation")
        }
    }

    @Nested
    inner class SynonymsTests {

        @Test
        fun `no synonyms`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/synonyms/no_synonyms.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.synonyms).isEmpty()
        }

        @Test
        fun `one synonym`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/synonyms/one_synonym.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.synonyms).containsExactly("劇場版 少女☆歌劇 レヴュースタァライト ロンド・ロンド・ロンド")
        }

        @Test
        fun `multiple synonyms`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/synonyms/multiple_synonyms.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.synonyms).containsExactlyInAnyOrder(
                "Angels of Death",
                "殺戮の天使",
            )
        }

        @Test
        fun `synonym containing encoded special char`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/synonyms/encoded_special_chars.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.synonyms).containsExactly("ラブライブ! μ's Final LoveLive! オープニングアニメーション")
        }
    }

    @Nested
    inner class EpisodesTests {

        @Test
        fun `unknown number of episodes`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/unknown.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isZero()
        }

        @Test
        fun `1 episode`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/1.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isOne()
        }

        @Test
        fun `10 episodes`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/10.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(10)
        }

        @Test
        fun `100 episodes`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/100.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(100)
        }

        @Test
        fun `currently running`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/episodes/currently_running.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.episodes).isEqualTo(1006)
        }
    }

    @Nested
    inner class SourcesTests {

        @Test
        fun `extract id 3437`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/sources/3437.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.sources).containsExactly(URI("https://livechart.me/anime/3437"))
        }
    }

    @Nested
    inner class PictureAndThumbnailTests {

        @Test
        fun `neither picture nor thumbnail`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html")


            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://cdn.myanimelist.net/images/qm_50.gif"))
            assertThat(result.thumbnail).isEqualTo(URI("https://cdn.myanimelist.net/images/qm_50.gif"))
        }

        @Test
        fun `picture and thumbnail`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html")


            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.picture).isEqualTo(URI("https://u.livechart.me/anime/3437/poster_image/ea9acd1ccea844fd9c4debde5e8e631e.png?style=large&format=jpg"))
            assertThat(result.thumbnail).isEqualTo(URI("https://u.livechart.me/anime/3437/poster_image/ea9acd1ccea844fd9c4debde5e8e631e.png?style=small&format=jpg"))
        }
    }

    @Nested
    inner class TypeTests {

        @Test
        fun `type is TV`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/tv.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(TV)
        }

        @Test
        fun `Questionmark is mapped to 'UNKNOWN'`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/unknown.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(UNKNOWN)
        }
        
        @Test
        fun `type 'tv short' is mapped to 'TV'`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/tv_short.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(TV)
        }
        
        @Test
        fun `type is Movie`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/movie.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(MOVIE)
        }

        @Test
        fun `type 'web short' is mapped to 'ONA'`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/web_short.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(ONA)
        }

        @Test
        fun `type 'web' is mapped to 'ONA'`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/web.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(ONA)
        }

        @Test
        fun `type is OVA`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/ova.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(OVA)
        }

        @Test
        fun `type is Special`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/type/special.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.type).isEqualTo(SPECIAL)
        }
    }

    @Nested
    inner class DurationTests {

        @Test
        fun `1 hr`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/1_hour.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(1, HOURS))
        }

        @Test
        fun `1 hr 11 min`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/1_hour_11_min.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(71, MINUTES))
        }

        @Test
        fun `2 hr`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/2_hours.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(2, HOURS))
        }

        @Test
        fun `2 hr 15 min`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/2_hours_15_minutes.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(135, MINUTES))
        }

        @Test
        fun `10 min`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/10_min.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(10, MINUTES))
        }

        @Test
        fun `30 sec`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/30_sec.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(30, SECONDS))
        }

        @Test
        fun `unknown duration`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/duration/unknown.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.duration).isEqualTo(Duration(0, SECONDS))
        }
    }

    @Nested
    inner class TagsTests {

        @Test
        fun `extract multiple tags`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/multiple_tags.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).containsExactly(
                "comedy",
                "demons",
                "fantasy",
                "romance",
                "supernatural",
            )
        }

        @Test
        fun `extract exactly one tag`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/one_tag.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).containsExactly("comedy")
        }

        @Test
        fun `no tags available`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/tags/no_tags.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.tags).isEmpty()
        }
    }

    @Nested
    inner class AnimeSeasonTests {

        @Nested
        inner class SeasonTests {

            @Test
            fun `season is 'UNDEFINED'`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/undefined.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
            }

            @Test
            fun `season is 'FALL'`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/fall.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(FALL)
            }

            @Test
            fun `season is 'SPRING'`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/spring.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(SPRING)
            }

            @Test
            fun `season is 'SUMMER'`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/summer.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(SUMMER)
            }

            @Test
            fun `season is 'WINTER'`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/season/winter.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.season).isEqualTo(WINTER)
            }
        }

        @Nested
        inner class YearOfPremiereTests {

            @Test
            fun `exact date is given`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/exact_date.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2021)
            }

            @Test
            fun `premiere in season format`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/season.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2022)
            }

            @Test
            fun `premiere is undefined`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/undefined.html")
                
                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isZero()
            }

            @Test
            fun `link would indicate tba which means undefined, but the text of the link contains a year`() {
                // given
                val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
                }

                val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/link_is_tba_but_text_contains_year.html")

                val converter = LivechartConverter(
                    config = testLivechartConfig,
                )

                // when
                val result = converter.convert(testFile)

                // then
                assertThat(result.animeSeason.year).isEqualTo(2022)
            }
        }
    }

    @Nested
    inner class StatusTests {

        @Test
        fun `status is ongoing`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/ongoing.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(ONGOING)
        }

        @Test
        fun `status is upcoming - start date is unknown`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_unknown_date.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `status is upcoming - start date is known`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/upcoming_known_date.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(UPCOMING)
        }

        @Test
        fun `status is finished`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/status/finished.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.status).isEqualTo(FINISHED)
        }
    }

    @Nested
    inner class RelatedAnimeTests {

        @Test
        fun `no related anime`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/no_relations.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).isEmpty()
        }

        @Test
        fun `one related anime`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/one_relation.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).containsExactly(
                URI("https://livechart.me/anime/3808"),
            )
        }

        @Test
        fun `multiple related anime no scrolling`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/multiple_relations_no_scrolling.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).containsExactlyInAnyOrder(
                URI("https://livechart.me/anime/3367"),
                URI("https://livechart.me/anime/3369"),
                URI("https://livechart.me/anime/3368"),
            )
        }

        @Test
        fun `multiple related anime with scrolling`() {
            // given
            val testLivechartConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                override fun buildAnimeLink(id: AnimeId): URI = LivechartConfig.buildAnimeLink(id)
                override fun buildDataDownloadLink(id: String): URI = LivechartConfig.buildDataDownloadLink(id)
                override fun fileSuffix(): FileSuffix = LivechartConfig.fileSuffix()
            }

            val testFile = loadTestResource("file_converter_tests/related_anime/multiple_relations_with_scrolling.html")

            val converter = LivechartConverter(
                config = testLivechartConfig,
            )

            // when
            val result = converter.convert(testFile)

            // then
            assertThat(result.relatedAnime).containsExactlyInAnyOrder(
                URI("https://livechart.me/anime/3662"),
                URI("https://livechart.me/anime/6221"),
                URI("https://livechart.me/anime/3767"),
                URI("https://livechart.me/anime/7353"),
                URI("https://livechart.me/anime/3782"),
                URI("https://livechart.me/anime/7133"),
                URI("https://livechart.me/anime/4823"),
                URI("https://livechart.me/anime/7289"),
                URI("https://livechart.me/anime/6604"),
                URI("https://livechart.me/anime/5530"),
                URI("https://livechart.me/anime/3984"),
                URI("https://livechart.me/anime/5793"),
                URI("https://livechart.me/anime/7156"),
                URI("https://livechart.me/anime/4054"),
                URI("https://livechart.me/anime/6022"),
                URI("https://livechart.me/anime/6918"),
                URI("https://livechart.me/anime/5170"),
                URI("https://livechart.me/anime/2691"),
                URI("https://livechart.me/anime/8311"),
                URI("https://livechart.me/anime/127"),
                URI("https://livechart.me/anime/10692"),
                URI("https://livechart.me/anime/6762"),
                URI("https://livechart.me/anime/339"),
                URI("https://livechart.me/anime/353"),
                URI("https://livechart.me/anime/1358"),
                URI("https://livechart.me/anime/793"),
                URI("https://livechart.me/anime/1823"),
                URI("https://livechart.me/anime/2664"),
                URI("https://livechart.me/anime/9894"),
            )
        }
    }
}