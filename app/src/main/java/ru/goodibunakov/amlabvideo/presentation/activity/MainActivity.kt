package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import kotlinx.android.synthetic.main.activity_main.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.NotificationOpenedHandler.Companion.INTENT_FROM_NOTIFICATION
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
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
    private var isDrawerFirstInit: Boolean = true //для установки активным первого пунтка в drawer если initDrawer вызван не после "обновить плейлисты"

    override val viewModel: MainViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val sharedViewModel: SharedViewModel by viewModels { AmlabApplication.viewModelFactory }

    private var isFullscreen = false
    private lateinit var fullScreenHelper: FullScreenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fullScreenHelper = FullScreenHelper(this, appBarLayout)

        initDrawerAndToolbar()

        viewModel.playlistsLiveData.observe(this, Observer {
            fillDrawer(savedInstanceState, it)
        })

        sharedViewModel.playlistId.observe(this, Observer { tag ->
            if (!tag.contains(APP_MENU_ITEM)) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, VideoFragment(), TAG_VIDEO_FRAGMENT)
                        .commit()
            } else if (tag.contains(getString(R.string.about))) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AboutFragment(), TAG_ABOUT_FRAGMENT)
                        .commit()
            } else if (tag.contains(getString(R.string.messages))) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MessagesFragment(), TAG_MESSAGES_FRAGMENT)
                        .commit()
            } else if (tag.contains(getString(R.string.about_channel))) {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AboutChannelFragment(), TAG_ABOUT_CHANNEL_FRAGMENT)
                        .commit()
            }
        })

        viewModel.toolbarTitleLiveData.observe(this, Observer { updateToolBarTitle(it) })

        viewModel.playlistsUpdatedLiveData.observe(this, Observer {
            showPlaylistUpdated(it)
        })
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
            viewModel.drawerInitializedLiveData.observe(this, Observer {
                if (it) {
                    if (fragment == null || fragment !is MessagesFragment) {
                        Log.d("debug", "setSelection(IDENTIFIER_MESSAGES, true)")
                        slider.setSelectionAtPosition(-1, false)
                        slider.setSelection(IDENTIFIER_MESSAGES, true)
                        isDrawerFirstInit = false
                    }
                }
            })
        }
    }


    private fun initDrawerAndToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, root, toolbar, com.mikepenz.materialdrawer.R.string.material_drawer_open, com.mikepenz.materialdrawer.R.string.material_drawer_close)
        actionBarDrawerToggle.isDrawerSlideAnimationEnabled = true
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                rootLayout.updatePadding(top = insets.systemWindowInsetTop)
            } else {
                slider.insetForeground = null
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
                    attachToSliderView(slider)
                    headerBackground = ImageHolder(R.drawable.drawer_header)
                    addProfiles(profile)
                    withSavedInstance(savedInstanceState)
                    selectionListEnabledForSingleProfile = false
                }

        slider.apply {
            if (itemAdapter.itemList.size() > 0) {
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
                    Log.d("debug", "onDrawerItemClickListener click = $drawerItem.")
                    sharedViewModel.playlistId.setValidatedValue(tag)
                    Log.d("debug", "onDrawerItemClickListener it.playlistId.value = $tag")
                    Log.d("debug", "onDrawerItemClickListener ${supportFragmentManager.backStackEntryCount}")

                    viewModel.toolbarTitleLiveData.value = drawerItem.name?.getText(this@MainActivity)
                }
                false
            }
            setSavedInstance(savedInstanceState)
        }

        val fromNotification = intent.getBooleanExtra(INTENT_FROM_NOTIFICATION, false)
        if (isDrawerFirstInit && !fromNotification) {
            slider.setSelection(IDENTIFIER_NEW_VIDEOS, true)
            isDrawerFirstInit = false
        }

        viewModel.drawerInitializedLiveData.setValidatedValue(true)
    }

    private fun updateToolBarTitle(name: String) {
        supportActionBar?.title = when {
            name.contains(getString(R.string.messages)) -> {
                getString(R.string.messages)
            }
            name.contains(getString(R.string.about)) -> {
                getString(R.string.about)
            }
            name.contains(getString(R.string.about_channel)) -> {
                getString(R.string.about_channel)
            }
            else -> {
                name
            }
        }
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
        when (item.itemId) {
            R.id.menuUpdatePlaylists -> {
                viewModel.updatePlaylistsToDatabase()
                return true
            }
            else -> {
                return actionBarDrawerToggle.onOptionsItemSelected(item)
            }
        }
    }


    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the drawer to the bundle
        outState = slider.saveInstanceState(outState)
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerView.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }


    override fun showNetworkAvailable(isAvailable: ConnectedStatus) {
        val indicatorColor = when (isAvailable) {
            ConnectedStatus.YES -> R.color.colorSuccess
            ConnectedStatus.NO -> R.color.colorError
            else -> android.R.color.black
        }
        networkIndicator.setBackgroundColor(ContextCompat.getColor(this, indicatorColor))

        networkIndicator.text = when (isAvailable) {
            ConnectedStatus.YES -> getString(R.string.network_indicator_yes)
            ConnectedStatus.NO -> getString(R.string.network_indicator_no)
            else -> ""
        }

        if (isAvailable == ConnectedStatus.YES) {
            showNetworkIndicator(false)
            fragmentContainer.visibility = View.VISIBLE
            noInternet.visibility = View.GONE
        } else {
            showNetworkIndicator(true)
            fragmentContainer.visibility = View.INVISIBLE
            noInternet.visibility = View.VISIBLE
        }
    }

    private fun showNetworkIndicator(isShow: Boolean) {
        val transition = Slide(Gravity.BOTTOM)
                .apply {
                    duration = 500
                    addTarget(R.id.networkIndicator)
                    if (!isShow) startDelay = 700
                }

        TransitionManager.beginDelayedTransition(root, transition)

        networkIndicator.setVisibility(isShow)
    }

    companion object {
        const val APP_MENU_ITEM = "app_menu"
        const val IDENTIFIER_NEW_VIDEOS = 0L
        const val IDENTIFIER_MESSAGES = 100L
        const val IDENTIFIER_ABOUT_CHANNEL = 101L
        const val IDENTIFIER_ABOUT = 102L

        private const val TAG_VIDEO_FRAGMENT = "video_fragment"
        private const val TAG_ABOUT_FRAGMENT = "about_fragment"
        private const val TAG_ABOUT_CHANNEL_FRAGMENT = "about_channel"
        private const val TAG_MESSAGES_FRAGMENT = "messages_fragment"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_DIALOG_REQUEST) recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toolbar?.menu?.close()

        actionBarDrawerToggle.onConfigurationChanged(newConfig)


        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) is VideoFragment) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                exitFullScreen()
            } else {
                if (root.isDrawerOpen(slider)) root.closeDrawer(slider)
                enterFullScreen()
            }
        }
    }

    override fun onBackPressed() {
        when {
            root.isDrawerOpen(slider) -> {
                root.closeDrawer(slider)
            }
            isFullscreen -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
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
        isFullscreen = true
        fullScreenHelper.enterFullScreen()
    }

    override fun exitFullScreen() {
        isFullscreen = false
        fullScreenHelper.exitFullScreen()
    }

    private fun showPlaylistUpdated(throwable: Throwable?) {
        if (throwable != null) {
            Snackbar.make(root, getString(R.string.playlists_update_error), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(root, getString(R.string.playlists_update_success), Snackbar.LENGTH_SHORT).show()
        }
    }
}