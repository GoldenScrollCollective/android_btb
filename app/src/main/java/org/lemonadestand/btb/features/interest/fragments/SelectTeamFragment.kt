package org.lemonadestand.btb.features.interest.fragments

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
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.common.adapter.TeamAdapter
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.databinding.FragmentSelectTeamBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel
import org.lemonadestand.btb.singleton.Singleton

class SelectTeamFragment : Fragment() {

    lateinit var mBinding: FragmentSelectTeamBinding
    private lateinit var teamAdapter: TeamAdapter
    private var shortAnimationDuration: Int = 0
    private var tag: String = "TeamFragment"
    private lateinit var viewModel: UserViewModel
    private var userList = ArrayList<UserListModel>()
    private var userTemp = ArrayList<UserListModel>()
    private lateinit var callback: OnItemClickListener
    var tempUser: UserListModel? = null
    var allUser: UserListModel? = null

    private var isEvent: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSelectTeamBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )

        getData()
        setAdapter()
        setUpViewModel()
        teamAdapter.setOnItemClick(this.callback)
        return mBinding.root

    }

    private fun startLoading() {
        mBinding.shimmerLayout.startShimmer()
        mBinding.rvUserList.hide()
        mBinding.noDataView.hide()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpViewModel() {
        startLoading()
        val repository = UserRepository()
        val viewModelProviderFactory =
            CommonViewModelFactory((requireActivity()).application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
        viewModel.getUserList(page = 0)
        viewModel.userResponseModel.observe(viewLifecycleOwner) {
            if (!it.data.isNullOrEmpty()) {
                userList.clear()
                userTemp.clear()

                userList.addAll(it.data)
                val result =
                    userList.firstOrNull { its -> its.uniqueId == Utils.getData(context, Utils.UID) }
                if (result != null) {
                    tempUser = UserListModel(
                        id = result.id, uniqueId = result.uniqueId, org_id = result.org_id,
                        active = result.active,
                        name = "Me",
                        handles = result.handles,
                        public = result.public,
                        give = result.give,
                        address = result.address,
                        addressShipping = result.addressShipping,
                        phone = result.phone,
                        spend = result.spend,
                        picture = result.picture,
                        username = result.username
                    )
                }

                if (isEvent) {

                    allUser = UserListModel(
                        id = "", uniqueId = "", org_id = "",
                        active = "",
                        name = "All Users",
                        public = null,
                        give = "",
                        phone = "",
                        spend = "",
                        picture = Utils.getUser(context).organization.picture,
                        username = "",
                        isSelected = true
                    )
                }






                if (allUser != null) {
                    userList.add(0, allUser!!)
                    tempUser!!.isSelected = false
                    userList.add(1, tempUser!!)

                } else {
                    userList.add(0, tempUser!!)
                }
                userList.remove(result)


                val selection: UserListModel? = if (isEvent) {
                    Log.e("event_id==>1", Utils.getUserIdEvent(context) + "<-" )
                    userList.firstOrNull { its -> its.uniqueId == Utils.getUserIdEvent(context) }
                } else {
                    Log.e("event_id==>", Utils.getUserIdInterest(context)+ "<-")
                    userList.firstOrNull { its -> its.uniqueId == Utils.getUserIdInterest(context) }
                }

                if (selection != null) {
                    selection.isSelected = true
                    allUser?.isSelected = false

                } else {
                    if (isEvent) {
                        if (Utils.getUserIdEvent(context) == null) {
                            allUser!!.isSelected = true
                        }

                    } else {
                        if (Utils.getUserIdInterest(context) == null) {
                            tempUser!!.isSelected = true
                        }
                    }
                }
                userTemp.addAll(it.data)
                teamAdapter.notifyDataSetChanged()
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
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
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

    private fun setAdapter() {
        mBinding.shimmerLayout.startShimmer()
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        teamAdapter = TeamAdapter(userList, context = requireContext(), isEvent)
        mBinding.rvUserList.adapter = teamAdapter
    }


    private fun stopLoading(isDataAvailable: Boolean) {
        val view = if (isDataAvailable) mBinding.rvUserList else mBinding.noDataView
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }

        mBinding.shimmerLayout.animate()
            .alpha(0f)
            .setDuration(650)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mBinding.shimmerLayout.hide()
                }
            })
    }

    private fun getData() {
        val args = arguments
        isEvent = args!!.getBoolean("is_event", false)
    }


    fun setCallback(callback: OnItemClickListener) {
        Log.e("here=>", "done")
        this.callback = callback

    }


}