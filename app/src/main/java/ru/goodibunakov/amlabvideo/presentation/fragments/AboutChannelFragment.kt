package ru.goodibunakov.amlabvideo.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_about_channel.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.viewmodels.AboutChannelViewModel


class AboutChannelFragment : Fragment() {

    //https://www.googleapis.com/youtube/v3/channels?part=brandingSettings&id=UC_adZIRDiLC3eLIq24HBmRA&key=AIzaSyB8XPLOU4IPt99fJHiDhvNjoywzqpA3JT8
    private val viewModel: AboutChannelViewModel by viewModels { AmlabApplication.viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_about_channel, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner, Observer {
            progress.visibility = it
        })

        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            val requestOptions = RequestOptions()
                    .error(R.drawable.empty_photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(header.context)
                    .load(it.headerUrl)
                    .apply(requestOptions)
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(header)
            description.text = it.description
        })
    }
}