package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfomer.blitz.setTimeAgo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_video.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.adapter.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnFullScreenListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel.Companion.ALL_VIDEOS
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.util.*


class VideoFragment : Fragment(), OnClickListener {

    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }

    private lateinit var adapter: VideoAdapter
    private lateinit var youTubePlayerDisposable: Disposable

    private lateinit var onFullScreenListener: OnFullScreenListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onFullScreenListener = activity as OnFullScreenListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement OnFullScreenListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("debug", "VideoFragment onViewCreated")
        initPlayerView()
        initRecyclerView()
        observeLiveData()
    }

    private fun observeLiveData() {
        sharedViewModel.playlistId.observe(viewLifecycleOwner, Observer { playlistId ->
            viewModel.loadItems(playlistId)
        })

        viewModel.videosLiveData.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
            Log.d("debug", "list video = $it")
        })

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.videoDetails.observe(viewLifecycleOwner, Observer {
            infoTitle.text = it.title
            infoDetails.text = String.format(
                    Locale.US,
                    getString(R.string.video_views_and_pass_from_publish_date),
                    it.viewCount)
            infoTimeAgo.setTimeAgo(it.publishedAtDate)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) somethingWrong.visibility = View.VISIBLE
            else somethingWrong.visibility = View.GONE
        })
    }

    private fun initPlayerView() {
        lifecycle.addObserver(playerView)
        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                youTubePlayerDisposable = viewModel.videoIdSubject
                        .filter { it.isNotEmpty() }
                        .subscribe({
                            youTubePlayer.cueVideo(it, 0f)
                        }, {})
            }
        })
        playerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                onFullScreenListener.enterFullScreen()
//                addCustomActionsToPlayer()
            }

            override fun onYouTubePlayerExitFullScreen() {
                if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) return
                onFullScreenListener.exitFullScreen()
//                removeCustomActionsFromPlayer()
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

    private fun removeCustomActionsFromPlayer() {
        playerView.getPlayerUiController().showCustomAction1(false)
        playerView.getPlayerUiController().showCustomAction2(false)
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

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)
        adapter = VideoAdapter(this)
        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
    }

    fun exitFullScreen() {
        playerView.exitFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        playerView.getPlayerUiController().getMenu()?.dismiss()

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerView.exitFullScreen()
        } else {
            playerView.enterFullScreen()
        }
    }

    override fun onItemClick(videoItem: VideoUIModel) {
        Log.d("debug", "clicked $videoItem")
        if (videoItem.videoId != viewModel.videoIdSubject.value) viewModel.videoIdSubject.onNext(videoItem.videoId)
    }

    override fun onDestroyView() {
        if (::youTubePlayerDisposable.isInitialized && !youTubePlayerDisposable.isDisposed) youTubePlayerDisposable.dispose()
        super.onDestroyView()
    }
}