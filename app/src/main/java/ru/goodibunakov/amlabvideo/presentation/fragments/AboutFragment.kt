package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import ru.goodibunakov.amlabvideo.BuildConfig
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
            getString(R.string.pref_site_key),
            getString(R.string.pref_vk_key),
            getString(R.string.pref_instagram_key),
            getString(R.string.pref_fb_key) -> {
                val url = preference.key.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(Intent.createChooser(intent, getString(R.string.open_with)))
                true
            }
            getString(R.string.pref_developer_title) -> {
                sendEmailToDeveloper()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun sendEmailToDeveloper() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("goodibunakov@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Amlab Android App ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
//        intent.putExtra(Intent.EXTRA_TEXT, "Я хочу поговорить о...")
        try {
            startActivity(Intent.createChooser(intent, "Отправить письмо..."))
        } catch (ex: ActivityNotFoundException) {
            view?.let {
                Snackbar.make(it, getString(R.string.error_no_email_clients), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}