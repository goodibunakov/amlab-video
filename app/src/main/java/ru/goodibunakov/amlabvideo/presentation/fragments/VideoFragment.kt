package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikhaellopez.ratebottomsheet.RateBottomSheet
import com.mikhaellopez.ratebottomsheet.RateBottomSheetManager
import com.perfomer.blitz.setTimeAgo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_video.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.interfaces.EmptyListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnFullScreenListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.InfiniteScrollListener
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.utils.setVisibility
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.util.*


class VideoFragment : Fragment(R.layout.fragment_video), OnClickListener, InfiniteScrollListener.OnLoadMoreListener, EmptyListener {

    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }

    private lateinit var videoAdapter: VideoAdapter
    private lateinit var youTubePlayerDisposable: Disposable

    private lateinit var onFullScreenListener: OnFullScreenListener
    private lateinit var infiniteScrollListener: InfiniteScrollListener

    private lateinit var fragmentType: FragmentType

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
        fragmentType = arguments?.getSerializable(EXTRA_TYPE) as FragmentType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("debug", "VideoFragment onViewCreated")
        initPlayerView()
        initRecyclerView()
        observeLiveData()
        initRateBottomSheet()
    }

    private fun observeLiveData() {
        sharedViewModel.playlistId.observe(viewLifecycleOwner, Observer { playlistId ->
            Log.d("debug", "VideoFragment playlistId = $playlistId")
            viewModel.loadItems(playlistId, fragmentType)
        })

        viewModel.videosLiveData.observe(viewLifecycleOwner, Observer {
            infiniteScrollListener.setLoaded()

            videoAdapter.addItems(it)
            toggleVisibility(it.isEmpty())
        })

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner, Observer {
            progressBar.setVisibility(it)
        })

        viewModel.videoDetails.observe(viewLifecycleOwner, Observer {
            infoTitle.text = it.title
            infoDetails.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.video_views_and_pass_from_publish_date),
                    it.viewCount)
            infoTimeAgo.setTimeAgo(it.publishedAtDate)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            errorText.setVisibility(it != null)
        })

        viewModel.recyclerLoadMoreProcess.observe(viewLifecycleOwner, Observer {
            if (it) videoAdapter.addNull() else videoAdapter.removeNull()
        })

        viewModel.canLoadMoreLiveData.observe(viewLifecycleOwner, Observer {
            infiniteScrollListener.setCanLoadMore(it)
        })

        viewModel.videoItemStarChangedLiveData.observe(viewLifecycleOwner, Observer {
            Log.d("debug", "videoItemStarred = $it")
            videoAdapter.notifyItemChanged(it, fragmentType)
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
        videoAdapter = VideoAdapter(this, this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        infiniteScrollListener = InfiniteScrollListener(linearLayoutManager, this@VideoFragment)
        recycler.apply {
            setHasFixedSize(true)
            adapter = videoAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(recycler.context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(infiniteScrollListener)
        }
    }

    override fun onLoadMore() {
        viewModel.loadMoreItems()
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

    override fun onStarClick(videoItem: VideoUIModel) {
        viewModel.starClicked(videoItem)
    }

    override fun listIsEmpty() {
        toggleVisibility(true)
    }

    private fun toggleVisibility(listIsEmpty: Boolean) {
        playerView.setVisibility(!listIsEmpty)
        infoLayout.setVisibility(!listIsEmpty)
        emptyText.setVisibility(listIsEmpty)
    }

    private fun initRateBottomSheet() {
        context?.let {
            RateBottomSheetManager(it)
                    .setInstallDays(3) // 3 by default
                    .setLaunchTimes(5) // 5 by default
                    .setRemindInterval(2) // 2 by default
                    .setShowAskBottomSheet(true) // True by default
                    .setShowLaterButton(true) // True by default
                    .setShowCloseButtonIcon(true) // True by default
                    .monitor()
        }

        Handler().postDelayed({ RateBottomSheet.showRateBottomSheetIfMeetsConditions(this) }, 3500)
    }

    override fun onDestroyView() {
        if (::youTubePlayerDisposable.isInitialized && !youTubePlayerDisposable.isDisposed) youTubePlayerDisposable.dispose()
        super.onDestroyView()
    }

    companion object {

        private const val EXTRA_TYPE = "extra_type"

        fun newInstance(type: FragmentType): VideoFragment {
            return VideoFragment().apply {
                arguments = bundleOf(EXTRA_TYPE to type)
            }
        }
    }

    enum class FragmentType {
        FROM_WEB,
        FROM_DB
    }
}