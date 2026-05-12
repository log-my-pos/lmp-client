package dev.pandasystems.logmypos_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import dev.pandasystems.logmypos_client.menus.Home
import dev.pandasystems.logmypos_client.menus.HomeScreen
import dev.pandasystems.logmypos_client.menus.Profile
import dev.pandasystems.logmypos_client.menus.ProfileScreen
import dev.pandasystems.logmypos_client.navigation.NavHost
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		
		setContent {
			MaterialTheme(
				typography = hankenGroteskTypography
			) {
				NavHost(Home) {
					Composer<Home> { HomeScreen() }
					Composer<Profile> { ProfileScreen() }
				}
			}
		}
	}
}