package org.lemonadestand.btb.features.common.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseBottomSheetDialogFragment
import org.lemonadestand.btb.components.radio.RadioGroup
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.interest.fragments.SelectContactFragment
import org.lemonadestand.btb.features.interest.fragments.SelectTeamFragment


class TeamAndContactsFragment : BaseBottomSheetDialogFragment(R.layout.fragment_team_and_contacts) {

	private var isEvent: Boolean = false
	private var currentFragment: Fragment? = null
	private var tabIndex: Int = 0
		set(value) {
			field = value
			handleTabIndex()
		}
	var onSelect: ((value: UserListModel) -> Unit)? = null

	override fun init() {
		super.init()

		arguments?.let {
			if (it.containsKey("title")) {
				val titleView = rootView.findViewById<TextView>(R.id.titleView)
				titleView.text = it.getString("title")
			}

			isEvent = it.getBoolean("is_event", false)
		}

		val tabView = rootView.findViewById<RadioGroup>(R.id.tabView)
		tabView.onSelect = { tabIndex = it }
		handleTabIndex()
	}

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

	private fun handleTabIndex() {
		when (tabIndex) {
			0 -> {
				if (currentFragment is SelectTeamFragment) {
					return
				}
				currentFragment = SelectTeamFragment().apply {
					arguments = Bundle().apply {
						putBoolean("is_event", isEvent)
					}
					onSelect = { this@TeamAndContactsFragment.onSelect?.invoke(it) }
				}
				childFragmentManager.beginTransaction()
					.replace(R.id.fragment_container, currentFragment!!)
					.commit()
			}

			1 -> {
				if (currentFragment is SelectContactFragment) {
					return
				}
				currentFragment = SelectContactFragment().apply {
					arguments = Bundle().apply {
						putBoolean("is_event", isEvent)
					}
					onSelect = {
						this@TeamAndContactsFragment.onSelect?.invoke(it)
					}
				}
				childFragmentManager.beginTransaction()
					.replace(R.id.fragment_container, currentFragment!!)
					.commit()
			}
		}
	}
}