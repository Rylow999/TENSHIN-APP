package com.tenshin.app.data.remote

import android.content.Context
import java.io.File

object AutoConfig {
    private const val IP_FILENAME = "server_ip.txt"

    fun getInjectedIp(context: Context): String? {
        return try {
            val file = File(context.getExternalFilesDir(null), IP_FILENAME)
            if (file.exists()) {
                file.readText().trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
