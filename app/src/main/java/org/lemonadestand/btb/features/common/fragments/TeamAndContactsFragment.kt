package org.lemonadestand.btb.features.common.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.lemonadestand.btb.R
import org.lemonadestand.btb.databinding.FragmentTeamAndContactsBinding
import org.lemonadestand.btb.features.interest.fragments.ContactFragment
import org.lemonadestand.btb.features.interest.fragments.SelectTeamFragment
import org.lemonadestand.btb.interfaces.OnItemClickListener


class TeamAndContactsFragment : BottomSheetDialogFragment() {

    lateinit var mBinding: FragmentTeamAndContactsBinding

    private lateinit var callback: OnItemClickListener

    private var  isEvent: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentTeamAndContactsBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )

        setTitle()
        setDefaultView()
        setClicks()
        return mBinding.root
    }

    private fun setTitle() {
        val args = arguments
        val title = args!!.getString("title", "")
         isEvent = args.getBoolean("is_event", false)
        mBinding.tvTitle.text = title
    }

    private fun setClicks() {
        mBinding.tvTeam.setOnClickListener { handleTeamClick() }
        mBinding.tvContacts.setOnClickListener { handleContactClick() }
        mBinding.tvTeam.performClick()
    }

    private fun handleContactClick() {
        val fragment =  ContactFragment()
        fragment.setCallback(callback)
        val bundle = Bundle()
        bundle.putBoolean("is_event",isEvent)
        fragment.arguments = bundle
        fragment.setCallback(callback)
        setFragment(fragment)
        setDefaultView()
        mBinding.tvContacts.alpha = 1f
        mBinding.tvContacts.setBackgroundResource(R.drawable.back_for_all)

    }

    private fun setFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun handleTeamClick() {
        val fragment =  SelectTeamFragment()
        val bundle = Bundle()
        bundle.putBoolean("is_event",isEvent)
        fragment.arguments = bundle
        fragment.setCallback(callback)
        setFragment(fragment)
        setDefaultView()
        mBinding.tvTeam.alpha = 1f
        mBinding.tvTeam.setBackgroundResource(R.drawable.back_for_all)
    }

    private fun setDefaultView() {
        mBinding.tvContacts.alpha = 0.7f
        mBinding.tvContacts.setBackgroundResource(0)
        mBinding.tvTeam.alpha = 0.7f
        mBinding.tvTeam.setBackgroundResource(0)
    }
    fun setCallback(callback: OnItemClickListener) {
        this.callback = callback
    }

    /*override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight =
                    0 // Remove this line to hide a dark background if you manually hide the dialog.
            }
        })
    }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                bottomSheet!!
            )
            // Set the height of the bottom sheet to MATCH_PARENT
            bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            //behavior.peekHeight = 0 // Remove this line to hide a dark background if you manually hide the dialog.
        }
        return dialog
    }
}