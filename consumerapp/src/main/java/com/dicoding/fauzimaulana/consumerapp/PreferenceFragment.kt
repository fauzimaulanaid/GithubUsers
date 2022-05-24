package com.dicoding.fauzimaulana.consumerapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.fauzimaulana.consumerapp.receiver.AlarmReceiver
import java.util.*

class PreferenceFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var alarmReceiver: AlarmReceiver

    private lateinit var ALARM: String
    private lateinit var LANGUAGE: String

    private lateinit var languagePreference: Preference
    private lateinit var alarmPreference: SwitchPreference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preference)
        init()
        setSummary()
        languagePreference.setOnPreferenceClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
            true
        }
    }

    private fun init() {
        ALARM = resources.getString(R.string.key_alarm)
        LANGUAGE = resources.getString(R.string.key_language)

        alarmPreference = findPreference<SwitchPreference>(ALARM) as SwitchPreference
        languagePreference = findPreference<Preference>(LANGUAGE) as Preference
    }

    private fun setSummary() {
        val languageSelected = Locale.getDefault().toString()
        if (languageSelected == "en_US") {
            languagePreference.summary = resources.getString(R.string.english)
        } else {
            languagePreference.summary = resources.getString(R.string.bahasa)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == ALARM) {
            val isOn: Boolean = alarmPreference.isChecked
            alarmReceiver = AlarmReceiver()
            if (isOn) {
                val repeatTime = "09:00"
                val repeatMessage = resources.getString(R.string.notification)
                alarmReceiver.setRepeatingAlarm(requireContext(), repeatTime, repeatMessage)
            } else {
                alarmReceiver.cancelAlarm(requireContext())
            }
        }
    }
}