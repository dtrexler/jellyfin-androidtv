package org.jellyfin.androidtv.ui.playback.moremetadata

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jellyfin.sdk.api.client.util.ApiSerializer

class SongRangesTests : FunSpec({
	test("covering uses half-open ranges and keeps overlaps") {
		val first = Song(title = "First", startTicks = 100, endTicks = 200)
		val second = Song(title = "Second", startTicks = 150, endTicks = 300)
		val songs = listOf(first, second)

		songs.covering(99) shouldBe emptyList()
		songs.covering(100) shouldBe listOf(first)
		songs.covering(150) shouldBe listOf(first, second)
		songs.covering(200) shouldBe listOf(second)
		songs.covering(300) shouldBe emptyList()
	}

	test("response decodes with and without optional values") {
		val response = ApiSerializer.decodeResponseBody<SongRangesResponse>(
			"""
			{"ItemId":"48140466cff63222bd5563c27e43190d","Songs":[
				{"Title":"First","Artists":["A","B"],"ImageUrl":"/MoreMetadata/Images/x","StartTicks":100,"EndTicks":200},
				{"Title":"Second","Artists":[],"StartTicks":150,"EndTicks":300}
			]}
			"""
		)

		response.songs shouldBe listOf(
			Song("First", listOf("A", "B"), "/MoreMetadata/Images/x", 100, 200),
			Song("Second", startTicks = 150, endTicks = 300),
		)
	}
})
