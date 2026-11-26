package org.jellyfin.androidtv.ui.playback.moremetadata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.ApiClientException
import timber.log.Timber

@Serializable
data class SongRangesResponse(
	@SerialName("Songs") val songs: List<Song> = emptyList(),
)

@Serializable
data class Song(
	@SerialName("Title") val title: String,
	@SerialName("Artists") val artists: List<String> = emptyList(),
	@SerialName("ImageUrl") val imageUrl: String? = null,
	@SerialName("StartTicks") val startTicks: Long,
	@SerialName("EndTicks") val endTicks: Long,
)

/** Ranges are half-open: start <= position < end. */
fun List<Song>.covering(positionTicks: Long) = filter { positionTicks >= it.startTicks && positionTicks < it.endTicks }

/** Empty on failure, including a server without the plugin. */
suspend fun ApiClient.getSongs(mediaSourceId: String): List<Song> = try {
	withContext(Dispatchers.IO) {
		request(
			pathTemplate = "/MoreMetadata/{mediaSourceId}",
			pathParameters = mapOf("mediaSourceId" to mediaSourceId),
		).createContent<SongRangesResponse>().songs
	}
} catch (err: ApiClientException) {
	Timber.w(err, "Unable to load songs for media source %s", mediaSourceId)
	emptyList()
}
