package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.BroadcastReceiver
import android.content.DialogInterface
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.goodibunakov.amlabvideo.api.QuotaErrorInterceptor.Companion.ACTION_QUOTA_EXCEEDED
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.fragments.ErrorDialog
import ru.goodibunakov.amlabvideo.presentation.viewmodels.BaseActivityViewModel

abstract class BaseActivity<T : BaseActivityViewModel> : AppCompatActivity() {

    protected abstract val viewModel: T
    protected var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.networkLiveData.observe(this, Observer {
            showNetworkAvailable(it)
        })

        viewModel.responseQuotaReceiver.observe(this, Observer {
            unregisterReceiver()
            broadcastReceiver = it
            registerReceiver()
        })

        viewModel.errorQuotaLiveData.observe(this, Observer {
            if (it) showQuotaErrorDialog()
        })
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver()
    }

    protected abstract fun showNetworkAvailable(isAvailable: ConnectedStatus)

    private fun registerReceiver() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver!!, IntentFilter(ACTION_QUOTA_EXCEEDED))
            Log.d("debug", "BaseActivity registerReceiver")
        }
    }

    private fun unregisterReceiver() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver!!)
            Log.d("debug", "BaseActivity unregisterReceiver")
        }
    }


    private fun showQuotaErrorDialog() {
        ErrorDialog.generate(this, DialogInterface.OnClickListener { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                finishAndRemoveTask()
            }
        }).show()
    }
}