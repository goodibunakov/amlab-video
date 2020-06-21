package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import ru.goodibunakov.amlabvideo.R


class WarningDialog {

    companion object {

        fun generate(context: Context, title: Int, message: Int, showCancelButton: Boolean, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
            return AlertDialog.Builder(context).apply {
                setMessage(message)
                setTitle(title)
                setIcon(R.drawable.alert_circle)
                setCancelable(false)
                setPositiveButton(R.string.dialog_ok, listener)
                if (showCancelButton) setNegativeButton(R.string.dialog_cancel, listener)
            }
        }

    }
}