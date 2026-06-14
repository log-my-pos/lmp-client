package dev.pandasystems.logmypos_client.models.location

interface LocationSearch {
	val name: String
	val formattedAddress: String?
	val distanceMeters: Double?
	
	suspend fun resolve(): LocationData
	
	companion object {
		val PREVIEW = object : LocationSearch {
			override val name: String = "Main Street"
			override val formattedAddress: String = "123 Main Street"
			override val distanceMeters: Double = 1234.0

			override suspend fun resolve(): LocationData {
				throw IllegalStateException("Preview location search cannot be resolved")
			}
		}
	}
}