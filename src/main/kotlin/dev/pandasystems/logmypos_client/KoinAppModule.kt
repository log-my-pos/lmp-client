package dev.pandasystems.logmypos_client

import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.api.LogMyPosApi
import dev.pandasystems.logmypos_client.data.AppDatabase
import dev.pandasystems.logmypos_client.repository.FakeJournalRepositoryImpl
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.repository.JournalRepositoryImpl
import dev.pandasystems.logmypos_client.services.auth.*
import dev.pandasystems.logmypos_client.services.location.FakeLocationService
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.services.location.LocationServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { JournalRepositoryImpl(get<AppDatabase>().journalEntryDao()) } bind JournalRepository::class
    single { LocationServiceImpl() } bind LocationService::class

    single { TokenManagerImpl(androidContext()) } bind TokenManager::class
    single { LogMyPosApi(get()) }
    single { LocationApiService(get()) }
    single { AuthServiceImpl(get(), get()) } bind AuthService::class
}

val previewModule = module {
    single { FakeJournalRepositoryImpl() } bind JournalRepository::class
    single { FakeLocationService() } bind LocationService::class

    single { FakeTokenManager() } bind TokenManager::class
    single { LogMyPosApi(get()) }
    single { LocationApiService(get()) }
    single { FakeAuthService() } bind AuthService::class
}