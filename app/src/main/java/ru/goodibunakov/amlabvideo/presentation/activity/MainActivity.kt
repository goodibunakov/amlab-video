package ru.goodibunakov.amlabvideo.presentation.activity

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
import kotlinx.android.synthetic.main.activity_main.networkIndicator
import kotlinx.android.synthetic.main.activity_splash.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.data.repositories.ConnectedStatus
import ru.goodibunakov.amlabvideo.presentation.fragments.AboutFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.MessagesFragment
import ru.goodibunakov.amlabvideo.presentation.fragments.VideoFragment
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel
import ru.goodibunakov.amlabvideo.presentation.viewmodels.SharedViewModel.Companion.ALL_VIDEOS


class MainActivity : AppCompatActivity() {

    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var profile: IProfile
    private val mainViewModel: MainViewModel by viewModels { AmlabApplication.viewModelFactory }
    private val sharedViewModel: SharedViewModel by viewModels { AmlabApplication.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initDrawer()

        mainViewModel.playlistsLiveData.observe(this, Observer {
            fillDrawer(savedInstanceState, it)
            sharedViewModel.playlistId.value = ALL_VIDEOS
        })

        sharedViewModel.playlistId.observe(this, Observer { tag ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (tag == ALL_VIDEOS) {
                if (currentFragment is VideoFragment) {
                    supportFragmentManager.beginTransaction()
                            .show(currentFragment)
                            .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, VideoFragment.newInstance(), TAG_VIDEO_FRAGMENT)
                            .commit()
                }
            }
            if (tag.contains(APP_MENU_ITEM)) {
                if (tag.contains(getString(R.string.about))) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, AboutFragment(), TAG_ABOUT_FRAGMENT)
                            .commit()
                }
                if (tag.contains(getString(R.string.messages))) {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, MessagesFragment(), TAG_MESSAGES_FRAGMENT)
                            .commit()
                }
            }
        })

        mainViewModel.networkLiveData.observe(this, Observer {
            showNetworkAvailable(it)
        })

        mainViewModel.toolbarTitleViewModel.observe(this, Observer { updateToolBarTitle(it) })
    }

    private fun initDrawer() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(
                this,
                root,
                toolbar,
                com.mikepenz.materialdrawer.R.string.material_drawer_open,
                com.mikepenz.materialdrawer.R.string.material_drawer_close
        )
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
                    sharedViewModel.let {
                        Log.d("debug", "click = $view")
                        Log.d("debug", "click = $drawerItem.")
                        it.playlistId.value = tag
                        Log.d("debug", "it.playlistId.value = $tag")
                        Log.d("debug", "${supportFragmentManager.backStackEntryCount}")
                    }
                    mainViewModel.toolbarTitleViewModel.value = drawerItem.name?.getText(this@MainActivity)
                }
                false
            }
            setSavedInstance(savedInstanceState)
        }
    }

    private fun updateToolBarTitle(name: String) {
        when {
            name == getString(R.string.new_videos) -> {
                toolbar.title = getString(R.string.new_videos)
            }
            name.contains(getString(R.string.messages)) -> {
                toolbar.title = getString(R.string.messages)
            }
            name.contains(getString(R.string.about)) -> {
                toolbar.title = getString(R.string.about)
            }
            else -> {
                toolbar.title = name
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
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

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (root.isDrawerOpen(slider)) {
            root.closeDrawer(slider)
        } else {
            super.onBackPressed()
        }
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

        TransitionManager.beginDelayedTransition(parentSplash, transition)

        networkIndicator.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    companion object {
        const val APP_MENU_ITEM = "app_menu"

        private const val TAG_VIDEO_FRAGMENT = "video_fragment"
        private const val TAG_ABOUT_FRAGMENT = "about_fragment"
        private const val TAG_MESSAGES_FRAGMENT = "messages_fragment"

    }
}