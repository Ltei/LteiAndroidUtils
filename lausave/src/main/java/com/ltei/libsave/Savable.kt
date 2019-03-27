package com.ltei.lausave

import android.content.SharedPreferences

interface Savable {

    fun saveToPrefs(id: String, prefs: SharedPreferences.Editor)
    fun loadFromPrefs(id: String, prefs: SharedPreferences)

}