package dev.pandasystems.logmypos_client

import android.app.Application
import androidx.work.*
import com.mapbox.common.MapboxOptions
import dev.pandasystems.logmypos_client.worker.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class LogMyPosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)

        startKoin {
            androidContext(this@LogMyPosApplication)
            modules(appModule)
        }

        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PeriodicLocationSync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }
}
