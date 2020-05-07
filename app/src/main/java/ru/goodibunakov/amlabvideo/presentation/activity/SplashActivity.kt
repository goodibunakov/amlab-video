package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.domain.GetChannelPlaylistsUseCase
import ru.goodibunakov.amlabvideo.domain.GetNetworkStatusUseCase
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SplashViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.ViewModelFactory

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val splashViewModel: SplashViewModel by viewModels {
            ViewModelFactory(
                    GetChannelPlaylistsUseCase(AmlabApplication.apiRepository),
                    GetNetworkStatusUseCase(AmlabApplication.apiRepository)
            )
        }

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
                if (it) Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        })
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