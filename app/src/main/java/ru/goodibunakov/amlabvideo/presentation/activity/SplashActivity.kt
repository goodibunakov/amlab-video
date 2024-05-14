package ru.goodibunakov.amlabvideo.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.BuildConfig
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.databinding.ActivitySplashBinding
import ru.goodibunakov.amlabvideo.presentation.utils.zoomIn
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SplashViewModel
import java.util.concurrent.TimeUnit


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<SplashViewModel>() {

    private lateinit var animationDisposable: Disposable

    override val viewModel: SplashViewModel by viewModels { AmlabApplication.viewModelFactory }

    private val binding by viewBinding(ActivitySplashBinding::bind, R.id.activity_splash_root)

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)

        animationDisposable = binding.logo.zoomIn()
            .delay(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("debug", "animationDisposable oncomplete")
                viewModel.playlistsLiveData.observe(this) {
                    if (it.isNotEmpty()) {
                        viewModel.errorQuotaLiveData.observe(this) { errorQuotaExists ->
                            if (!errorQuotaExists) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.open_next, R.anim.close_main)
                                finish()
                            }
                        }
                    }
                }
            }, {
                Log.d("debug", "animationDisposable error = $it")
            })

        binding.version.text =
            String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME)

        viewModel.error.observe(this) {
            it?.let {
                Snackbar.make(binding.root, "Ошибка: ${it.localizedMessage}", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun showNetworkAvailable(isAvailable: ConnectedStatus) {
        val indicatorColor = when (isAvailable) {
            ConnectedStatus.YES -> R.color.colorSuccess
            ConnectedStatus.NO -> R.color.colorError
            else -> android.R.color.black
        }
        binding.networkIndicator.setBackgroundColor(ContextCompat.getColor(this, indicatorColor))

        binding.networkIndicator.text = when (isAvailable) {
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

        TransitionManager.beginDelayedTransition(binding.root, transition)

        binding.networkIndicator.isVisible = isShow
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

    override fun layoutResId() = R.layout.activity_splash
}