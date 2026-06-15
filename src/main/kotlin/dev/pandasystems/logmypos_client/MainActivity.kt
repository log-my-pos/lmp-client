package dev.pandasystems.logmypos_client

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		// TODO: Used for development so phone screen doesn't turn off
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		
		setContent {
			App()
		}
	}
}