package dev.pandasystems.logmypos_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.pandasystems.logmypos_client.menus.Home
import dev.pandasystems.logmypos_client.menus.HomeScreen
import dev.pandasystems.logmypos_client.menus.Profile
import dev.pandasystems.logmypos_client.menus.ProfileScreen
import dev.pandasystems.logmypos_client.navigation.NavHost
import dev.pandasystems.logmypos_client.navigation.NavigationManager

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		
		onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (NavigationManager.backlogStack.isNotEmpty())
					NavigationManager.backlogStack.pop().invoke()
				else {
					finish()
				}
			}
		})
		
		setContent {
			NavHost(Home) {
				Composer<Home> { HomeScreen() }
				Composer<Profile> { ProfileScreen() }
			}
		}
	}
}