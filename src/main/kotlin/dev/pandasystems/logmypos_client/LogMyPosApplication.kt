package dev.pandasystems.logmypos_client

import android.app.Application
import com.mapbox.common.MapboxOptions
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LogMyPosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)

        startKoin {
            androidContext(this@LogMyPosApplication)
            modules(appModule)
        }
    }
}
