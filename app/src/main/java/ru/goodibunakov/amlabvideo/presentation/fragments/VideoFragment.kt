package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfomer.blitz.setTimeAgo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.fragment_video.view.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.activity.MainActivity.Companion.APP_MENU_ITEM
import ru.goodibunakov.amlabvideo.presentation.adapter.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.util.*


class VideoFragment : Fragment() {

    //    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }

    private var videoId: String? = null
    private var playlistId = "0"
        set(value) {
            if (value != field && !value.contains(APP_MENU_ITEM))
                field = value
        }

    private lateinit var onFullScreenListener: OnFullScreenListener

    companion object {
        private const val PLAYLIST_ID = "playlist_id"

        fun newInstance(playlistId: String): VideoFragment {
            val fragment = VideoFragment()
            val bundle = Bundle().apply { putString(PLAYLIST_ID, playlistId) }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onFullScreenListener = activity as OnFullScreenListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement OnFullScreenListener")
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
        initPlayerView()


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

    private fun initPlayerView() {
        lifecycle.addObserver(playerView)
        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                val videoId = "S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId, 0F)
            }
        })
        playerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                onFullScreenListener.enterFullScreen()
                recycler.visibility = View.GONE
                infoLayout.visibility = View.GONE
                addCustomActionsToPlayer()
            }

            override fun onYouTubePlayerExitFullScreen() {
                onFullScreenListener.exitFullScreen()
                recycler.visibility = View.VISIBLE
                infoLayout.visibility = View.VISIBLE
                removeCustomActionsFromPlayer()
            }
        })
    }

    /**
     * This method adds a new custom action to the player.
     * Custom actions are shown next to the Play/Pause button in the middle of the player.
     */
    private fun addCustomActionsToPlayer() {
        val customAction1Icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fast_rewind_white_24dp)
        val customAction2Icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fast_forward_white_24dp)
        if (customAction1Icon != null && customAction2Icon != null) {
            playerView.getPlayerUiController().setCustomAction1(customAction1Icon, View.OnClickListener { Toast.makeText(requireActivity(), "custom action1 clicked", Toast.LENGTH_SHORT).show() })
            playerView.getPlayerUiController().setCustomAction2(customAction2Icon, View.OnClickListener { Toast.makeText(requireActivity(), "custom action2 clicked", Toast.LENGTH_SHORT).show() })
        }
    }

//    /**
//     * Set a click listener on the "Play next video" button
//     */
//    private open fun setPlayNextVideoButtonClickListener(youTubePlayer: YouTubePlayer) {
//        val playNextVideoButton: Button = findViewById(R.id.next_video_button)
//        playNextVideoButton.setOnClickListener({ view ->
//            YouTubePlayerUtils.loadOrCueVideo(
//                    youTubePlayer, lifecycle,
//                    VideoIdsProvider.getNextVideoId(), 0f
//            )
//        })
//    }

    private fun removeCustomActionsFromPlayer() {
        playerView.getPlayerUiController().showCustomAction1(false)
        playerView.getPlayerUiController().showCustomAction2(false)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)
        recycler.adapter = VideoAdapter()
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
    }

    fun backNormal() {
        playerView.exitFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        playerView.getPlayerUiController().getMenu()?.dismiss()
    }
}