package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SplashViewModel

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        splashViewModel.playlistsLiveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.open_next, R.anim.close_main)
                finish()
            }
        })

        splashViewModel.error.observe(this, Observer {
            it.let {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            }
        }
    }
}