package org.lemonadestand.btb.features.interest.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.common.adapter.ContactAdapter
import org.lemonadestand.btb.databinding.FragmentContactBinding
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.extenstions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel
import org.lemonadestand.btb.singleton.Singleton

class ContactFragment : Fragment() {
    lateinit var mBinding: FragmentContactBinding
    private lateinit var contactAdapter: ContactAdapter
    private var shortAnimationDuration: Int = 0
    private var tag: String = "TeamFragment"
    private lateinit var viewModel: UserViewModel
    private var userList = ArrayList<UserListModel>()
    private var userTemp = ArrayList<UserListModel>()
    private lateinit var callback: OnItemClickListener

    private var  isEvent: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        getData()
        setAdapter()
        setUpViewModel()
        contactAdapter.setOnItemClick(this.callback)
        return mBinding.root

    }

    private fun getData() {
        val args = arguments
        isEvent = args!!.getBoolean("is_event", false)
    }

    private fun startLoading() {
        mBinding.simmerLayout.startShimmer()
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
        viewModel.getContactList(page = 0)
        viewModel.contactResponseModel.observe(viewLifecycleOwner) {
            if (it.data.isNotEmpty()) {
                userList.clear()
                userTemp.clear()
                userList.addAll(it.data)
                userTemp.addAll(it.data)
                val selection: UserListModel? = if (isEvent) {
                    userList.firstOrNull { its -> its.uniq_id == Utils.getUserIdEvent(context) }
                } else {
                    userList.firstOrNull { its -> its.uniq_id == Utils.getUserIdInterest(context) }
                }
                if (selection != null) {
                    selection.isSelected = true
                }
                contactAdapter.notifyDataSetChanged()
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
        mBinding.simmerLayout.startShimmer()
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        contactAdapter = ContactAdapter(userList, context = requireContext(),isEvent)
        mBinding.rvUserList.adapter = contactAdapter
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

        mBinding.simmerLayout.animate()
            .alpha(0f)
            .setDuration(650)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mBinding.simmerLayout.hide()
                }
            })
    }



    fun setCallback(callback: OnItemClickListener) {
        Log.e("here=>","done")
        this.callback = callback

    }




}