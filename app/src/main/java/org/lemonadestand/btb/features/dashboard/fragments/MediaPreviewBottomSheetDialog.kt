package org.lemonadestand.btb.features.dashboard.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.MediaView
import org.lemonadestand.btb.components.base.BaseBottomSheetDialogFragment
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.features.post.models.Post


class MediaPreviewBottomSheetDialog @JvmOverloads constructor(
    val post: Post,
): BaseBottomSheetDialogFragment(R.layout.fragment_media_preview_sheet) {

    private lateinit var mediaView: MediaView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { it ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun init() {
        super.init()

        mediaView = rootView.findViewById(R.id.mediaView)
        mediaView.post = post

        val btnClose = rootView.findViewById<RelativeLayout>(R.id.btnClose)
        btnClose.setOnClickListener {
            dismiss()
        }

        rootView.post {
            val layoutParams = rootView.layoutParams
            layoutParams.height = resources.displayMetrics.heightPixels
            rootView.layoutParams = layoutParams
        }
    }


}