package dev.pandasystems.logmypos_client.utils

import android.util.Log

class Logger(
    val tag: String = "LogMyPos"
) {
    fun debug(message: String) {
        Log.d(tag, message)
    }

    fun info(message: String) {
        Log.i(tag, message)
    }

    fun error(message: String) {
        Log.e(tag, message)
    }

    fun warning(message: String) {
        Log.w(tag, message)
    }
}