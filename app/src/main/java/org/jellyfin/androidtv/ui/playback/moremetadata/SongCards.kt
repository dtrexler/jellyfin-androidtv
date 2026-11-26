package org.jellyfin.androidtv.ui.playback.moremetadata

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.base.Text
import org.jellyfin.androidtv.ui.composable.AsyncImage

@Composable
fun SongCards(
	songs: List<Song>,
	imageUrl: (String) -> String,
) {
	val context = LocalContext.current
	val placeholder = remember(context) { ContextCompat.getDrawable(context, R.drawable.ic_music_album) }

	FlowRow(
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		songs.forEach { song ->
			key(song) {
				Row(
					modifier = Modifier
						.width(180.dp)
						.clip(RoundedCornerShape(4.5.dp))
						.background(colorResource(R.color.popup_menu_background).copy(alpha = 0.8f))
						.padding(1.dp),
					horizontalArrangement = Arrangement.spacedBy(7.5.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					AsyncImage(
						modifier = Modifier
							.size(42.dp)
							.clip(RoundedCornerShape(2.25.dp))
							.background(Color.White.copy(alpha = 0.15f)),
						url = song.imageUrl?.let(imageUrl),
						placeholder = placeholder,
					)

					Column {
						Text(
							text = song.title,
							modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
							color = Color.White,
							fontSize = 12.sp,
							fontWeight = FontWeight.Bold,
							maxLines = 1,
						)
						Text(
							text = song.artists.joinToString(),
							color = Color.White.copy(alpha = 0.7f),
							fontSize = 10.5.sp,
							overflow = TextOverflow.Ellipsis,
							maxLines = 1,
						)
					}
				}
			}
		}
	}
}
