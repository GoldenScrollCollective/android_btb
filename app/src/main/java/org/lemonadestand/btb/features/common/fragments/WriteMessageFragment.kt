package org.lemonadestand.btb.features.common.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.lemonadestand.btb.databinding.FragmentWriteMessageBinding
import org.lemonadestand.btb.interfaces.OnItemClickListener


class WriteMessageFragment : BottomSheetDialogFragment() {

    private lateinit var callback: OnItemClickListener
    lateinit var mBinding: FragmentWriteMessageBinding
    private var isSingleLine  = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWriteMessageBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )

        setContent()
        setClickEvents()
        return mBinding.root
    }

    private fun setClickEvents() {
        mBinding.tvDone.setOnClickListener {
            dismiss()
            callback.onItemClicked(mBinding.htmlEditor.text.toString().trim(),-1)
        }


    }


    private fun setContent() {
        val args = arguments
        if(args!=null)
        {
            val message = args.getString("message")
            if(args.containsKey("title")) {
                mBinding.tvTitle.text = args.getString("title")
            }
            if(args.containsKey("is_single_line")) {
                isSingleLine = args.getBoolean("is_single_line",false)
            }
            mBinding.htmlEditor.text = message

            if(isSingleLine)
            {
                mBinding.htmlEditor
            }
        }

    }

    fun setCallback(callback : OnItemClickListener)
    {
        this.callback = callback
    }

}