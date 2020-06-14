package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import ru.goodibunakov.amlabvideo.R


class ErrorDialog {

    companion object {
        fun generate(context: Context, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.dialog_error_quota_message)
            builder.setTitle(R.string.dialog_error_quota_title)
            builder.setIcon(R.drawable.alert_circle)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.dialog_ok, listener)
            return builder
        }
    }
}