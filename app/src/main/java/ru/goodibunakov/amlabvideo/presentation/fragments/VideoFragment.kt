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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfomer.blitz.setTimeAgo
import kotlinx.android.synthetic.main.fragment_video.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.adapter.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.time.ZonedDateTime
import java.util.*

class VideoFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }

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
//        initRecyclerView()

        sharedViewModel.playlistId.observe(viewLifecycleOwner, Observer {
            if (it == "0") viewModel.loadAllVideosList() else viewModel.loadPlaylist(it)
            Log.d("debug", "playlistId = $it")
        })

        viewModel.videosLiveData.observe(viewLifecycleOwner, Observer {
//            set data to adapter
            Log.d("debug", "list video = $it")
        })

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner, Observer {
            if (it) progressBar.visibility = View.VISIBLE else View.GONE
        })

        viewModel.currentVideoLiveData.observe(viewLifecycleOwner, Observer {
//            playerView.video = it.videoId
//            infoTitle.text = it.title
//            infoDetails.text = getString(R.string.video_views_and_pass_from_publish_date, it.)
        })

        viewModel.videoDetails.observe(viewLifecycleOwner, Observer {
            infoTitle.text = it.title
            infoDetails.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.video_views_and_pass_from_publish_date),
                    it.viewCount)
            infoTimeAgo.setTimeAgo(it.publishedAtDate)
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)
        recycler.adapter = VideoAdapter()
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
    }
}
