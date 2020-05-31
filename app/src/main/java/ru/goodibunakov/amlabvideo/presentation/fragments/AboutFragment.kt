package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.goodibunakov.amlabvideo.R


class AboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_fragment, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            getString(R.string.pref_share_key) -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) +
                        " " + getString(R.string.googleplay_url))
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
                true
            }
            getString(R.string.pref_rate_review_key) -> {
                val rateReviewIntent = Intent(Intent.ACTION_VIEW)
                rateReviewIntent.data = Uri.parse(getString(R.string.googleplay_url))
                startActivity(rateReviewIntent)
                true
            }
            getString(R.string.pref_vk_key) -> {
                val url = preference.title.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }
}