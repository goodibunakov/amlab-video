package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import ru.goodibunakov.amlabvideo.presentation.fragments.AboutFragment
import ru.goodibunakov.amlabvideo.presentation.model.PlaylistsModelUI
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var profile: IProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initDrawer()

        val mainViewModel: MainViewModel by viewModels { AmlabApplication.viewModelFactory }

        mainViewModel.playlistsLiveData.observe(this, Observer {
            fillDrawer(savedInstanceState, it)
        })
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
            nameText = "GooDi"
            iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.mipmap.ic_launcher)!!
            descriptionText = "goodibunakov@gmail.com1"
        }

        // Create the AccountHeader
        headerView = AccountHeaderView(this, compact = true)
                .apply {
                    attachToSliderView(slider)
                    headerBackground = ImageHolder(R.drawable.header)
                    addProfiles(profile)
                    withSavedInstance(savedInstanceState)
                    selectionListEnabledForSingleProfile = false
                }

        slider.apply {
            itemAdapter.add(
                    PrimaryDrawerItem().apply {
                        nameText = "Новые видео"
                        isSelected = true
                    }, DividerDrawerItem())
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
                        nameRes = R.string.about
                    }
            )
//            itemAdapter.add(
//                    PrimaryDrawerItem().apply {
//                        nameText = "1"
//                        isSelected = true
//                    },
//                    SecondaryDrawerItem().withName("2"),
//                    SecondaryDrawerItem().withName("3"),
//                    SecondaryDrawerItem().withName("4"),
//                    DividerDrawerItem(),
//                    SecondaryDrawerItem().apply {
//                        nameText = "5"
//                        tag = "id5"
//                    },
//                    SecondaryDrawerItem().withName("6"),
//                    DividerDrawerItem(),
//                    SecondaryDrawerItem().apply {
//                        nameRes = R.string.about
//                    }
//            )
            onDrawerItemClickListener = { view, drawerItem, position ->
                if (drawerItem is Nameable) {
                    Toast.makeText(this@MainActivity, drawerItem.name?.getText(this@MainActivity), Toast.LENGTH_SHORT).show()
                }
                false
            }
            setSavedInstance(savedInstanceState)
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
                showAboutScreen()
                return true
            }
            else -> {
                return actionBarDrawerToggle.onOptionsItemSelected(item)
            }
        }
    }

    private fun showAboutScreen() {
        supportFragmentManager.beginTransaction()
                .add(AboutFragment(), "AboutFragment")
                .commit()
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
}