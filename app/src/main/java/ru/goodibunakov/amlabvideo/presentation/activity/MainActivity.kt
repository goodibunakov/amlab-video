package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.transition.Slide
import androidx.transition.TransitionManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.notifications.NotificationOpenedHandler.Companion.INTENT_FROM_NOTIFICATION
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.databinding.ActivityMainBinding
import ru.goodibunakov.amlabvideo.presentation.fragments.AboutChannelFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.AboutFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.MessagesFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.VideoFragment
import ru.goodibunakov.amlabvideo.presentation.interfaces.OnFullScreenListener
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI
import ru.goodibunakov.amlabvideo.presentation.utils.*
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel


/**
 * Переключение светлой-темной темы
 * https://proandroiddev.com/dark-mode-on-android-app-with-kotlin-dc759fc5f0e1
 * https://proandroiddev.com/implementing-dark-theme-in-your-android-application-ec2b4fefb6e3
 */

class MainActivity : BaseActivity<MainViewModel>(), OnFullScreenListener {

    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var profile: IProfile
    private var isDrawerFirstInit: Boolean =
        true //для установки активным первого пункта в drawer если initDrawer вызван не после "обновить плейлисты"
    private var selectedItemPosition: Int = -1

    override val viewModel: MainViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val sharedViewModel: SharedViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val binding by viewBinding(ActivityMainBinding::bind, R.id.activity_main_root)

    private var isFullscreen = false
    private var isPipActive = false
    private lateinit var fullScreenHelper: FullScreenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreenHelper = FullScreenHelper(this, binding.appBarLayout)

        initDrawerAndToolbar()

        viewModel.playlistsLiveData.observe(this) {
            fillDrawer(savedInstanceState, it)
        }

        sharedViewModel.playlistId.observe(this) { tag ->
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (!tag.contains(APP_MENU_ITEM)) {
                fragmentTransaction
                    .replace(
                        R.id.fragmentContainer,
                        VideoFragment.newInstance(VideoFragment.FragmentType.FROM_WEB),
                        TAG_VIDEO_FRAGMENT
                    )
                    .commit()
            } else if (tag.contains(getString(R.string.about))) {
                fragmentTransaction
                    .replace(R.id.fragmentContainer, AboutFragment(), TAG_ABOUT_FRAGMENT)
                    .commit()
            } else if (tag.contains(getString(R.string.messages))) {
                fragmentTransaction
                    .replace(R.id.fragmentContainer, MessagesFragment(), TAG_MESSAGES_FRAGMENT)
                    .commit()
            } else if (tag.contains(getString(R.string.about_channel))) {
                fragmentTransaction
                    .replace(
                        R.id.fragmentContainer,
                        AboutChannelFragment(),
                        TAG_ABOUT_CHANNEL_FRAGMENT
                    )
                    .commit()
            } else {
                fragmentTransaction
                    .replace(
                        R.id.fragmentContainer,
                        VideoFragment.newInstance(VideoFragment.FragmentType.FROM_DB),
                        TAG_VIDEO_FRAGMENT
                    )
                    .commit()
            }
        }

        viewModel.toolbarTitleLiveData.observe(this) { updateToolBarTitle(it) }

        viewModel.playlistsUpdatedLiveData.observe(this) {
            showPlaylistUpdated(it)
        }
    }

    override fun onResume() {
        super.onResume()
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        Log.d("debug", "fragment = $fragment")
        val fromNotification = intent.getBooleanExtra(INTENT_FROM_NOTIFICATION, false)
        Log.d("debug", "fromNotification = $fromNotification")
        if (fromNotification) {
            viewModel.drawerInitializedLiveData.observe(this) {
                if (it) {
                    if (fragment == null || fragment !is MessagesFragment) {
                        Log.d("debug", "setSelection(IDENTIFIER_MESSAGES, true)")
                        binding.slider.setSelectionAtPosition(-1, false)
                        binding.slider.setSelection(IDENTIFIER_MESSAGES, true)
                        isDrawerFirstInit = false
                    }
                }
            }
        }
    }


    private fun initDrawerAndToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.activityMainRoot,
            binding.toolbar,
            com.mikepenz.materialdrawer.R.string.material_drawer_open,
            com.mikepenz.materialdrawer.R.string.material_drawer_close
        )
        actionBarDrawerToggle.isDrawerSlideAnimationEnabled = true
        ViewCompat.setOnApplyWindowInsetsListener(binding.activityMainRoot) { view, insets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.rootLayout.updatePadding(top = insets.systemWindowInsetTop)
            } else {
                binding.slider.insetForeground = null
            }
            insets
        }
    }

    private fun fillDrawer(savedInstanceState: Bundle?, playlists: List<PlaylistsModelUI>) {

        profile = ProfileDrawerItem().apply {
//            nameText = "GooDi"
            iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_logo)!!
//            descriptionText = "goodibunakov@gmail.com"
        }

        // Create the AccountHeader
        headerView = AccountHeaderView(this, compact = true)
            .apply {
                attachToSliderView(binding.slider)
                headerBackground = ImageHolder(R.drawable.drawer_header)
                addProfiles(profile)
                withSavedInstance(savedInstanceState)
                selectionListEnabledForSingleProfile = false
            }

        binding.slider.apply {
            if (itemAdapter.itemList.size() > 0) {
                this.selectedItemPosition = selectedItemPosition
                Log.d("debug", "this.selectedItemPosition = ${this.selectedItemPosition}")
                itemAdapter.itemList.clear(0)
            }

            itemAdapter.add(
                GmailDrawerItemPrimary().apply {
                    nameText = playlists.first().title
                    tag = playlists.first().id
                    identifier = IDENTIFIER_NEW_VIDEOS
                },
                DividerDrawerItem()
            )
            playlists.drop(1).map {
                itemAdapter.add(
                    GmailDrawerItemSecondary().apply {
                        nameText = it.title
                        tag = it.id
                    }
                )
            }
            itemAdapter.add(
                DividerDrawerItem(),
                GmailDrawerItemSecondary().apply {
                    nameRes = R.string.stars
                    tag = APP_MENU_ITEM + "_${getString(R.string.stars)}"
                    iconRes = R.drawable.star_filled
                    isIconTinted = true
                    identifier = IDENTIFIER_STARS
                },
                GmailDrawerItemSecondary().apply {
                    nameRes = R.string.messages
                    tag = APP_MENU_ITEM + "_${getString(R.string.messages)}"
                    iconRes = R.drawable.message
                    isIconTinted = true
                    identifier = IDENTIFIER_MESSAGES
                },
                GmailDrawerItemSecondary().apply {
                    nameRes = R.string.about_channel
                    tag = APP_MENU_ITEM + "_${getString(R.string.about_channel)}"
                    iconRes = R.drawable.youtube
                    isIconTinted = true
                    identifier = IDENTIFIER_ABOUT_CHANNEL
                },
                GmailDrawerItemSecondary().apply {
                    nameRes = R.string.about
                    tag = APP_MENU_ITEM + "_${getString(R.string.about)}"
                    iconRes = R.drawable.information_outline
                    isIconTinted = true
                    identifier = IDENTIFIER_ABOUT
                }
            )
            onDrawerItemClickListener = { view, drawerItem, position ->
                val tag = drawerItem.tag as String
                if (drawerItem is Nameable) {
                    Log.d("debug", "onDrawerItemClickListener playlistId.setValidatedValue")
                    sharedViewModel.playlistId.setValidatedValue(tag)
                    Log.d("debug", "onDrawerItemClickListener it.playlistId.value = $tag")
                    Log.d(
                        "debug",
                        "onDrawerItemClickListener ${supportFragmentManager.backStackEntryCount}"
                    )

                    viewModel.toolbarTitleLiveData.value =
                        drawerItem.name?.getText(this@MainActivity)
                }
                false
            }
            setSavedInstance(savedInstanceState)
        }

        val fromNotification = intent.getBooleanExtra(INTENT_FROM_NOTIFICATION, false)
        if (isDrawerFirstInit && !fromNotification) {
            binding.slider.setSelection(IDENTIFIER_NEW_VIDEOS, true)
            isDrawerFirstInit = false
        }

        //восстановление выделенного пункта после обновления плейлистов
        if (this.selectedItemPosition != -1) {
            binding.slider.setSelectionAtPosition(-1, false)
            binding.slider.setSelectionAtPosition(this.selectedItemPosition, false)
            isDrawerFirstInit = false
        }

        viewModel.drawerInitializedLiveData.setValidatedValue(true)
    }

    private fun updateToolBarTitle(name: String) {
        supportActionBar?.title = name
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuUpdatePlaylists -> {
                viewModel.updatePlaylistsToDatabase()
                true
            }
            else -> {
                actionBarDrawerToggle.onOptionsItemSelected(item)
            }
        }
    }


    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the drawer to the bundle
        outState = binding.slider.saveInstanceState(outState)
        //add the values which need to be saved from the accountHeader to the bundle
        if (::headerView.isInitialized) outState = headerView.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }


    override fun showNetworkAvailable(isAvailable: ConnectedStatus) {
        val indicatorColor = when (isAvailable) {
            ConnectedStatus.YES -> R.color.colorSuccess
            ConnectedStatus.NO -> R.color.colorError
            else -> android.R.color.black
        }
        binding.networkIndicator.setBackgroundColor(ContextCompat.getColor(this, indicatorColor))

        binding.networkIndicator.text = when (isAvailable) {
            ConnectedStatus.YES -> getString(R.string.network_indicator_yes)
            ConnectedStatus.NO -> getString(R.string.network_indicator_no)
            else -> ""
        }

        if (isAvailable == ConnectedStatus.YES) {
            showNetworkIndicator(false)
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.noInternet.visibility = View.GONE
        } else {
            showNetworkIndicator(true)
            binding.fragmentContainer.visibility = View.INVISIBLE
            binding.noInternet.visibility = View.VISIBLE
        }
    }

    private fun showNetworkIndicator(isShow: Boolean) {
        val transition = Slide(Gravity.BOTTOM)
            .apply {
                duration = 500
                addTarget(R.id.networkIndicator)
                if (!isShow) startDelay = 700
            }

        TransitionManager.beginDelayedTransition(binding.activityMainRoot, transition)

        binding.networkIndicator.isVisible = isShow
    }

    companion object {
        const val APP_MENU_ITEM = "app_menu"
        const val IDENTIFIER_NEW_VIDEOS = 0L
        const val IDENTIFIER_STARS = 100L
        const val IDENTIFIER_MESSAGES = 101L
        const val IDENTIFIER_ABOUT_CHANNEL = 102L
        const val IDENTIFIER_ABOUT = 103L

        private const val TAG_VIDEO_FRAGMENT = "video_fragment"
        private const val TAG_ABOUT_FRAGMENT = "about_fragment"
        private const val TAG_ABOUT_CHANNEL_FRAGMENT = "about_channel"
        private const val TAG_MESSAGES_FRAGMENT = "messages_fragment"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_DIALOG_REQUEST) recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.toolbar.menu?.close()

        actionBarDrawerToggle.onConfigurationChanged(newConfig)


        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) is VideoFragment) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && !isPipActive) {
                exitFullScreen()
            } else {
                if (binding.activityMainRoot.isDrawerOpen(binding.slider)) binding.activityMainRoot.closeDrawer(
                    binding.slider
                )
                enterFullScreen()
            }
        }
    }

    override fun onBackPressed() {
        when {
            binding.activityMainRoot.isDrawerOpen(binding.slider) -> {
                binding.activityMainRoot.closeDrawer(binding.slider)
            }
            isFullscreen -> {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is VideoFragment) {
                    currentFragment.exitFullScreen()
                    isFullscreen = false
                } else {
                    super.onBackPressed()
                }
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun enterFullScreen() {
        if (isFullscreen) return

        isFullscreen = true
        fullScreenHelper.enterFullScreen()
    }

    override fun exitFullScreen() {
        isFullscreen = false
        fullScreenHelper.exitFullScreen()
    }

    private fun showPlaylistUpdated(throwable: Throwable?) {
        if (throwable != null) {
            Snackbar.make(
                binding.activityMainRoot,
                getString(R.string.playlists_update_error),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                binding.activityMainRoot,
                getString(R.string.playlists_update_success),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        isPipActive = isInPictureInPictureMode
        sharedViewModel.isInPictureInPictureMode.value = isInPictureInPictureMode
    }

    override fun layoutResId() = R.layout.activity_main
}