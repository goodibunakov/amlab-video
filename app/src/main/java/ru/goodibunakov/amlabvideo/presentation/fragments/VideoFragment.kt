package ru.goodibunakov.amlabvideo.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfomer.blitz.setTimeAgo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.fragment_video.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.activity.MainActivity.Companion.APP_MENU_ITEM
import ru.goodibunakov.amlabvideo.presentation.adapter.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.util.*


class VideoFragment : Fragment(){

//    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }

//    private var youTubePlayer: YouTubePlayer? = null
    private var videoId: String? = null
    private var playlistId = "0"
        set(value) {
            if (value != field && !value.contains(APP_MENU_ITEM))
                field = value
        }

    companion object {

        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: String): VideoFragment {
            val fragment = VideoFragment()
            val bundle = Bundle().apply { putString(PLAYLIST_ID, playlistId) }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getString(PLAYLIST_ID) ?: "0"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("debug", "VideoFragment onViewCreated")

//you tube либа итальянца
        lifecycle.addObserver(playerView)
        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                val videoId = "S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId, 0F)
            }
        })

//        initRecyclerView()

//        sharedViewModel.playlistId.observe(viewLifecycleOwner, Observer {
//            if (it == "0") viewModel.loadAllVideosList() else viewModel.loadPlaylist(it)
//            Log.d("debug", "playlistId = $it")
//        })

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

//    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?, restored: Boolean) {
//        this.youTubePlayer = youTubePlayer
//        youTubePlayer?.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE)
//        youTubePlayer?.setOnFullscreenListener { activity as YouTubePlayer.OnFullscreenListener }
//
//        if (!restored && !videoId.isNullOrEmpty()) {
//            youTubePlayer?.cueVideo(videoId)
//        }
//    }
//
//    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, youTubeInitializationResult: YouTubeInitializationResult?) {
//        this.youTubePlayer = null
//    }
//
    fun backNormal() {
        //        youTubePlayer?.setFullscreen(false)
    }
//
//    override fun onDestroy() {
//        youTubePlayer?.release()
//        super.onDestroy()
//    }

}