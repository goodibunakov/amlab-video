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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import kotlinx.android.synthetic.main.activity_main.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.fragments.AboutFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.MessagesFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.OnFullScreenListener
import ru.goodibunakov.amlabvideo.presentation.fragments.VideoFragment
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI
import ru.goodibunakov.amlabvideo.presentation.utils.FullScreenHelper
import ru.goodibunakov.amlabvideo.presentation.utils.setValidatedValue
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel.Companion.ALL_VIDEOS
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel


class MainActivity : AppCompatActivity(), OnFullScreenListener {

    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var profile: IProfile

    private val mainViewModel: MainViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val sharedViewModel: SharedViewModel by viewModels { AmlabApplication.viewModelFactory }

    private var isFullscreen = false
    private lateinit var fullScreenHelper: FullScreenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fullScreenHelper = FullScreenHelper(this, appBarLayout)
        setSupportActionBar(toolbar)
        initDrawer()

        mainViewModel.playlistsLiveData.observe(this, Observer {
            fillDrawer(savedInstanceState, it)
            sharedViewModel.playlistId.setValidatedValue(ALL_VIDEOS)
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
            }
        })

        mainViewModel.networkLiveData.observe(this, Observer {
            showNetworkAvailable(it)
        })

        mainViewModel.toolbarTitleLiveData.observe(this, Observer { updateToolBarTitle(it) })
    }

    private fun initDrawer() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, root, toolbar, com.mikepenz.materialdrawer.R.string.material_drawer_open, com.mikepenz.materialdrawer.R.string.material_drawer_close)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            rootLayout.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
    }

    private fun fillDrawer(savedInstanceState: Bundle?, playlists: List<PlaylistsModelUI>) {

        profile = ProfileDrawerItem().apply {
//            nameText = "GooDi"
            iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.mipmap.ic_launcher)!!
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
            itemAdapter.add(
                    PrimaryDrawerItem().apply {
                        nameRes = R.string.new_videos
                        isSelected = true
                        tag = ALL_VIDEOS
                    },
                    DividerDrawerItem())
            playlists.map {
                itemAdapter.add(
                        SecondaryDrawerItem().apply {
                            nameText = it.title
                            tag = it.id
                        }
                )
            }
            itemAdapter.add(
                    DividerDrawerItem(),
                    SecondaryDrawerItem().apply {
                        nameRes = R.string.messages
                        tag = APP_MENU_ITEM + "_${getString(R.string.messages)}"
                    },
                    SecondaryDrawerItem().apply {
                        nameRes = R.string.about
                        tag = APP_MENU_ITEM + "_${getString(R.string.about)}"
                    }
            )
            onDrawerItemClickListener = { view, drawerItem, position ->
                val tag = drawerItem.tag as String
                if (drawerItem is Nameable) {
                    mainViewModel.let {
                        Log.d("debug", "onDrawerItemClickListener click = $view")
                        Log.d("debug", "onDrawerItemClickListener click = $drawerItem.")
                        sharedViewModel.playlistId.setValidatedValue(tag)
                        Log.d("debug", "onDrawerItemClickListener it.playlistId.value = $tag")
                        Log.d("debug", "onDrawerItemClickListener ${supportFragmentManager.backStackEntryCount}")
                    }
                    mainViewModel.toolbarTitleLiveData.value = drawerItem.name?.getText(this@MainActivity)
                }
                false
            }
            setSavedInstance(savedInstanceState)
        }
    }

    private fun updateToolBarTitle(name: String) {
        when {
            name == getString(R.string.new_videos) -> {
                supportActionBar?.title = getString(R.string.new_videos)
            }
            name.contains(getString(R.string.messages)) -> {
                supportActionBar?.title = getString(R.string.messages)
            }
            name.contains(getString(R.string.about)) -> {
                supportActionBar?.title = getString(R.string.about)
            }
            else -> {
                supportActionBar?.title = name
            }
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuAbout -> {
//                showAboutScreen()
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


    private fun showNetworkAvailable(isAvailable: ConnectedStatus) {
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

        if (isAvailable == ConnectedStatus.YES)
            showNetworkIndicator(false)
        else
            showNetworkIndicator(true)
    }

    private fun showNetworkIndicator(isShow: Boolean) {
        val transition = Slide(Gravity.TOP)
                .apply {
                    duration = 400
                    addTarget(R.id.networkIndicator)
                    if (!isShow) startDelay = 700
                }

        TransitionManager.beginDelayedTransition(root, transition)

        networkIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    companion object {
        const val APP_MENU_ITEM = "app_menu"

        private const val TAG_VIDEO_FRAGMENT = "video_fragment"
        private const val TAG_ABOUT_FRAGMENT = "about_fragment"
        private const val TAG_MESSAGES_FRAGMENT = "messages_fragment"
        private const val RECOVERY_DIALOG_REQUEST = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_DIALOG_REQUEST) recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        actionBarDrawerToggle.onConfigurationChanged(newConfig)

//        isFullscreen = if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            exitFullScreen()
//            false
//        } else {
//            enterFullScreen()
//            true
//        }
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
}