package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.activity_splash.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SplashViewModel


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val splashViewModel: SplashViewModel by viewModels { AmlabApplication.viewModelFactory }

        splashViewModel.playlistsLiveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.open_next, R.anim.close_main)
                finish()
            }
        })

        splashViewModel.error.observe(this, Observer {
            it?.let {
//                if (it) Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        })

        splashViewModel.networkLiveData.observe(this, Observer {
            showNetworkAvailable(it)
        })
    }

    private fun showNetworkAvailable(isAvailable: ConnectedStatus) {
        val indicatorColor = when (isAvailable) {
            ConnectedStatus.YES -> R.color.colorSuccess
            ConnectedStatus.NO -> R.color.colorError
            else -> android.R.color.black
        }
        networkIndicator.setBackgroundColor(ContextCompat.getColor(this, indicatorColor))

        networkIndicator.text = when (isAvailable) {
            ConnectedStatus.YES -> getString(R.string.network_indicator_yes)
            ConnectedStatus.NO -> getString(R.string.network_indicator_no)
            else -> ""
        }

        if (isAvailable == ConnectedStatus.YES)
            showNetworkIndicator(false)
        else
            showNetworkIndicator(true)
    }

    private fun showNetworkIndicator(isShow: Boolean) {
        val transition = Slide(Gravity.TOP)
                .apply {
                    duration = 400
                    addTarget(R.id.networkIndicator)
                    if (!isShow) startDelay = 700
                }

        TransitionManager.beginDelayedTransition(parentSplash, transition)

        networkIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        }
    }
}