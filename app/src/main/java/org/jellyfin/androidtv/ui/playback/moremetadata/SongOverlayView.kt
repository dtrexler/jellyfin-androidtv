package org.jellyfin.androidtv.ui.playback.moremetadata

import android.content.Context
import android.util.AttributeSet
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AbstractComposeView
import org.jellyfin.androidtv.preference.UserPreferences
import org.jellyfin.androidtv.ui.playback.PlaybackController
import org.jellyfin.sdk.api.client.ApiClient
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

/** Songs playing at the current position, shown while the player controls are visible. */
class SongOverlayView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {
	var osdVisible by mutableStateOf(false)
	private var mediaSourceId by mutableStateOf<String?>(null)
	private var positionTicks by mutableLongStateOf(0L)

	fun setPlayback(controller: PlaybackController?, positionMs: Long) {
		// Live TV positions are program relative. getCurrentMediaSource() fails without a current item.
		mediaSourceId = controller
			?.takeIf { !it.isLiveTv && it.currentlyPlayingItem != null }
			?.currentMediaSource
			?.id
		positionTicks = positionMs.milliseconds.inWholeNanoseconds / 100
	}

	@Composable
	override fun Content() {
		val userPreferences = koinInject<UserPreferences>()
		if (!remember { userPreferences[UserPreferences.moreMetadataEnabled] }) return

		val api = koinInject<ApiClient>()
		val sourceId = mediaSourceId ?: return
		var songs by remember(sourceId) { mutableStateOf(emptyList<Song>()) }
		LaunchedEffect(sourceId) { songs = api.getSongs(sourceId) }

		AnimatedVisibility(
			visible = osdVisible,
			enter = slideInVertically { it } + fadeIn(),
			exit = slideOutVertically { it } + fadeOut(),
		) {
			SongCards(
				songs = songs.covering(positionTicks),
				imageUrl = { path -> api.createUrl(path, ignorePathParameters = true) },
			)
		}
	}
}
