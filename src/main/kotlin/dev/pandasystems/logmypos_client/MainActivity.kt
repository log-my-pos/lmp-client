package dev.pandasystems.logmypos_client

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.screen.auth.LoginScreen
import dev.pandasystems.logmypos_client.screen.location.JournalEntryScreen
import dev.pandasystems.logmypos_client.screen.main.MainScreen
import dev.pandasystems.logmypos_client.services.location.LocationService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		// TODO: Used for development so phone screen doesn't turn off
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		
		setContent {
			App {
				val scope = rememberCoroutineScope()
				val locationService: LocationService = koinInject()

				val repository: JournalRepository = koinInject()
				val entries by repository.allEntries.collectAsState(emptyList())

				val mapState = rememberMapState()
				val mapViewportState = LocalMapViewportStateProvider.current

				Surface(modifier = Modifier.fillMaxSize()) {
					Navigator(LoginScreen()) { navigator ->
						val isMainScreen = navigator.lastItem is MainScreen

						Box(Modifier.fillMaxSize()) {
							MapboxMap(
								Modifier.fillMaxSize(),
								mapState = mapState,
								mapViewportState = mapViewportState,
								onMapClickListener = { point ->
									scope.launch {
										locationService.selectLocation(point.latitude(), point.longitude())
									}
									true
								},
								scaleBar = {},
								logo = {},
								attribution = {},
								compass = {},
								style = { MapStyle(style = "mapbox://styles/julianmaggio/cmoijn6tp002201sfdm0nab23") },
								content = {
									val marker = rememberIconImage(R.drawable.marker)
									val selectedLocation = locationService.selectedLocation
									if (selectedLocation != null) {
										PointAnnotation(selectedLocation.asMapBoxPoint) {
											iconImage = marker
											iconSize = 0.35
											iconAnchor = IconAnchor.BOTTOM
										}
									} else {
										entries.forEach { entry ->
											PointAnnotation(Point.fromLngLat(entry.longitude, entry.latitude)) {
												iconImage = marker
												iconSize = 0.35
												iconAnchor = IconAnchor.BOTTOM
												interactionsState.onClicked {
													navigator.push(JournalEntryScreen(entry.id))
													true
												}
											}
										}
									}
								}
							)

							if (!isMainScreen) {
								Box(
									modifier = Modifier
										.fillMaxSize()
										.pointerInput(Unit) {
											awaitPointerEventScope {
												while (true) {
													val event = awaitPointerEvent()
													event.changes.forEach { it.consume() }
												}
											}
										}
								)
							}

							// Composite the current active screen
							CrossfadeTransition(navigator)
						}
					}
				}
			}
		}
	}
}