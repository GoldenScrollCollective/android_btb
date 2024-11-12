package org.lemonadestand.btb.components.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(
    @LayoutRes val layoutResId: Int
) : Fragment() {
    companion object {
        val TAG: String = this::class.java.name
    }

    protected lateinit var rootView: View
    protected val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(layoutResId, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        resolveArguments()
    }

    override fun onResume() {
        super.onResume()
        applyAppInfo()
        update()
    }

    protected open fun resolveArguments() {}
    protected open fun init() {}

    open fun update() {}

    protected open fun applyAppInfo() {}
    fun startActivity(cls: Class<*>) {
        val intent = Intent(requireActivity(), cls)
        requireActivity().startActivity(intent)
    }

    fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace((rootView.parent as ViewGroup).id, fragment).commit()
    }

    fun addFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().add((rootView.parent as ViewGroup).id, fragment).commit()
    }

    fun goBack() {
        navController.navigateUp()
    }

    fun requestPermissions(permissions: Array<String>, callback: ((value: Boolean) -> Unit)?) {
        (requireActivity() as BaseActivity).onRequestPermissionCompleted = callback
        (requireActivity() as BaseActivity).requestPermissionLauncher.launch(permissions)
    }
}