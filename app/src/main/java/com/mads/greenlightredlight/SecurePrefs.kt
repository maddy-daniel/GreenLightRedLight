package com.mads.greenlightredlight

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

//Stores the lock-enabled preference using EncryptedSharedPreferences rather
//the plain SharedPreferences already used elsewhere (e.g. rollover tracking)
//since this preference gates access to the user's financial data.

object SecurePrefs{
    private const val PREFS_NAME ="greenlightredlight_secure_prefs"
    private const val KEY_LOCK_ENABLED = "lock_enabled"

    private fun getPrefs(context: Context) = run{
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    fun isLockEnabled(context: Context): Boolean{
        return getPrefs(context).getBoolean(KEY_LOCK_ENABLED, false)
    }
    fun setLockEnabled(context: Context, enabled: Boolean){
        getPrefs(context).edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }
}