package org.lemonadestand.btb.features.interest.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.InterestFieldType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.databinding.FragmentInterestBinding
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.interest.adapter.InterestUiAdapter
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener

import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.Field
import org.lemonadestand.btb.features.common.models.body.UpdateInterestBody
import org.lemonadestand.btb.features.interest.models.InterestModel
import org.lemonadestand.btb.features.interest.models.Option
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.InterestRepository
import org.lemonadestand.btb.mvvm.viewmodel.InterestViewModel
import org.lemonadestand.btb.features.event.fragments.WriteTextFragment
import org.lemonadestand.btb.features.common.fragments.CheckListFragment
import org.lemonadestand.btb.features.common.fragments.OptionListFragment
import org.lemonadestand.btb.features.common.fragments.TeamAndContactsFragment
import org.lemonadestand.btb.singleton.Singleton
import java.util.Locale


class InterestFragment : Fragment(), OnItemClickListener, ColorPickerDialogListener {

    private lateinit var mBinding: FragmentInterestBinding
    private val bottomSheetFragmentMessage = TeamAndContactsFragment()
    private var bottomSheetFragmentSingleLine = WriteTextFragment()
    private var optionListFragment = OptionListFragment()
    private var checkListFragment = CheckListFragment()
    private var bottomSheetFragment = WriteTextFragment()
    private var savedUserModel: UserListModel? = null
    private lateinit var viewModel: InterestViewModel
    private var shortAnimationDuration: Int = 0
    private var interestListValue: ArrayList<InterestModel> = ArrayList()
    private var interestUiList: ArrayList<InterestModel> = ArrayList()
    private var interestUiListMain: ArrayList<InterestModel> = ArrayList()
    private lateinit var interestUiAdapter: InterestUiAdapter
    var selectedIndex = 0
    var currentColor= Color.parseColor("#4149b6")

    companion object {

        var currentType = InterestFieldType.Common
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentInterestBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        setUpAdapter()
        setClicks()
        handleSelectedData()
        setUpViewModel()
        return mBinding.root
    }

    private fun setUpAdapter() {
        interestUiAdapter = InterestUiAdapter(interestListValue, requireContext(), interestUiList)
        interestUiAdapter.setOnItemClick(this)
        mBinding.rvInterest.adapter = interestUiAdapter
    }

    private fun setClicks() {
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        mBinding.userCard.setOnClickListener {
            currentType = InterestFieldType.UserSelect
            showBottomSheetMessage()
        }
    }


    private fun handleSelectedData() {
        val user = Utils.getInterestUser(context)
        if (user != null) {
            savedUserModel = user
            setSelectUserData(
                user.picture,
                user.name
            )
        } else {
            setSelectUserData(
                Utils.getData(context, Utils.PICTURE),
                Utils.getData(context, Utils.USER_NAME)
            )
        }
    }

    private fun showBottomSheetMessage() {

        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        bottomSheetFragmentMessage.setCallback(this)
        val args = Bundle()
        args.putString("title", "Show Interest For :")
        args.putBoolean("is_event", false)
        bottomSheetFragmentMessage.arguments = args
        bottomSheetFragmentMessage.show(fragmentManager, bottomSheetFragmentMessage.tag)
    }

    override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
        selectedIndex = index
        when (currentType) {
            InterestFieldType.AdapterClicks -> {
                handleAdapterClicks(data = `object` as InterestModel, type = type, index = index)
            }

            InterestFieldType.UserSelect -> {
                val data = `object` as UserListModel
//                Log.e("data=>", data.username)
                bottomSheetFragmentMessage.dismiss()
                setSelectUserData(data.picture, userName = data.name)
                startLoading()
                interestUiList.clear()
                interestUiList.addAll(interestUiListMain)
                interestUiAdapter.notifyDataSetChanged()
                viewModel.getInterestDataList(resource = data.uniqueId)
            }

            InterestFieldType.SingleLine -> {
                val text = `object`.toString()
                interestUiList[index].value = text
                interestUiAdapter.updateData(interestUiList)

                Log.e("hello=>", interestUiList[index].id.toString())
                Log.e("hello=>", index.toString())
               val body = UpdateInterestBody(fields = arrayListOf(Field(text)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())

            }

            InterestFieldType.DropdownSelect -> {
                optionListFragment.dismiss()
                val text = `object` as Option
                interestUiList[index].value = text.value
                interestUiAdapter.updateData(interestUiList)
                val body = UpdateInterestBody(fields = arrayListOf(Field(text.value)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            InterestFieldType.Radio -> {
                optionListFragment.dismiss()
                val text = `object` as Option
                interestUiList[index].value = text.value
                interestUiAdapter.updateData(interestUiList)
                val body = UpdateInterestBody(fields = arrayListOf(Field(text.value)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            InterestFieldType.Checkbox -> {
                checkListFragment.dismiss()
                val selectedList = `object` as ArrayList<Option>
                val optionList = interestUiList[index].options as ArrayList<Option>
                for (i in 0 until optionList.size) {
                    for (j in 0 until selectedList.size) {
                        if (selectedList[j].isCheck) {
                            if (selectedList[j].value == optionList[i].value) {
                                optionList[i].isCheck = true
                            }
                        }
                    }
                }

                interestUiList[index].options = optionList
                val commaSeparatedString = selectedList.joinToString(", ") { it.value }
                interestUiList[index].value = commaSeparatedString
                interestUiAdapter.updateData(interestUiList)
                val body = UpdateInterestBody(fields = arrayListOf(Field(commaSeparatedString)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            else -> {}
        }


    }


    private fun setSelectUserData(image: String?, userName: String) {
        if (image != null) {
            Glide.with(requireContext()).load(image).into(mBinding.ivImage)
        } else {
            val imageI = userName.lowercase(Locale.ROOT).getImageUrlFromName()
            Glide.with(requireContext()).load(imageI).into(mBinding.ivImage)
        }
    }

    private fun setUpViewModel() {
        startLoading()
        val repository = InterestRepository()
        val viewModelProviderFactory =
            CommonViewModelFactory((context as DashboardActivity).application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[InterestViewModel::class.java]
        viewModel.getInterestDataUiList()

        viewModel.interestResponseModelUi.observe(viewLifecycleOwner) {
            if (it.data.isNotEmpty()) {
                interestUiList.clear()
                Log.e("apiData=>", it.data.toString())
                interestUiList.addAll(it.data)
                interestUiListMain.addAll(it.data)
                interestUiAdapter.notifyDataSetChanged()
                getInterestValues()
            } else {
                stopLoading()
            }
        }
        viewModel.interestResponseModel.observe(viewLifecycleOwner) {
            stopLoading()

            if (it.data.isNullOrEmpty()) {
                return@observe
            }

            interestListValue.clear()
            interestListValue.addAll(it.data)

            /*  for (i in 0 until  interestUiList.size)
              {
                  for (j in 0 until interestListValue.size)
                  {
                     // Log.e("true==>","1=>"+ interestUiList[i].fieldID.toString())
                      Log.e("true==>", interestListValue[j].field_id.toString())
                      if (interestUiList[i].field_id == interestListValue[j].field_id)
                      {
                          interestUiList[i].value = interestListValue[j].value
                      }
                  }
              }*/
            updateUIList()


            interestUiAdapter.notifyDataSetChanged()
        }


        viewModel.liveError.observe(viewLifecycleOwner) {
            Singleton.handleResponse(response = it, context as Activity, "interestFragment")
            ProgressDialogUtil.dismissProgressDialog()
        }

        viewModel.commonResponse.observe(viewLifecycleOwner) {
            handleCommonResponse(context as DashboardActivity, it)
            ProgressDialogUtil.dismissProgressDialog()
            //if (it.status == Singleton.SUCCESS) { }

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

    private fun updateUIList() {

        for (interestUi in interestUiList) {
            val matchingInterest = interestListValue.firstOrNull { it.field_id == interestUi.field_id }
            matchingInterest?.let {
                interestUiList[interestUiList.indexOf(interestUi)] = it
            }
        }
    }

    private fun getInterestValues() {
        val resource =
            if (savedUserModel == null) Utils.getUser(context).uniqueId else savedUserModel!!.uniqueId
        viewModel.getInterestDataList(resource = resource)
    }

    private fun startLoading() {
        mBinding.shimmerLayout.startShimmer()
        mBinding.rvInterest.hide()

        mBinding.shimmerLayout.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(0)
                .setListener(null)
        }
        mBinding.shimmerLayout.startShimmer()


    }

    private fun stopLoading() {
        val view = mBinding.rvInterest
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

    private fun showBottomSheetLine(
        text: String,
        title: String,
        isSingleLine: Boolean,
        index: Int
    ) {
        currentType = InterestFieldType.SingleLine
        val fragmentManager: FragmentManager = childFragmentManager
        bottomSheetFragmentSingleLine = WriteTextFragment()
        bottomSheetFragmentSingleLine.setCallback(this)
        val args = Bundle()
        args.putString("message", text)
        args.putString("title", title)
        args.putInt("index", index)
        args.putBoolean("is_single_line", isSingleLine)
        bottomSheetFragmentSingleLine.arguments = args
        bottomSheetFragmentSingleLine.show(fragmentManager, bottomSheetFragmentSingleLine.tag)
    }

    private fun showBottomDropdownList(
        data: InterestModel,
        index: Int
    ) {

        currentType = InterestFieldType.DropdownSelect
        val optionList: ArrayList<Option> = data.options as ArrayList<Option>

        for (i in 0 until optionList.size) {
            optionList[i].isCheck = false
        }
        for (i in 0 until optionList.size) {
            if (optionList[i].value == data.value) {
                Log.e("optionList=>", optionList[i].value)
                Log.e("optionList=>", data.value + "")
                optionList[i].isCheck = true
            }
        }
        val fragmentManager: FragmentManager = childFragmentManager
        optionListFragment = OptionListFragment()
        optionListFragment.setCallback(this)
        val args = Bundle()
        val json = Gson().toJson(optionList)
        args.putString("list", json)
        args.putString("title", data.label)
        args.putInt("index", index)
        optionListFragment.arguments = args
        optionListFragment.show(fragmentManager, optionListFragment.tag)
    }

    private fun showCheckList(
        data: InterestModel,
        index: Int
    ) {

        currentType = InterestFieldType.Checkbox
        val optionList: ArrayList<Option> = data.options as ArrayList<Option>



        for (i in 0 until optionList.size) {


            if (optionList[i].value == data.value) {
                Log.e("optionList=>", optionList[i].value)
                Log.e("optionList=>", data.value + "")
                optionList[i].isCheck = true
            }
        }
        val fragmentManager: FragmentManager = childFragmentManager
        checkListFragment = CheckListFragment()
        checkListFragment.setCallback(this)
        val args = Bundle()
        val json = Gson().toJson(optionList)
        args.putString("list", json)
        args.putString("title", data.label)
        args.putInt("index", index)
        checkListFragment.arguments = args
        checkListFragment.show(fragmentManager, checkListFragment.tag)
    }

    private fun handleAdapterClicks(type: ClickType, data: InterestModel, index: Int) {
        if (data.value == null) data.value = ""
        when (type) {

            ClickType.SingleLine -> {
                showBottomSheetLine(data.value!!, data.label, isSingleLine = true, index)
            }

            ClickType.MultiLine -> {
                showBottomSheetLine(data.value!!, data.label, isSingleLine = false, index)
            }

            ClickType.NumberInput -> {
                Log.e("NumberInput==>", data.value!! + "")
                val body = UpdateInterestBody(fields = arrayListOf(Field(data.value!!)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            ClickType.Radio -> {
                showBottomDropdownList(data, index)
            }

            ClickType.Checkbox -> {
                showCheckList(data, index)
            }

            ClickType.DropdownSelect -> {
                showBottomDropdownList(data, index)
            }

            ClickType.DatePicker -> {
                val body = UpdateInterestBody(fields = arrayListOf(Field(data.value!!)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            ClickType.ColorPicker -> {
                val body = UpdateInterestBody(fields = arrayListOf(Field(data.value!!)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            ClickType.PhoneNumber -> {
                val body = UpdateInterestBody(fields = arrayListOf(Field(data.value!!)))
                viewModel.updateField(body = body, fieldId = interestUiList[index].id.toString())
            }

            ClickType.FileUpload -> {

            }


            else -> {}
        }
    }



    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.e("color==>",color.toString())
    }

    override fun onDialogDismissed(dialogId: Int) {
        TODO("Not yet implemented")
    }
}