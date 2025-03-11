package org.lemonadestand.btb.features.dashboard.activities

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.activities.LoginActivity
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.extensions.setImageUrl
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.post.fragments.HomeFragmentDirections
import org.lemonadestand.btb.utils.Utils

class DashboardActivity : BaseActivity(R.layout.activity_dashboard) {

	private lateinit var bottomNav: BottomNavigationView
	private lateinit var navController: NavController
	private lateinit var line1: View
	private lateinit var line2: View
	private lateinit var line3: View
	private lateinit var mainDrawer: DrawerLayout

	override fun init() {
		super.init()

		initLayoutViews()
		setDrawerNavigation()
		setBottomListener()
		handleBottomUiEvent(R.id.homeFragment)

		mainDrawer = findViewById(R.id.main_drawer)
	}

	override fun update() {
		super.update()

		updateNavigationView()
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

				R.id.eventFragment -> {
					navController.navigate(R.id.eventFragment)
					handleBottomUiEvent(R.id.eventFragment)
				}

				R.id.homeFragment -> {
					navController.navigate(R.id.homeFragment)
					handleBottomUiEvent(R.id.homeFragment)
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

	private fun handleBottomUiEvent(currentId: Int) {
		deselectAllColor()

		when (currentId) {
			R.id.eventFragment -> {
				line1.setBackgroundColor(getColor(R.color.bottom_nav_active))
			}

			R.id.homeFragment -> {
				line2.setBackgroundColor(getColor(R.color.bottom_nav_active))
			}


			R.id.interestFragment -> {
				line3.setBackgroundColor(getColor(R.color.bottom_nav_active))
			}
		}

	}

	private fun setDrawerNavigation() {
		val navBtnTeam = findViewById<LinearLayout>(R.id.navBtnTeam)
		navBtnTeam.setOnSingleClickListener {
			toggleDrawer()
			navController.navigate(HomeFragmentDirections.toTeams())
		}

		val navBtnCompanies = findViewById<LinearLayout>(R.id.navBtnCompanies)
		navBtnCompanies.setOnSingleClickListener {
			toggleDrawer()
			navController.navigate(HomeFragmentDirections.toCompanies())
		}

		val navBtnContacts = findViewById<LinearLayout>(R.id.navBtnContacts)
		navBtnContacts.setOnSingleClickListener {
			toggleDrawer()
			navController.navigate(HomeFragmentDirections.toContacts())
		}

		val navBtnLogout = findViewById<LinearLayout>(R.id.navBtnLogout)
		navBtnLogout.setOnClickListener {
			toggleDrawer()
			handleLogout()
		}
	}

	private fun initLayoutViews() {
		navController = findNavController(R.id.host_fragment)
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
		navController = navHostFragment.navController

		bottomNav = findViewById(R.id.bottom_navigation_dashboard)
		bottomNav.setupWithNavController(navController)

		line1 = findViewById(R.id.line1)
		line2 = findViewById(R.id.line2)
		line3 = findViewById(R.id.line3)
	}

	private fun deselectAllColor() {
		line1.setBackgroundColor(Color.rgb(255, 255, 255))
		line2.setBackgroundColor(Color.rgb(255, 255, 255))
		line3.setBackgroundColor(Color.rgb(255, 255, 255))
	}

	private fun updateNavigationView() {
		val currentUser = Utils.getUser(this) ?: return

		val navUserPictureView = findViewById<ImageView>(R.id.navUserPicture)
		navUserPictureView.setImageUrl(currentUser.pictureUrl)

		val navUserNameView = findViewById<TextView>(R.id.navUserName)
		navUserNameView.text = currentUser.name

		val navOrgNameView = findViewById<TextView>(R.id.navOrgName)
		navOrgNameView.text = currentUser.organization?.name

		currentUser.stats?.let {
			val totalImpactView = findViewById<TextView>(R.id.totalImpactView)
			totalImpactView.text = String.format("$%.0f", it.dollarImpact)

			val pendingExpensesView = findViewById<TextView>(R.id.pendingExpensesView)
			pendingExpensesView.text = "$0"

			val livesBlessedView = findViewById<TextView>(R.id.livesBlessedView)
			livesBlessedView.text = "${it.livesBlessed}"

			val recognizedOthersView = findViewById<TextView>(R.id.recognizedOthersView)
			recognizedOthersView.text = "${it.appreciation}"

			val recognizedByOthersView = findViewById<TextView>(R.id.recognizedByOthersView)
			recognizedByOthersView.text = "${it.recognized}"

			val storiesSharedView = findViewById<TextView>(R.id.storiesSharedView)
			storiesSharedView.text = "${it.stories}"
		}

		currentUser.fintechCard?.let {
			val debitCardBalanceView = findViewById<TextView>(R.id.debitCardBalanceView)
			debitCardBalanceView.text = String.format("$%.0f", it.btbCardBalance)
		}

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