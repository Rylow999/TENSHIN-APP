package com.tenshin.app.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("tenshin_settings", Context.MODE_PRIVATE)

    private val _ignoreTrash = MutableStateFlow(sharedPrefs.getBoolean("ignore_trash", false))
    val ignoreTrash: StateFlow<Boolean> = _ignoreTrash.asStateFlow()

    private val _showSteelPath = MutableStateFlow(sharedPrefs.getBoolean("show_steel_path", false))
    val showSteelPath: StateFlow<Boolean> = _showSteelPath.asStateFlow()

    private val _enableNotifications = MutableStateFlow(sharedPrefs.getBoolean("enable_notifications", true))
    val enableNotifications: StateFlow<Boolean> = _enableNotifications.asStateFlow()

    fun setIgnoreTrash(value: Boolean) {
        _ignoreTrash.value = value
        sharedPrefs.edit().putBoolean("ignore_trash", value).apply()
    }

    fun setShowSteelPath(value: Boolean) {
        _showSteelPath.value = value
        sharedPrefs.edit().putBoolean("show_steel_path", value).apply()
    }

    fun setEnableNotifications(value: Boolean) {
        _enableNotifications.value = value
        sharedPrefs.edit().putBoolean("enable_notifications", value).apply()
    }
}
