package dev.pandasystems.logmypos_client

import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.api.LogMyPosApi
import dev.pandasystems.logmypos_client.data.AppDatabase
import dev.pandasystems.logmypos_client.models.GlobalData
import dev.pandasystems.logmypos_client.repository.FakeJournalRepositoryImpl
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.repository.JournalRepositoryImpl
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.services.auth.AuthServiceImpl
import dev.pandasystems.logmypos_client.services.auth.TokenManager
import dev.pandasystems.logmypos_client.services.location.FakeLocationServiceImpl
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.services.location.LocationServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
	single { AppDatabase.getDatabase(androidContext()) }
	single { JournalRepositoryImpl(get<AppDatabase>().journalEntryDao()) } bind JournalRepository::class
    single { LocationServiceImpl() } bind LocationService::class

    single { GlobalData() } bind GlobalData::class

    single { TokenManager(androidContext()) }
    single { LogMyPosApi(get()) }
    single { LocationApiService(get()) }
    single { AuthServiceImpl(get(), get()) } bind AuthService::class
}

val previewModule = module {
    single { FakeJournalRepositoryImpl() } bind JournalRepository::class
    single { FakeLocationServiceImpl() } bind LocationService::class

    single { GlobalData() } bind GlobalData::class

    // For simplicity in previews, we can use the real impl with mocks or just dummy data
    single { TokenManager(androidContext()) }
    single { LogMyPosApi(get()) }
    single { LocationApiService(get()) }
    single { AuthServiceImpl(get(), get()) } bind AuthService::class
}