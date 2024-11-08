package org.lemonadestand.btb.features.event.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.event.adapter.EventAdapter
import org.lemonadestand.btb.databinding.FragmentPastEventBinding
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.event.activities.EditReminderActivity
import org.lemonadestand.btb.features.event.models.EventModel
import org.lemonadestand.btb.features.event.models.EventModelDate
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.EventRepository
import org.lemonadestand.btb.mvvm.viewmodel.EventViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Singleton.launchActivity


class PastEventFragment : Fragment(), OnItemClickListener {

    lateinit var mBinding: FragmentPastEventBinding
    private var user: UserListModel? = null

    private lateinit var eventAdapter: EventAdapter
    private lateinit var viewModel: EventViewModel
    private var shortAnimationDuration: Int = 0
    private var eventDateList: ArrayList<EventModelDate> = ArrayList()
    private var tag = "PastEventFragment"
    private var clickType = ClickType.COMMON
    private var clickedPosition = 0
    private var clickedSuperPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPastEventBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        getSelectedUser()
        setUpPublicAdapter()
        setUpViewModel()
        setButtonClicks()
        setSwipeRefresh()

        return mBinding.root
    }

    private fun getSelectedUser() {
        val bundle = arguments
        user = bundle!!.getParcelable("user")
    }

    @SuppressLint("InflateParams")
    private fun setButtonClicks() {

    }

    private fun setUpPublicAdapter() {

        eventAdapter = EventAdapter(eventDateList, requireContext())
        eventAdapter.setOnItemClick(this)
        mBinding.rvPastEvent.adapter = eventAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpViewModel() {
        startLoading()
        val repository = EventRepository()
        val viewModelProviderFactory =
            CommonViewModelFactory((context as DashboardActivity).application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]


        viewModel.getPastEventList(
            PastEventBody(
                limit = Singleton.API_LIST_LIMIT,
                page = "0",
                sort = "desc", //desc //asc
                order_by = "start",
                resource = if (user != null) "user/${user!!.uniq_id}" else "",
                completed = "0",
                archive = "1"
            )
        )
        viewModel.pastEventModel.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                eventDateList.clear()

                val dateList: ArrayList<String> = ArrayList()

                for (i in 0 until it.data.size) {

                    if (it.data[i].blessing_complete != null) {
                        if (!dateList.contains(getDate(it.data[i].blessing_complete!!))) {
                            dateList.add(getDate(it.data[i].blessing_complete!!))
                            eventDateList.add(
                                EventModelDate(
                                    date = it.data[i].blessing_complete!!,
                                    eventList = it.data as ArrayList<EventModel>
                                )
                            )
                        }
                    } else {
                        if (!dateList.contains(getDate(it.data[i].start))) {
                            dateList.add(getDate(it.data[i].start))
                            eventDateList.add(
                                EventModelDate(
                                    date = it.data[i].start,
                                    eventList = it.data as ArrayList<EventModel>
                                )
                            )
                        }
                    }

                }
                eventAdapter.notifyDataSetChanged()
                stopLoading(true)
            } else {
                stopLoading(false)

            }
        }


        viewModel.liveError.observe(viewLifecycleOwner) {
            Singleton.handleResponse(response = it, context as Activity, tag)
            ProgressDialogUtil.dismissProgressDialog()
        }

        viewModel.commonResponse.observe(viewLifecycleOwner) {
            handleCommonResponse(context as DashboardActivity, it)
            ProgressDialogUtil.dismissProgressDialog()
            if (it.status == Singleton.SUCCESS) {
                if (clickType == ClickType.DELETE_EVENT) {
                    eventDateList[clickedSuperPosition].eventList.removeAt(clickedPosition)
                    eventAdapter.updateData(eventDateList)
                }
            }

        }


        viewModel.isLoading.observe(viewLifecycleOwner) {
            Log.e("value==>", it.toString())
            if (it) {
                ProgressDialogUtil.showProgressDialog(context as DashboardActivity)
            } else {
                ProgressDialogUtil.dismissProgressDialog()
            }
        }

        viewModel.noInternet.observe(viewLifecycleOwner) {
            Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
            ProgressDialogUtil.dismissProgressDialog()
        }
    }


    private fun startLoading() {
        mBinding.rvPastEvent.hide()
        mBinding.noDataView.root.hide()
        mBinding.simmerLayout.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(0)
                .setListener(null)
        }
        mBinding.simmerLayout.startShimmer()

    }

    private fun setSwipeRefresh() {
        mBinding.swipeRefresh.setOnRefreshListener {
            refreshData()
            mBinding.swipeRefresh.isRefreshing = false
        }
    }

    private fun refreshData() {
        startLoading()
        viewModel.getPastEventList(
            PastEventBody(
                limit = Singleton.API_LIST_LIMIT,
                page = "0",
                sort = "asc", //desc //asc
                order_by = "start",
                resource = if (user != null) "user/${user!!.uniq_id}" else "",
                completed = "0",
                archive = "1"
            )
        )
    }

    private fun stopLoading(isDataAvailable: Boolean) {
        val view = if (isDataAvailable) mBinding.rvPastEvent else mBinding.noDataView.root
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        mBinding.simmerLayout.animate()
            .alpha(0f)
            .setDuration(650)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mBinding.simmerLayout.hide()
                }
            })
    }

    override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {

        clickedPosition = index
        clickedSuperPosition = superIndex
        clickType = type
        when (type) {
            ClickType.DELETE_EVENT -> {
                val postModel = `object` as EventModel
                viewModel.deleteEvent(postModel.uniq_id)
            }
            ClickType.EDIT_EVENT -> {
                val eventModel  = `object` as EventModel
                activity?.launchActivity<EditReminderActivity>()
                {
                    putExtra("event_data",eventModel)
                }

            }
            else -> {


            }
        }


    }


}