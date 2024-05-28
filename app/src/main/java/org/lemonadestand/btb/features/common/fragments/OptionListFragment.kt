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
import org.lemonadestand.btb.features.common.adapter.OptionListAdapter
import org.lemonadestand.btb.databinding.FragmentOptionListBinding
import org.lemonadestand.btb.features.interest.models.Option
import org.lemonadestand.btb.interfaces.OnItemClickListener


import java.util.Locale

class OptionListFragment : BottomSheetDialogFragment() {

    lateinit var mBinding: FragmentOptionListBinding
    private lateinit var optionAdapter: OptionListAdapter
    private var shortAnimationDuration: Int = 0
    private var tag: String = "OptionListFragment"


    private lateinit var callback: OnItemClickListener
    var optionArrayList : ArrayList<Option> = ArrayList()
    var optionArrayListTemp : ArrayList<Option> = ArrayList()

    companion object{
        var optionListFragmentIndex = -1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOptionListBinding.inflate(
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
        mBinding.tvCancel.setOnClickListener { dismiss() }
    }

    private fun setAdapter() {

        val bundle = arguments
        if (bundle != null) {
            //val b = bundle.getBundle("list")
            val jsonList = bundle.getString("list")

            if(bundle.containsKey("index")) {
                optionListFragmentIndex = bundle.getInt("index",-1)
            }
            val gson = Gson()
            val listFromGson = gson.fromJson<ArrayList<Any>>(
                jsonList,
                object : TypeToken<ArrayList<Option>>() {}.type
            )

            optionArrayList = listFromGson as ArrayList<Option>

            for (i in 0 until optionArrayList.size)
            {
                Log.e("test==>",optionArrayList[i].isCheck.toString()+"${optionArrayList[i].label}")
            }
            optionArrayListTemp = listFromGson
            shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
            optionAdapter = OptionListAdapter(optionArrayList, context = requireContext())
            Log.e("adapter=>", optionAdapter.toString())
            optionAdapter.setOnItemClick(callback)
            mBinding.rvUserList.adapter = optionAdapter
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

                        optionAdapter.notifyDataSetChanged()
                    } else {
                        optionArrayList.clear()
                        optionArrayList.addAll(optionArrayListTemp)
                        optionAdapter.notifyDataSetChanged()
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

