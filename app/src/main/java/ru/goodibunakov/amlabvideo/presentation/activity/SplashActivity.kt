package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.BuildConfig
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.utils.setVisibility
import ru.goodibunakov.amlabvideo.presentation.utils.zoomIn
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SplashViewModel
import java.util.concurrent.TimeUnit


class SplashActivity : BaseActivity<SplashViewModel>() {

    private lateinit var animationDisposable: Disposable

    override val viewModel: SplashViewModel by viewModels { AmlabApplication.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        animationDisposable = logo.zoomIn()
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("debug", "animationDisposable oncomplete")
                    viewModel.playlistsLiveData.observe(this, {
                        if (it.isNotEmpty()) {
                            viewModel.errorQuotaLiveData.observe(this, { errorQuotaExists ->
                                if (!errorQuotaExists) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.open_next, R.anim.close_main)
                                    finish()
                                }
                            })
                        }
                    })
                },{
                    Log.d("debug", "animationDisposable error = $it")
                })

        version.text = String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME)

        viewModel.error.observe(this, {
            it?.let {
                Snackbar.make(parentSplash, "Ошибка: ${it.localizedMessage}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun showNetworkAvailable(isAvailable: ConnectedStatus) {
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
        val transition = Slide(Gravity.BOTTOM)
                .apply {
                    duration = 500
                    addTarget(R.id.networkIndicator)
                    if (!isShow) startDelay = 500
                }

        TransitionManager.beginDelayedTransition(parentSplash, transition)

        networkIndicator.setVisibility(isShow)
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

    override fun onDestroy() {
        if (::animationDisposable.isInitialized && !animationDisposable.isDisposed) animationDisposable.dispose()
        super.onDestroy()
    }
}