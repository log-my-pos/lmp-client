package dev.pandasystems.logmypos_client.navigation

import java.util.*

internal object NavigationManager {
	val controllerStack = ArrayDeque<NavigationController>()
	val backlogStack = ArrayDeque<() -> Unit>()
}