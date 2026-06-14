package dev.pandasystems.logmypos_client.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SuspendLazy<T>(
	private val initializer: suspend () -> T
) {
	private val mutex = Mutex()
	private var value: T? = null
	private var initialized = false

	suspend fun get(): T {
		if (initialized) {
			@Suppress("UNCHECKED_CAST")
			return value as T
		}

		return mutex.withLock {
			if (!initialized) {
				value = initializer()
				initialized = true
			}

			@Suppress("UNCHECKED_CAST")
			value as T
		}
	}
}