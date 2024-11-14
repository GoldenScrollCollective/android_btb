package org.lemonadestand.btb.features.post.fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.databinding.FragmentHomeBinding
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.dashboard.views.FilterView
import org.lemonadestand.btb.features.post.activities.ShareStoryActivity
import org.lemonadestand.btb.features.post.activities.ShowAppreciationActivity
import org.lemonadestand.btb.singleton.Filter
import org.lemonadestand.btb.singleton.Singleton.launchActivity
import org.lemonadestand.btb.utils.Utils

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private lateinit var mBinding: FragmentHomeBinding

    private var currentFragment: Fragment? = null

    private var filter = Filter.PUBLIC
        set(value) {
            field = value
            if (currentFragment is CompanyTabFragment) {
                (currentFragment as CompanyTabFragment).refreshData(value)
            }
        }

    private var tabIndex = 0
        set(value) {
            field = value
            handleTabIndex()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )

        val currentUser = Utils.getUser(requireActivity())
        if (currentUser?.picture != null) {
            context?.let { Glide.with(it).load(currentUser.picture).into(mBinding.userSortName) }
        } else {
            context?.let { Glide.with(it).load(currentUser.name.trim().lowercase().getImageUrlFromName()).into(mBinding.userSortName) }
        }
        mBinding.userSortName.setOnClickListener {
            val dashboardActivity = requireActivity() as DashboardActivity
            dashboardActivity.toggleDrawer()
        }

        mBinding.btnFilter.setOnClickListener { view ->
            val filterView = FilterView(requireContext(), filter = filter)
            val popupWindow = PopupWindow(
                filterView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            popupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))) // Transparent color to remove default shadow
            popupWindow.elevation = 20f

            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            popupWindow.showAsDropDown(view, 0, 0, 0)

            filterView.onSelect = { value ->
                popupWindow.dismiss()
                filter = value
            }
        }

        mBinding.tabView.onSelect = {
            tabIndex = it
        }
        handleTabIndex()

        mBinding.btnFloatingPlus.setOnSingleClickListener {
            val view: View = LayoutInflater.from(context).inflate(R.layout.custom_public_menu, null)
            val popupWindow = PopupWindow(
                view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val location = IntArray(2)
            mBinding.btnFloatingPlus.getLocationOnScreen(location)
            popupWindow.showAtLocation(
                mBinding.btnFloatingPlus,
                Gravity.NO_GRAVITY,
                location[0],
                location[1] - (popupWindow.height + (mBinding.btnFloatingPlus.height * 3))
            )
            popupWindow.showAsDropDown(it, 100, 0, 0)
            val tvShareStory = view.findViewById<TextView>(R.id.tv_share_story)
            val tvShowAppreciation = view.findViewById<TextView>(R.id.tv_show_appreciation)
            tvShareStory.setOnClickListener {
                popupWindow.dismiss()
                (context as Activity).launchActivity<ShareStoryActivity>()
            }
            tvShowAppreciation.setOnClickListener {
                popupWindow.dismiss()
                (context as Activity).launchActivity<ShowAppreciationActivity>()
            }
        }

        return mBinding.root
    }

    private fun handleTabIndex() {
        when(tabIndex) {
            0 -> {
                if (currentFragment is CompanyTabFragment) return
                currentFragment = CompanyTabFragment()
                childFragmentManager.beginTransaction().replace(R.id.home_fragment, currentFragment!!).commit()
                mBinding.btnFilter.visibility = View.VISIBLE
            }
            1 -> {
                if (currentFragment is CommunityTabFragment) return
                currentFragment = CommunityTabFragment()
                childFragmentManager.beginTransaction().replace(R.id.home_fragment, currentFragment!!).commit()
                mBinding.btnFilter.visibility = View.GONE
            }
        }
    }
}