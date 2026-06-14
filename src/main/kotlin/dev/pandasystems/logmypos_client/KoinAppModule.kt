package dev.pandasystems.logmypos_client

import com.mapbox.search.autocomplete.PlaceAutocomplete
import dev.pandasystems.logmypos_client.data.AppDatabase
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.LocationService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
	single { PlaceAutocomplete.create(locationProvider = null) }
	single { AppDatabase.getDatabase(androidContext()) }
	single { JournalRepository(get<AppDatabase>().journalEntryDao()) }
	
	singleOf(::LocationService)
}