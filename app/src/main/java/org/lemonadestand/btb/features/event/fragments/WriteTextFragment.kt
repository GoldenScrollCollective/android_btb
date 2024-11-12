package org.lemonadestand.btb.features.event.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.lemonadestand.btb.databinding.FragmentWriteTextBinding
import org.lemonadestand.btb.interfaces.OnItemClickListener


class WriteTextFragment : BottomSheetDialogFragment() {

    private var callback: OnItemClickListener? = null
    lateinit var mBinding: FragmentWriteTextBinding
    private var isSingleLine = false
    var index = -1

    var onChange: ((value: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentWriteTextBinding.inflate(
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
            onChange?.invoke(mBinding.htmlEditor.text.toString().trim())
            callback?.onItemClicked(mBinding.htmlEditor.text.toString().trim(), index)
        }
    }


    private fun setContent() {
        val args = arguments
        if (args != null) {
            val message = args.getString("message")
            if(args.containsKey("title")) {
                mBinding.titleView.text = args.getString("title")
            }
            if(args.containsKey("is_single_line")) {
                isSingleLine = args.getBoolean("is_single_line",false)
            }
            if(args.containsKey("index")) {
                index = args.getInt("index",-1)
            }
            mBinding.htmlEditor.setText(message)

            if(isSingleLine)
            {
                mBinding.htmlEditor.isSingleLine = isSingleLine
            }
        }

    }

    fun setCallback(callback: OnItemClickListener) {
        this.callback = callback
    }

}