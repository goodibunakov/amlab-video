package ru.goodibunakov.amlabvideo.presentation.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.util.addStickyDrawerItems
import com.mikepenz.materialdrawer.widget.AccountHeaderView
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.fragments.PreferencesFragment


class MainActivity : MvpAppCompatActivity() {

    private lateinit var headerView: AccountHeaderView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var profile: IProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initDrawer(savedInstanceState)
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        actionBarDrawerToggle = ActionBarDrawerToggle(
                this,
                root,
                toolbar,
                com.mikepenz.materialdrawer.R.string.material_drawer_open,
                com.mikepenz.materialdrawer.R.string.material_drawer_close
        )

        profile = ProfileDrawerItem().apply {
            nameText = "GooDi"
            iconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.splash_logo)!!
        }.withEmail("goodibunakov@gmail.com1")

        // Create the AccountHeader
        headerView = AccountHeaderView(this, compact = true)
                .apply {
                    attachToSliderView(slider)
                    headerBackground = ImageHolder(R.drawable.header)
                    addProfiles(profile)
                    withSavedInstance(savedInstanceState)
                    accountSwitcherArrow.visibility = View.GONE
                    onAccountHeaderListener = null
                }

        slider.apply {
            itemAdapter.add(
                    PrimaryDrawerItem().withName("1").withIcon(R.drawable.splash_logo),
                    SecondaryDrawerItem().withName("2"),
                    SecondaryDrawerItem().withName("3"),
                    SecondaryDrawerItem().withName("4"),
                    SecondaryDrawerItem().withName("5").withIconTintingEnabled(true).withTag("Bullhorn"),
                    SecondaryDrawerItem().withName("6")
            )
            addStickyDrawerItems(
                    SecondaryDrawerItem().withName("7").withIdentifier(10)
            )
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
        menuInflater.inflate(R.menu.main_menu, menu)
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
                .add(PreferencesFragment(), "PreferencesFragment")
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