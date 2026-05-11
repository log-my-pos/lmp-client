package dev.pandasystems.logmypos_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.pandasystems.logmypos_client.menus.Home
import dev.pandasystems.logmypos_client.menus.HomeScreen
import dev.pandasystems.logmypos_client.menus.Profile
import dev.pandasystems.logmypos_client.menus.ProfileScreen
import dev.pandasystems.logmypos_client.navigation.NavHost

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		
		setContent {
			NavHost(Home) {
				Composer<Home> { HomeScreen() }
				Composer<Profile> { ProfileScreen() }
			}
		}
	}
}