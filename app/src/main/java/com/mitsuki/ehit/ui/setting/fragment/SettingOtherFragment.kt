package com.mitsuki.ehit.ui.setting.fragment

import android.content.Context
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mitsuki.ehit.R
import com.mitsuki.ehit.const.ValueFinder
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.extensions.text
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.ui.setting.dialog.ProxyInputDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.IllegalArgumentException

@AndroidEntryPoint
class SettingOtherFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var shareData: ShareData

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_other, rootKey)

        findPreference<Preference>(ShareData.SP_PROXY_MODE)?.apply {
            summary = proxySummary(shareData.spProxyMode)
            setOnPreferenceClickListener {
                ProxyInputDialog { index, host ->
                    summary = "${string(ValueFinder.proxySummary(index))} $host"
                }.show(childFragmentManager, "proxy")
                true
            }
        }

        findPreference<ListPreference>(ShareData.SP_THEME)?.apply {
            entries = arrayOf(
                string(R.string.text_theme_follow_system),
                string(R.string.text_theme_night_no),
                string(R.string.text_theme_night_yes)
            )
            entryValues = Array(3) { it.toString() }
        }
    }

    private fun proxySummary(index: Int): String {
        val extendInfo = when (index) {
            0, 1 -> ""
            2, 3 -> " ${shareData.spProxyIp}:${shareData.spProxyPort}"
            else -> throw  IllegalArgumentException()
        }
        return string(ValueFinder.proxySummary(index)) + extendInfo
    }


}