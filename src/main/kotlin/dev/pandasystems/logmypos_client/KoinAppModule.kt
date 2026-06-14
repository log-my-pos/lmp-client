package dev.pandasystems.logmypos_client

import dev.pandasystems.logmypos_client.data.AppDatabase
import dev.pandasystems.logmypos_client.repository.FakeJournalRepositoryImpl
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.repository.JournalRepositoryImpl
import dev.pandasystems.logmypos_client.services.location.FakeLocationServiceImpl
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.services.location.LocationServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val appModule = module {
	single { AppDatabase.getDatabase(androidContext()) }
	single { JournalRepositoryImpl(get<AppDatabase>().journalEntryDao()) } bind JournalRepository::class
	
	single<LocationServiceImpl>() bind LocationService::class
}

val previewModule = module {
	single<FakeJournalRepositoryImpl>() bind JournalRepository::class
	single<FakeLocationServiceImpl>() bind LocationService::class
}