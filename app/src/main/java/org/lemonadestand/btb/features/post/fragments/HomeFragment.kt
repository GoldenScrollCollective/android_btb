package org.lemonadestand.btb.features.post.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.databinding.FragmentHomeBinding
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.dashboard.dialog.FilterDialog
import org.lemonadestand.btb.features.dashboard.views.FilterView
import org.lemonadestand.btb.singleton.Filter
import org.lemonadestand.btb.utils.Utils


class HomeFragment : Fragment(){

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var me: ImageView
    private lateinit var currentFragment: Fragment

    private var filter = Filter.PUBLIC

    private lateinit var mBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        setDefaultFragment()
        handleClicks()


//        return mBinding.root

//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//        drawerLayout = view.findViewById(R.id.drawer_layout)
//        navigationView = view.findViewById(R.id.nav_view)

//        drawerLayout = mBinding.drawerLayout;
//        navigationView = mBinding.navView;


        // Create ActionBarDrawerToggle and set it to the DrawerLayout
//        actionBarDrawerToggle = ActionBarDrawerToggle(
//            requireActivity(),
//            drawerLayout,
//            R.string.nav_open,
//            R.string.nav_close
//        )
//
//        drawerLayout.addDrawerListener(actionBarDrawerToggle)
//        actionBarDrawerToggle.syncState()



//        // Set a listener for navigation item clicks
//        navigationView.setNavigationItemSelectedListener { item ->
//            // Handle item clicks here
//            drawerLayout.closeDrawers()
//            true
//        }

//        me = view.findViewById(R.id.userSortName);

        me = mBinding.userSortName;
        var currentUser = Utils.getUser(requireActivity())
        if (currentUser.picture != null) {
            context?.let { Glide.with(it).load(currentUser.picture).into(me) }
        } else {
            context?.let { Glide.with(it).load(currentUser.name.trim().lowercase().getImageUrlFromName()).into(me) }
        }
        me.setOnClickListener {

            val dashboardActivity = activity as? DashboardActivity
            dashboardActivity?.toggleDrawer()

//            if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
//                drawerLayout.openDrawer(Gravity.LEFT)
//
//
//            } else {
////                drawerLayout.closeDrawer(Gravity.LEFT)
//            }
        }

        return mBinding.root
    }

    private fun setDefaultFragment() {
        setFragment(PublicFragment())
        setDefaultView()
        mBinding.tvPublic.alpha = 1f
        mBinding.tvPublic.elevation = 5f
        mBinding.tvPublic.setBackgroundResource(R.drawable.back_for_all)
        mBinding.tvPublic.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
    }

    private fun handleClicks() {
        mBinding.tvPublic.setOnClickListener { view ->
            setFragment(PublicFragment())
            setDefaultView()
            view.alpha = 1f
            view.elevation = 5f
            view.setBackgroundResource(R.drawable.back_for_all)
            mBinding.tvPublic.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            mBinding.btnFilter.visibility = View.VISIBLE
        }
        mBinding.tvPrivate.setOnClickListener { view ->
            setFragment(PrivateFragment())
            setDefaultView()
            view.alpha = 1f
            view.elevation = 5f
            view.setBackgroundResource(R.drawable.back_for_all)
            mBinding.tvPrivate.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            mBinding.btnFilter.visibility = View.GONE
        }
        mBinding.tvMine.setOnClickListener { view ->
            setFragment(MineFragment())
            setDefaultView()
            view.alpha = 1f
            view.elevation = 5f
            view.setBackgroundResource(R.drawable.back_for_all)
            mBinding.tvMine.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        }
        mBinding.tvArchived.setOnClickListener { view ->
            setFragment(ArchivedFragment())
            setDefaultView()
            view.alpha = 1f
            view.elevation = 5f
            view.setBackgroundResource(R.drawable.back_for_all)
            mBinding.tvArchived.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        }
        mBinding.btnFilter.setOnClickListener { view ->
//            val filterDialog = FilterDialog(view, requireContext())
//            filterDialog.onSelect = { value ->
//                (currentFragment as? PublicFragment)?.refreshData(value)
//            }
//            filterDialog.show()

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
                filter = value
                (currentFragment as? PublicFragment)?.refreshData(value)
                popupWindow.dismiss()
            }
        }
    }

    private fun setFragment(fragment: Fragment?) {
        fragment?.let { it ->
            currentFragment = it
            childFragmentManager.beginTransaction().replace(R.id.home_fragment, it).commit()
        }
    }

    private fun setDefaultView() {
        mBinding.tvPublic.alpha = 0.7f
        mBinding.tvPublic.setBackgroundResource(0)
        mBinding.tvPrivate.alpha = 0.7f
        mBinding.tvPrivate.setBackgroundResource(0)
        mBinding.tvMine.alpha = 0.7f
        mBinding.tvMine.setBackgroundResource(0)
        mBinding.tvArchived.alpha = 0.7f
        mBinding.tvArchived.setBackgroundResource(0)
        mBinding.tvPrivate.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
        mBinding.tvPublic.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
        mBinding.tvMine.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
        mBinding.tvArchived.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
    }
}