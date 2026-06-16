package dev.pandasystems.logmypos_client.models.location

interface LocationSearch {
	val name: String
	val formattedAddress: String?
	val distanceMeters: Double?
	
	suspend fun resolve(): LocationData
}

data class FakeLocationSearch(
	override val name: String,
	override val formattedAddress: String?,
	override val distanceMeters: Double?
) : LocationSearch {
	override suspend fun resolve(): LocationData {
		TODO("Not yet implemented")
	}
}