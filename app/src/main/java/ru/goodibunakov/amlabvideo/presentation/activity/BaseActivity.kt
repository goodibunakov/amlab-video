package ru.goodibunakov.amlabvideo.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.fragments.ErrorDialogFragment
import ru.goodibunakov.amlabvideo.presentation.viewmodels.BaseActivityViewModel

abstract class BaseActivity<T : BaseActivityViewModel> : AppCompatActivity() {

    protected abstract val viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.networkLiveData.observe(this, Observer {
            showNetworkAvailable(it)
        })
    }

    protected abstract fun showNetworkAvailable(isAvailable: ConnectedStatus)


    fun showErrorDialog() {
        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, ErrorDialogFragment())
                .commit()
//диалог о квоте
    }


}