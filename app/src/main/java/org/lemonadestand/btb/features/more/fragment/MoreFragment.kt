package org.lemonadestand.btb.features.more.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lemonadestand.btb.databinding.FragmentMoreBinding
import org.lemonadestand.btb.features.login.activities.LoginActivity
import org.lemonadestand.btb.utils.Utils

class MoreFragment : Fragment() {

    lateinit var mBinding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMoreBinding.inflate(
            LayoutInflater.from(inflater.context),
            container,
            false
        )
        setClicks()
        return mBinding.root
    }

    private fun setClicks() {
        mBinding.tvLogOut.setOnClickListener {
            Utils.saveData(
                requireActivity(),
                Utils.TOKEN,
                "12"
            )
            val i = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }


}