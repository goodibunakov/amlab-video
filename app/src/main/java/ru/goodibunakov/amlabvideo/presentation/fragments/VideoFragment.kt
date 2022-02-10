package ru.goodibunakov.amlabvideo.presentation.fragments

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import com.perfomer.blitz.setTimeAgo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.reactivex.disposables.Disposable
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.databinding.FragmentVideoBinding
import ru.goodibunakov.amlabvideo.presentation.interfaces.EmptyListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnClickListener
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnFullScreenListener
import ru.goodibunakov.amlabvideo.presentation.model.VideoUIModel
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.InfiniteScrollListener
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.VideoAdapter
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.VideoFragmentViewModel
import java.util.*


class VideoFragment : Fragment(R.layout.fragment_video),
    OnClickListener,
    InfiniteScrollListener.OnLoadMoreListener,
    EmptyListener {

    private val sharedViewModel: SharedViewModel by activityViewModels { AmlabApplication.viewModelFactory }
    private val viewModel: VideoFragmentViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val binding by viewBinding(FragmentVideoBinding::bind)

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
    }

    private fun observeLiveData() {
        sharedViewModel.playlistId.observe(viewLifecycleOwner) { playlistId ->
            Log.d("debug", "VideoFragment playlistId = $playlistId")
            viewModel.loadItems(playlistId, fragmentType)
        }

        viewModel.videosLiveData.observe(viewLifecycleOwner) {
            infiniteScrollListener.setLoaded()

            videoAdapter.addItems(it)
            toggleVisibility(it.isEmpty())
        }

        viewModel.progressBarVisibilityLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        viewModel.videoDetails.observe(viewLifecycleOwner) {
            Log.d("debug", "videoDetails = $it")
            with(binding) {
                infoTitle.text = it.title
                infoDetails.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.video_views_and_pass_from_publish_date),
                    it.viewCount
                )
                infoTimeAgo.setTimeAgo(it.publishedAtDate)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding.errorText.isVisible = it != null
        }

        viewModel.recyclerLoadMoreProcess.observe(viewLifecycleOwner) {
            if (it) videoAdapter.addNull() else videoAdapter.removeNull()
        }

        viewModel.canLoadMoreLiveData.observe(viewLifecycleOwner) {
            infiniteScrollListener.setCanLoadMore(it)
        }

        viewModel.videoItemStarChangedLiveData.observe(viewLifecycleOwner) {
            Log.d("debug", "videoItemStarred = $it")
            videoAdapter.notifyItemChanged(it, fragmentType)
        }

        sharedViewModel.isInPictureInPictureMode.observe(
            viewLifecycleOwner
        ) { isInPictureInPictureMode ->
            if (isInPictureInPictureMode) {
                onFullScreenListener.enterFullScreen()
            } else {
                onFullScreenListener.exitFullScreen()
            }
        }

        viewModel.showInAppReviewLiveData.observe(viewLifecycleOwner) { show ->
            if (show) {
                showInAppReview()
            }
        }
    }

    private fun showInAppReview() {
        val reviewManager = ReviewManagerFactory.create(requireContext())
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { }
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.rate_popup_ask_title)
                    .setPositiveButton(R.string.rate_popup_ask_ok) { _, _ ->
                        val rateReviewIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.googleplay_url))
                        )
                        startActivity(rateReviewIntent)
                    }
                    .setNegativeButton(R.string.rate_popup_ask_no) { _, _ -> }
                    .show()
            }
        }
    }

    private fun initPlayerView() {
        lifecycle.addObserver(binding.playerView)
        initPictureInPicture(binding.playerView)
        binding.playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                youTubePlayerDisposable = viewModel.videoIdSubject
                    .filter { it.isNotEmpty() }
                    .subscribe({
                        youTubePlayer.cueVideo(it, 0f)
                    }, {})
            }
        })
        binding.playerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                onFullScreenListener.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) return
                onFullScreenListener.exitFullScreen()
            }
        })
    }

    private fun initPictureInPicture(playerView: YouTubePlayerView?) {
        var supportsPIP = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            supportsPIP =
                requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }
        if (!supportsPIP) return

        val pictureInPictureIcon = ImageView(requireContext())
        pictureInPictureIcon.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_picture_in_picture_24dp
            )
        )

        pictureInPictureIcon.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (supportsPIP) {
                    requireActivity().enterPictureInPictureMode(
                        PictureInPictureParams.Builder().build()
                    )
                }
            }
        }

//        playerView?.addView(pictureInPictureIcon)
    }


    private fun initRecyclerView() {
        videoAdapter = VideoAdapter(this, this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        infiniteScrollListener = InfiniteScrollListener(linearLayoutManager, this@VideoFragment)
        binding.recycler.apply {
            setHasFixedSize(true)
            adapter = videoAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    binding.recycler.context,
                    DividerItemDecoration.VERTICAL
                )
            )
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(infiniteScrollListener)
        }
    }

    override fun onLoadMore() {
        viewModel.loadMoreItems()
    }

    fun exitFullScreen() {
        binding.playerView.exitFullScreen()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.playerView.exitFullScreen()
        } else {
            binding.playerView.enterFullScreen()
        }
    }

    override fun onItemClick(videoItem: VideoUIModel) {
        Log.d("debug", "clicked $videoItem")
        if (videoItem.videoId != viewModel.videoIdSubject.value) viewModel.videoIdSubject.onNext(
            videoItem.videoId
        )
    }

    override fun onStarClick(videoItem: VideoUIModel) {
        viewModel.starClicked(videoItem)
    }

    override fun listIsEmpty() {
        toggleVisibility(true)
    }

    private fun toggleVisibility(listIsEmpty: Boolean) {
        with(binding) {
            playerView.isVisible = !listIsEmpty
            infoLayout.isVisible = !listIsEmpty
            emptyText.isVisible = listIsEmpty
        }
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