package com.ltei.lausave

import android.content.SharedPreferences

@Deprecated("lausave will be removed")
interface Savable {

    fun saveToPrefs(id: String, prefs: SharedPreferences.Editor)
    fun loadFromPrefs(id: String, prefs: SharedPreferences)

}