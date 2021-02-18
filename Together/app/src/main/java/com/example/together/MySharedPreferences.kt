package com.example.together

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_USER_ID = "userId"
    val PREF_KEY_USER_PW = "userPw"
    val PREF_KEY_KAKAO = "kakao"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var userId: String?
        get() = prefs.getString(PREF_KEY_USER_ID, "")
        set(value) = prefs.edit().putString(PREF_KEY_USER_ID, value).apply()

    var userPw: String?
        get() = prefs.getString(PREF_KEY_USER_PW, "")
        set(value) = prefs.edit().putString(PREF_KEY_USER_PW, value).apply()

    var kakao: Boolean
        get() = prefs.getBoolean(PREF_KEY_KAKAO, false)
        set(value) = prefs.edit().putBoolean(PREF_KEY_KAKAO, value).apply()
}