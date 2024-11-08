package org.lemonadestand.btb.features.dashboard.activities

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

import com.google.android.material.bottomnavigation.BottomNavigationView
import org.lemonadestand.btb.R

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.login.activities.LoginActivity
import org.lemonadestand.btb.utils.Utils

class DashboardActivity : BaseActivity(R.layout.activity_dashboard) {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var line1: TextView
    private lateinit var line2: TextView
    private lateinit var line3: TextView
    private lateinit var line4: TextView
    private lateinit var mainDrawer: DrawerLayout

    override fun onResume() {
        super.onResume()

        updateNavigationView()
    }

    override fun init() {
        super.init()

        initLayoutViews()
        setDrawerNavigation()
        setBottomNavigation()
        setBottomListener()
        handleBottomUiEvent(bottomNav.selectedItemId)

        mainDrawer = findViewById(R.id.main_drawer)
    }

    // Expose a method to control DrawerLayout visibility
    fun toggleDrawer() {
        if (mainDrawer.isDrawerOpen(GravityCompat.START)) {
            mainDrawer.closeDrawer(GravityCompat.START)
        } else {
            mainDrawer.openDrawer(GravityCompat.START)
        }
    }


    private fun setBottomListener() {

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    handleBottomUiEvent(R.id.homeFragment)
                }

                R.id.eventFragment -> {
                    navController.navigate(R.id.eventFragment)
                    handleBottomUiEvent(R.id.eventFragment)
                }
                R.id.interestFragment -> {
                    navController.navigate(R.id.interestFragment)
                    handleBottomUiEvent(R.id.interestFragment)
                }

                R.id.moreFragment -> {
                    navController.navigate(R.id.moreFragment)
                    handleBottomUiEvent(R.id.moreFragment)
                }
            }
            true
        }


    }

    private fun handleBottomUiEvent(id: Int) {

        when (id) {
            R.id.homeFragment -> {
                Log.e("clicked=>", "done")
                deselectAllColor()
                bottomNav.itemIconTintList = null
                resetAllIcons()
                bottomNav.menu.getItem(0).icon =
                    ContextCompat.getDrawable(this, R.drawable.nav_heart_selected)

                line1.setBackgroundColor(Color.rgb(222, 160, 55))
            }

            R.id.eventFragment -> {
                deselectAllColor()
                resetAllIcons()
                bottomNav.menu.getItem(1).icon =
                    ContextCompat.getDrawable(this, R.drawable.events_gold)
                bottomNav.itemIconTintList = null

                line2.setBackgroundColor(Color.rgb(222, 160, 55))
            }

            R.id.interestFragment -> {
                deselectAllColor()
                resetAllIcons()
                bottomNav.menu.getItem(2).icon =
                    ContextCompat.getDrawable(this, R.drawable.nav_interest_selected)
                bottomNav.itemIconTintList = null
                line3.setBackgroundColor(Color.rgb(10, 78, 120))

            }

            R.id.moreFragment -> {
                deselectAllColor()
                resetAllIcons()
                bottomNav.menu.getItem(3).icon = ContextCompat.getDrawable(this, R.drawable.ic_more_selected)
                bottomNav.itemIconTintList = null
                line4.setBackgroundColor(Color.rgb(51, 63, 79))
            }
        }

    }

    private fun setDrawerNavigation() {
        val navBtnTeam = findViewById<LinearLayout>(R.id.navBtnTeam)
        navBtnTeam.setOnSingleClickListener {
            toggleDrawer()
            navController.navigate(R.id.teamsFragment)
        }

        val navBtnCompanies = findViewById<LinearLayout>(R.id.navBtnCompanies)
        navBtnCompanies.setOnSingleClickListener {
            toggleDrawer()
            navController.navigate(R.id.companiesFragment)
        }

        val navBtnContacts = findViewById<LinearLayout>(R.id.navBtnContacts)
        navBtnContacts.setOnSingleClickListener {
            toggleDrawer()
            navController.navigate(R.id.contactsFragment)
        }

        val navBtnLogout = findViewById<LinearLayout>(R.id.navBtnLogout)
        navBtnLogout.setOnClickListener {
            toggleDrawer()
            handleLogout()
        }
    }

    private fun setBottomNavigation() {
        bottomNav.setupWithNavController(navController)
    }

    private fun initLayoutViews() {
        bottomNav = findViewById(R.id.bottom_navigation_dashboard)
        line1 = findViewById(R.id.line1)
        line2 = findViewById(R.id.line2)
        line3 = findViewById(R.id.line3)
        line4 = findViewById(R.id.line4)
        navController = findNavController(R.id.host_fragment)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun deselectAllColor() {
        line1.setBackgroundColor(Color.rgb(255, 255, 255))
        line2.setBackgroundColor(Color.rgb(255, 255, 255))
        line3.setBackgroundColor(Color.rgb(255, 255, 255))
        line4.setBackgroundColor(Color.rgb(255, 255, 255))
    }

    private fun resetAllIcons() {
        bottomNav.menu.getItem(0).icon =
            ContextCompat.getDrawable(this, R.drawable.nav_heart_unselected)
        bottomNav.menu.getItem(1).icon =
            ContextCompat.getDrawable(this, R.drawable.events)
        bottomNav.menu.getItem(2).icon =
            ContextCompat.getDrawable(this, R.drawable.nav_interest_unselected)
        bottomNav.menu.getItem(3).icon =
            ContextCompat.getDrawable(this, R.drawable.nav_more_unselected)
    }

    private fun updateNavigationView() {
        val currentUser = Utils.getUser(this) ?: return

        val navUserPictureView = findViewById<ImageView>(R.id.navUserPicture)
        if (currentUser.picture != null) {
            Glide.with(this).load(currentUser.picture).into(navUserPictureView)
        } else {
            currentUser.name?.let {
                Glide.with(this).load(it.trim().lowercase().getImageUrlFromName())
                    .into(navUserPictureView)
            }
        }

        val navUserNameView = findViewById<TextView>(R.id.navUserName)
        navUserNameView.text = currentUser.name

        val navOrgNameView = findViewById<TextView>(R.id.navOrgName)
        navOrgNameView.text = currentUser.organization.name

        val navGiveView = findViewById<TextView>(R.id.navGiveView)
        navGiveView.text = "$${currentUser.give}"

        val navSpendView = findViewById<TextView>(R.id.navSpendView)
        navSpendView.text = "$${currentUser.spend}"
    }

    private fun handleLogout() {
        Utils.saveData(this@DashboardActivity, Utils.TOKEN, null)
        Utils.saveUser(this@DashboardActivity, null)

        startActivity(LoginActivity::class.java)
        finish()
    }
}