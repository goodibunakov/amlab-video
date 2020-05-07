package ru.goodibunakov.amlabvideo.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_network_indicator.*
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus


class NetworkIndicatorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_network_indicator, container, false)
    }

    fun showNetworkAvailable(isAvailable: ConnectedStatus) {
        val indicatorColor = when (isAvailable) {
            ConnectedStatus.YES -> R.color.colorSuccess
            ConnectedStatus.NO -> R.color.colorError
        }
        networkIndicator.setBackgroundColor(ContextCompat.getColor(requireContext(), indicatorColor))

        statusView.visibility = if (isAvailable == ConnectedStatus.YES)
            View.GONE
        else
            View.VISIBLE
    }
}