package org.lemonadestand.btb.features.common.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.lemonadestand.btb.features.common.adapter.CheckListAdapter
import org.lemonadestand.btb.databinding.FragmentCheckListBinding
import org.lemonadestand.btb.features.interest.models.Option
import org.lemonadestand.btb.interfaces.OnItemClickListener

import java.util.Locale

class CheckListFragment : BottomSheetDialogFragment() {

    lateinit var mBinding: FragmentCheckListBinding
    private lateinit var checkAdapter: CheckListAdapter
    private var shortAnimationDuration: Int = 0
    private var tag: String = "OptionListFragment"


    private lateinit var callback: OnItemClickListener
    var optionArrayList: ArrayList<Option> = ArrayList()
    var optionArrayListTemp: ArrayList<Option> = ArrayList()

    companion object {
        var optionListFragmentIndex = -1
        var titleCheck = "Select Options"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCheckListBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        setAdapter()
        setOnClicks()
        setSearch()
        return mBinding.root
    }

    private fun setOnClicks() {
        mBinding.tvDone.setOnClickListener {
            dismiss()
            Log.e("optionListFragmentIndex", optionListFragmentIndex.toString())
            if (checkAdapter.getSelectedList().isNotEmpty()) {
                callback.onItemClicked(
                    checkAdapter.getSelectedList(),
                    optionListFragmentIndex
                )
            }
        }
    }

    private fun setAdapter() {

        val bundle = arguments
        if (bundle != null) {
            //val b = bundle.getBundle("list")
            val jsonList = bundle.getString("list")

            if (bundle.containsKey("index")) {
                optionListFragmentIndex = bundle.getInt("index", -1)
            }

            if (bundle.containsKey("title")) {
                titleCheck = bundle.getString("title", "Select Option")
            }
            val gson = Gson()
            val listFromGson = gson.fromJson<ArrayList<Any>>(
                jsonList,
                object : TypeToken<ArrayList<Option>>() {}.type
            )

            optionArrayList = listFromGson as ArrayList<Option>
            optionArrayListTemp = listFromGson
            shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
            checkAdapter = CheckListAdapter(optionArrayList, context = requireContext())
            checkAdapter.setOnItemClick(callback)
            mBinding.rvUserList.adapter = checkAdapter
            mBinding.tvTitle.text = titleCheck
        }
    }

    private fun setSearch() {
        mBinding.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                var tempList: ArrayList<Option> = ArrayList()
                val query = s.toString().lowercase(Locale.getDefault())
                if (query.isNotEmpty()) {
                    Log.e("dwdhwd", "wdwdwd")
                    Log.e("query=>", query)
                    tempList = optionArrayList.filter {
                        it.label.contains(
                            query,
                            ignoreCase = true
                        )
                    } as ArrayList<Option>
                    Log.e("tempList=>", tempList.toString())
                    optionArrayList.clear()
                    optionArrayList.addAll(tempList)

                    checkAdapter.notifyDataSetChanged()
                } else {
                    optionArrayList.clear()
                    optionArrayList.addAll(optionArrayListTemp)
                    checkAdapter.notifyDataSetChanged()
                }

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {

        }
        bottomSheetDialog.apply {
            // behavior.state = BottomSheetBehavior.STATE_EXPANDED
            //behavior.peekHeight = //your harcoded or dimen height
        }

        return bottomSheetDialog
    }

    fun setCallback(callback: OnItemClickListener) {
        this.callback = callback
    }


}

