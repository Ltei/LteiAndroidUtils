package com.ltei.lausave

import android.content.SharedPreferences

@Deprecated("lausave will be removed")
object LPreferences {
    fun <T : Savable> putNullable(id: String, prefs: SharedPreferences.Editor, value: T?) {
        prefs.putBoolean("${id}_isNull", value == null)
        value?.saveToPrefs("${id}_value", prefs)
    }

    fun <T : Savable> getNullable(id: String, prefs: SharedPreferences, default: () -> T): T? {
        return if (prefs.getBoolean("${id}_isNull", true)) {
            null
        } else {
            val result = default()
            result.loadFromPrefs("${id}_value", prefs)
            result
        }
    }

    fun putNullableString(id: String, prefs: SharedPreferences.Editor, value: String?) {
        prefs.putBoolean("${id}_isNull", value == null)
        if (value != null) prefs.putString("${id}_value", value)
    }

    fun getNullableString(id: String, prefs: SharedPreferences): String? {
        return if (prefs.getBoolean("${id}_isNull", true)) {
            null
        } else {
            return prefs.getString("${id}_value", "")
        }
    }

    fun putNullableInt(id: String, prefs: SharedPreferences.Editor, value: Int?) {
        prefs.putBoolean("${id}_isNull", value == null)
        if (value != null) prefs.putInt("${id}_value", value)
    }

    fun getNullableInt(id: String, prefs: SharedPreferences): Int? {
        return if (prefs.getBoolean("${id}_isNull", true)) {
            null
        } else {
            return prefs.getInt("${id}_value", -1)
        }
    }

    fun <T : Savable> putArray(id: String, prefs: SharedPreferences.Editor, array: ArrayList<T>) {
        prefs.putInt("${id}_length", array.size)
        for (i in 0 until array.size) {
            array[i].saveToPrefs("${id}_elem$i", prefs)
        }
    }

    fun <T : Savable> getArray(id: String, prefs: SharedPreferences, default: () -> T): ArrayList<T> {
        val length = prefs.getInt("${id}_length", 0)
        val result = ArrayList<T>(length)
        for (i in 0 until length) {
            val elem = default()
            elem.loadFromPrefs("${id}_elem$i", prefs)
            result.add(elem)
        }
        return result
    }

    fun putIntArray(id: String, prefs: SharedPreferences.Editor, array: ArrayList<Int>) {
        prefs.putInt("${id}_length", array.size)
        for (i in 0 until array.size) {
            prefs.putInt("${id}_elem$i", array[i])
        }
    }

    fun getIntArray(id: String, prefs: SharedPreferences, defValue: Int): ArrayList<Int> {
        val length = prefs.getInt("${id}_length", 0)
        val result = ArrayList<Int>(length)
        for (i in 0 until length) {
            result.add(prefs.getInt("${id}_elem$i", defValue))
        }
        return result
    }
}