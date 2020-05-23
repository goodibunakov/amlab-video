package ru.goodibunakov.amlabvideo.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_video.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoViewModel

class VideoFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoViewModel by viewModels { AmlabApplication.viewModelFactory }

    companion object {
        fun newInstance(): VideoFragment {
            return VideoFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("debug", "VideoFragment onViewCreated")
        lifecycle.addObserver(playerView)

        sharedViewModel.playlistId.observe(viewLifecycleOwner, Observer {
            viewModel.loadPlaylist(it)
            Log.d("debug", "playlistId = $it")
        })

        viewModel.videosLiveData.observe(viewLifecycleOwner, Observer {
//            set data to adapter
            Log.d("debug", "list video = $it")
        })

    }
}
