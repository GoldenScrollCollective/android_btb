package org.lemonadestand.btb.components.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity(@LayoutRes val layoutId: Int): AppCompatActivity() {
    companion object {
        val TAG = this::class.java.name
    }

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        var granted = true
        for (entry in results.entries.iterator()) {
            if (!entry.value) {
                granted = false
                break
            }
        }
        onRequestPermissionCompleted?.invoke(granted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        resolveDisplayMetrics()
        setContentView(layoutId)
        resolveDisplayMetrics()

        init()
        initNavigation()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        resolveDisplayMetrics()
        super.onConfigurationChanged(newConfig)

    }

    override fun onResume() {
        super.onResume()
        update()
    }

    protected open fun init() {}
    protected open fun initNavigation() {}

    open fun update() {}

    fun replaceFragment(containerId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(containerId, fragment).commit()
    }

    fun startActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    var onRequestPermissionCompleted: ((value: Boolean) -> Unit)? = null
    fun requestPermissions(permissions: Array<String>, callback: ((value: Boolean) -> Unit)?) {
        onRequestPermissionCompleted = callback
        requestPermissionLauncher.launch(permissions)
    }

    private fun resolveDisplayMetrics() {
//		resolveDisplayMetrics(this)
//		resolveDisplayMetrics(applicationContext)
//		resolveDisplayMetrics(baseContext)
//		resolveDisplayMetrics(App.instance)
    }

    private fun resolveDisplayMetrics(context: Context) {
        try {
            val configuration = Configuration(context.resources.configuration)
            configuration.fontScale = 1.toFloat() //0.85 small size, 1 normal size, 1.15 big etc
            val metrics = context.resources.displayMetrics
            metrics.scaledDensity = configuration.fontScale * metrics.density
            configuration.densityDpi = resources.displayMetrics.xdpi.toInt()
            context.resources.updateConfiguration(configuration, metrics)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}