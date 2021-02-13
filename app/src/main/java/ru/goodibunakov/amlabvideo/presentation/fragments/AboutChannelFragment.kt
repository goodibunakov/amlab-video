package ru.goodibunakov.amlabvideo.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.databinding.FragmentAboutChannelBinding
import ru.goodibunakov.amlabvideo.presentation.viewmodels.AboutChannelViewModel


class AboutChannelFragment : Fragment(R.layout.fragment_about_channel) {

    //https://www.googleapis.com/youtube/v3/channels?part=brandingSettings&id=UC_adZIRDiLC3eLIq24HBmRA&key=AIzaSyB8XPLOU4IPt99fJHiDhvNjoywzqpA3JT8
    private val viewModel: AboutChannelViewModel by viewModels { AmlabApplication.viewModelFactory }

    private val binding by viewBinding(FragmentAboutChannelBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner, {
            binding.progress.isVisible = it
        })

        viewModel.error.observe(viewLifecycleOwner, {
            binding.emptyText.isVisible = it != null
            binding.header.isVisible = it == null
            binding.contentView.isVisible = it == null
        })

        viewModel.liveData.observe(viewLifecycleOwner, {
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(binding.header.context)
                .load(it.headerUrl)
                .apply(requestOptions)
                .thumbnail(0.1f)
                .error(R.drawable.empty_photo)
                .transition(DrawableTransitionOptions().crossFade())
                .into(binding.header)
            binding.description.text = it.description
        })
    }
}